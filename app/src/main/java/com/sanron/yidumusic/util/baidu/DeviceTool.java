package com.sanron.yidumusic.util.baidu;

import android.content.Context;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import java.util.UUID;

/**************************************
 * FileName : PACKAGE_NAME
 * Author : Administrator
 * Time : 2016/7/15 10:36
 * Description :
 **************************************/
public class DeviceTool {

    public static String getDeviceId(Context context) {
        IMEIInfo imei = getIMEIInfo(context);
        String androidId = getAndroidId(context);
        if (imei.isCanAccessSystemSetting()) {
            //如果可以读写系统设置
            //获取保存在系统设置里的deviceid
            String baiduDeviceId = Settings.System.getString(context.getContentResolver(), "com.baidu.deviceid");
            if (TextUtils.isEmpty(baiduDeviceId)) {
                StringBuilder sb = new StringBuilder()
                        .append("com.baidu")
                        .append(imei)
                        .append(androidId);
                String str1 = MD5Util.toMd5(sb.toString().getBytes(), true);
                String str2 = Settings.System.getString(context.getContentResolver(), str1);
                if (TextUtils.isEmpty(str2)) {
                    String uuid = UUID.randomUUID().toString();
                    StringBuilder sb2 = new StringBuilder()
                            .append(imei)
                            .append(androidId)
                            .append(uuid);
                    String str4 = MD5Util.toMd5(sb2.toString().getBytes(), true);
                    Settings.System.putString(context.getContentResolver(), str1, str4);
                    Settings.System.putString(context.getContentResolver(), "com.baidu.deviceid", str4);
                    return str4;
                } else {
                    Settings.System.putString(context.getContentResolver(), "com.baidu.deviceid", str2);
                    return str2;
                }
            } else {
                return baiduDeviceId;
            }
        } else {
            return MD5Util.toMd5(("com.baidu" + androidId).getBytes(), true);
        }
    }

    public static String getAndroidId(Context context) {
        String androidId = Settings.Secure.getString(context.getContentResolver(), "android_id");
        if (TextUtils.isEmpty(androidId))
            androidId = "";
        return androidId;
    }

    public static String imeiCheck(String imei) {
        if (imei != null
                && imei.contains(":")) {
            return "";
        }
        return imei;
    }

    /**
     * 获取本机IMEI
     *
     * @param context
     * @param def
     * @return
     */
    public static String getIMEI(Context context, String def) {
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            String imei = tm.getDeviceId();
            imei = imeiCheck(imei);
            if (!TextUtils.isEmpty(imei)) {
                return imei;
            }
        } catch (Exception e) {
            Log.e("DeviceId", "can't read imei");
        }
        return def;
    }

    public static IMEIInfo getIMEIInfo(Context context) {
        String imei = "";
        boolean canAccessSystemSetting = true;
        try {
            imei = Settings.System.getString(context.getContentResolver(), "bd_setting_i");
            if (TextUtils.isEmpty(imei)) {
                imei = getIMEI(context, "");
                Settings.System.putString(context.getContentResolver(), "bd_setting_i", imei);
            }
        } catch (Exception e) {
            Log.e("DeviceId", "can't read or write systemsetting");
            canAccessSystemSetting = false;
        }
        return new IMEIInfo(imei, canAccessSystemSetting);
    }

    public static class IMEIInfo {
        private String mImei;
        private boolean mCanAccessSystemSetting;

        public IMEIInfo(String mImei, boolean mCanAccessSystemSetting) {
            this.mImei = mImei;
            this.mCanAccessSystemSetting = mCanAccessSystemSetting;
        }

        public boolean isCanAccessSystemSetting() {
            return mCanAccessSystemSetting;
        }

        public void setCanAccessSystemSetting(boolean canAccessSystemSetting) {
            mCanAccessSystemSetting = canAccessSystemSetting;
        }
    }
}
