package sk.denis.davidek.popularmoviesstage2.utilities;

import android.content.Context;
import android.util.DisplayMetrics;

/**
 * Created by denis on 19.05.2017.
 */

public class LayoutUtils {

    public static int calculateNoOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int noOfColumns = (int) (dpWidth / 180);
        return noOfColumns;
    }
}
