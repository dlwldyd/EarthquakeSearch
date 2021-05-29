package com.jiyong.apps.earthquake;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SearchView;

public class MainActivity extends AppCompatActivity implements EarthquakeListFragment.OnListFragmentInteractionListener {
    private static final String TAG_LIST_FRAGMENT="TAG_LIST_FRAGMENT";
    public static final int MENU_PREFERENCES= Menu.FIRST+1;
    private static final int SHOW_PREFERENCES=1;
    EarthquakeListFragment mEarthquakeListFragment;
    EarthquakeViewModel earthquakeViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FragmentManager fm=getSupportFragmentManager();

        if(savedInstanceState==null){
            FragmentTransaction ft=fm.beginTransaction();
            mEarthquakeListFragment=new EarthquakeListFragment();
            ft.add(R.id.main_activity_relativeLayout, mEarthquakeListFragment, TAG_LIST_FRAGMENT);
            ft.commitNow();
        }
        else{
            mEarthquakeListFragment=(EarthquakeListFragment)fm.findFragmentByTag(TAG_LIST_FRAGMENT);
        }

        earthquakeViewModel=new ViewModelProvider(this).get(EarthquakeViewModel.class);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        SearchManager searchManager=(SearchManager)getSystemService(Context.SEARCH_SERVICE);
        SearchableInfo searchableInfo=searchManager.getSearchableInfo(new ComponentName(getApplicationContext(), EarthquakeSearchResultActivity.class));
        SearchView searchView=(SearchView)menu.findItem(R.id.search_view).getActionView();
        searchView.setSearchableInfo(searchableInfo);
        searchView.setIconifiedByDefault(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId()==R.id.settings_menu_item){
            Intent intent=new Intent(this, PrefActivity.class);
            startActivity(intent);
            return true;
        }
        return false;
    }

    @Override
    public void onListFragmentRefreshRequested() {
        updateEarthquakes();
    }
    private void updateEarthquakes(){
        earthquakeViewModel.loadEarthquakes();
    }
}