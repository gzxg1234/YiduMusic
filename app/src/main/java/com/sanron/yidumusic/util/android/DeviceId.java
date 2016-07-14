package com.sanron.yidumusic.util.android;

import android.content.Context;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.sanron.yidumusic.util.MD5Util;

import java.util.UUID;

/**
 * Created by sanron on 16-7-14.
 */
public class DeviceId {

    public static String getDeviceId(Context context) {
//        String imei = getImei(context);
//        String androidId = getAndroidId(context);
//        if (CAN_READ_AND_WRITE_SYSTEM_SETTINGS) {
//            String deviceid = Settings.System.getString(context.getContentResolver(), "com.baidu.deviceid");
//            if (TextUtils.isEmpty(deviceid)) {
//                StringBuilder sb = new StringBuilder("com.baidu")
//                        .append(imei)
//                        .append(androidId);
//                String md5 = MD5Util.toMd5(sb.toString().getBytes(), true);
//                String s = Settings.System.getString(context.getContentResolver(), md5);
//                if (TextUtils.isEmpty(s)) {
//                    String uuid = UUID.randomUUID().toString();
//                    StringBuilder x = new StringBuilder()
//                            .append(deviceid)
//                            .append(androidId)
//                            .append(uuid);
//                    return  MD5Util.toMd5(x.toString().getBytes(), true);
//                }
//            } else {
//                return deviceid;
//            }
//        } else {
//            return MD5Util.toMd5(("com.baidu" + androidId).getBytes(), true);
//        }

    }

    public static String getAndroidId(Context context) {
        String androidId = "";
        try {
            androidId = Settings.System.getString(context.getContentResolver(), "android_id");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return androidId;
    }

    public static String getImei(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String deviceId = telephonyManager.getDeviceId();
        if (deviceId.contains(":")) {
            deviceId = "";
        }
        return deviceId;
    }
}
