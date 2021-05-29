package com.jiyong.apps.earthquake;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

public class EarthquakeListFragment extends Fragment {
    private final ArrayList<Earthquake> mEarthquakes=new ArrayList<>();
    private RecyclerView mRecyclerView;
    private final EarthquakeRecyclerViewAdapter mEarthquakeAdapter= new EarthquakeRecyclerViewAdapter(mEarthquakes);
    protected EarthquakeViewModel earthquakeViewModel;
    private SwipeRefreshLayout swipeRefresh;
    private OnListFragmentInteractionListener mListener;
    private int mMinimumMagnitude=0;
    private SharedPreferences.OnSharedPreferenceChangeListener mPrefListener=new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if(PrefActivity.PREF_MIN_MAG.equals(key)){
                List<Earthquake> earthquakes=earthquakeViewModel.getEarthquakes().getValue();
                if(earthquakes!=null){
                    setEarthquakes(earthquakes);
                }
            }
        }
    };
    public EarthquakeListFragment(){
    }
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view=inflater.inflate(R.layout.fragment_earthquake_list, container, false);
        mRecyclerView = view.findViewById(R.id.list);
        swipeRefresh=view.findViewById(R.id.swipeRefresh);
        return view;
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        Context context=view.getContext();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        mRecyclerView.setAdapter(mEarthquakeAdapter);
        //끌어서 새로고침, 뷰 모델의 데이터를 가져와서 변경내용이 있으면 지진 리스트 변경
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateEarthquakes();
            }
        });
        earthquakeViewModel=new ViewModelProvider(getActivity()).get(EarthquakeViewModel.class);
        earthquakeViewModel.getEarthquakes().observe(getViewLifecycleOwner(), new Observer<List<Earthquake>>() {
            @Override
            public void onChanged(List<Earthquake> earthquakes) {
                if(earthquakes!=null){
                    setEarthquakes(earthquakes);
                }
            }

        });
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(getContext());
        prefs.registerOnSharedPreferenceChangeListener(mPrefListener);
    }
    //최소 진도 필터링, 프레퍼런스 리스너에서 콜
    public void setEarthquakes(List<Earthquake> earthquakes){
        updateFromPreferences();
        for(Earthquake earthquake: earthquakes){
            if(earthquake.getMagnitude()>=mMinimumMagnitude) {
                if (!mEarthquakes.contains(earthquake)) {
                    mEarthquakes.add(earthquake);
                    mEarthquakeAdapter.notifyItemInserted(mEarthquakes.indexOf(earthquake));
                }
            }
        }
        if(mEarthquakes.size()>0){
            for (int i = mEarthquakes.size()-1; i >=0 ; i--) {
                if(mEarthquakes.get(i).getMagnitude()<mMinimumMagnitude){
                    mEarthquakes.remove(i);
                    mEarthquakeAdapter.notifyItemRemoved(i);
                }
            }
        }
        swipeRefresh.setRefreshing(false);
    }
    public interface OnListFragmentInteractionListener{
        void onListFragmentRefreshRequested();
    }
    @Override
    public void onAttach(Context context) {

        super.onAttach(context);
        mListener=(OnListFragmentInteractionListener)context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener=null;
    }

    protected void updateEarthquakes(){
        if(mListener!=null) {
            mListener.onListFragmentRefreshRequested();
        }
    }
    private void updateFromPreferences(){
        SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(getContext());
        mMinimumMagnitude=Integer.parseInt(prefs.getString(PrefActivity.PREF_MIN_MAG, "3"));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }
}