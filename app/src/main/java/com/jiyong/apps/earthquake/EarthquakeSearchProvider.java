package com.jiyong.apps.earthquake;


import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class EarthquakeSearchProvider extends ContentProvider {
    private static final int SEARCH_SUGGESTIONS=1;
    @Override
    public boolean onCreate() {
        EarthquakeDatabaseAccessor.getInstance(getContext().getApplicationContext());
        return true;
    }
//room 데이터베이스에 쿼리
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        if(urimatcher.match(uri)==SEARCH_SUGGESTIONS){
            String searchQuery="%"+uri.getLastPathSegment()+"%";
            EarthquakeDAO earthquakeDAO=EarthquakeDatabaseAccessor.getInstance(getContext().getApplicationContext()).earthquakeDAO();
            Cursor c=earthquakeDAO.generateSearchSuggestions(searchQuery);
            return c;
        }
        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        if (urimatcher.match(uri) == SEARCH_SUGGESTIONS) {
            return SearchManager.SUGGEST_MIME_TYPE;
        }
        throw new IllegalArgumentException("Unsupported URI:" + uri);
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
    private static final UriMatcher urimatcher;
    static {
        urimatcher=new UriMatcher(UriMatcher.NO_MATCH);
        urimatcher.addURI("com.jiyong.provider.earthquake", SearchManager.SUGGEST_URI_PATH_QUERY, SEARCH_SUGGESTIONS);
        urimatcher.addURI("com.jiyong.provider.earthquake", SearchManager.SUGGEST_URI_PATH_QUERY+"/*", SEARCH_SUGGESTIONS);
        urimatcher.addURI("com.jiyong.provider.earthquake", SearchManager.SUGGEST_URI_PATH_SHORTCUT, SEARCH_SUGGESTIONS);
        urimatcher.addURI("com.jiyong.provider.earthquake", SearchManager.SUGGEST_URI_PATH_SHORTCUT+"/*", SEARCH_SUGGESTIONS);
    }
}
