package sk.denis.davidek.popularmoviesstage2.utilities;

import android.content.SharedPreferences;

/**
 * Created by denis on 21.05.2017.
 */

public class GlobalUtils {

    public static void setCurrentFilterSetting(SharedPreferences preferences, String key, String value) {

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.apply();

    }
}
