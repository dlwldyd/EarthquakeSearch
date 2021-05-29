package com.jiyong.apps.earthquake;


import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;

//설정 액티비티
public class PrefActivity extends AppCompatActivity {
    public static final String PREF_MIN_MAG="PREF_MIN_MAG";
//    public static final String PREF_AUTO_UPDATE="PREF_AUTO_UPDATE";
//    public static final String USER_PREFERENCE="USER_PREFERENCE";
//    public static final String PREF_UPDATE_FREQ="PREF_UPDATE_FREQ";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preferences);
    }
    public static class PrefFragment extends PreferenceFragmentCompat {

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.user_preferences, null);
        }
    }
}