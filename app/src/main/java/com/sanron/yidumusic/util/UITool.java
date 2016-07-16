package com.sanron.yidumusic.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.TypedValue;

/**
 * Created by sanron on 16-7-16.
 */
public class UITool {


    public static Drawable getTintDrawable(Context context, int resId, int tint) {
        Drawable drawable = context.getResources().getDrawable(resId);
        if (drawable != null) {
            DrawableCompat.setTint(drawable, tint);
        }
        return drawable;
    }

    public static int dpToPx(Context context, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                context.getResources().getDisplayMetrics());
    }
}
