package com.sanron.yidumusic.util.android;

import android.content.Context;
import android.telephony.TelephonyManager;

/**
 * Created by sanron on 16-7-14.
 */
public class IMEITool {

    public static String getImei(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String deviceId = telephonyManager.getDeviceId();
        if (deviceId.contains(":")) {
            deviceId = "";
        }
        return deviceId;
    }
}
