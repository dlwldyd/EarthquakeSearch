package com.jiyong.apps.earthquake;

import android.app.SearchManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class EarthquakeSearchResultActivity extends AppCompatActivity{
    private ArrayList<Earthquake> mEarthquakes=new ArrayList<>();
    private EarthquakeRecyclerViewAdapter mEarthquakeAdapter=new EarthquakeRecyclerViewAdapter(mEarthquakes);
    MutableLiveData<String> searchQuery;
    LiveData<List<Earthquake>> searchResults;
    MutableLiveData<String> selectedSearchSuggestionId;
    LiveData<Earthquake> selectedSearchSuggestion;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_earthquake_search_result);
        RecyclerView recyclerView=findViewById(R.id.search_result_list);
        recyclerView.setAdapter(mEarthquakeAdapter);
        searchQuery=new MutableLiveData<>();
        searchQuery.setValue(null);
        //선택된 검색제안의 id가 변경되면 쿼리 수행 후 해당 지진 데이터를 반환하는 라이브 데이터를 변경하도록 변환 map 구성
        searchResults= Transformations.switchMap(searchQuery, query->EarthquakeDatabaseAccessor.getInstance(getApplicationContext()).earthquakeDAO().searchEarthquakes("%"+query+"%"));
        searchResults.observe(EarthquakeSearchResultActivity.this, searchQueryResultObserver);
        selectedSearchSuggestionId=new MutableLiveData<>();
        selectedSearchSuggestionId.setValue(null);
        selectedSearchSuggestion=Transformations.switchMap(selectedSearchSuggestionId, id->EarthquakeDatabaseAccessor.getInstance(getApplicationContext()).earthquakeDAO().getEarthquake(id));
        //액티비티가 검색 제안에 따라 시작할때
        if (Intent.ACTION_VIEW.equals(getIntent().getAction())){
            selectedSearchSuggestion.observe(this, selectedSearchSuggestionObserver);
            setSelectedSearchSuggestion(getIntent().getData());
        }
        //검색 쿼리로부터 시작할때
        else{
            String query=getIntent().getStringExtra(SearchManager.QUERY);
            setSearchQuery(query);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        if(Intent.ACTION_VIEW.equals(getIntent().getAction())){
            setSelectedSearchSuggestion(getIntent().getData());
        }
        else {
            String query = getIntent().getStringExtra(SearchManager.QUERY);
            setSearchQuery(query);
        }
    }

    private void setSearchQuery(String query){
        searchQuery.setValue(query);
    }
    private final Observer<List<Earthquake>> searchQueryResultObserver= updatedEarthquakes->{
        mEarthquakes.clear();
        if (updatedEarthquakes!=null){
            mEarthquakes.addAll(updatedEarthquakes);
        }
        mEarthquakeAdapter.notifyDataSetChanged();
    };
    private void setSelectedSearchSuggestion(Uri dataString){
        String id=dataString.getPathSegments().get(1);
        selectedSearchSuggestionId.setValue(id);
    }
    final Observer<Earthquake> selectedSearchSuggestionObserver=selectedSearchSuggestion->{
        if (selectedSearchSuggestion!=null){
            //선택된 검색 제안에 일치하도록 검색 쿼리 변경
            setSearchQuery(selectedSearchSuggestion.getDetails());
        }
    };
}