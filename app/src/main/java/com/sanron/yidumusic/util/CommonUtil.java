package com.sanron.yidumusic.util;

import android.content.Context;
import android.util.TypedValue;

/**
 * Created by sanron on 16-7-13.
 */
public class CommonUtil {

    public static int dpToPx(Context context, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                context.getResources().getDisplayMetrics());
    }
}
