package com.sanron.yidumusic.util;

import android.app.Activity;
import android.content.res.Resources;
import android.view.View;
import android.view.WindowManager;

/**
 * Created by sanron on 16-7-14.
 */
public class StatusBarUtil {

    public static void applyInsertTop(Activity activity, View view) {
        if ((activity.getWindow().getAttributes().flags
                & WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                == 0) {
            return;
        }
        int statusBarHeight = getStatusBarHeight();
        view.setPadding(view.getPaddingLeft(), view.getPaddingTop() + statusBarHeight,
                view.getPaddingRight(), view.getPaddingBottom());
    }

    public static int getStatusBarHeight() {
        int id = Resources.getSystem().getIdentifier("status_bar_height", "dimen", "android");
        if (id != 0) {
            return Resources.getSystem()
                    .getDimensionPixelSize(id);
        }
        return 0;
    }
}
