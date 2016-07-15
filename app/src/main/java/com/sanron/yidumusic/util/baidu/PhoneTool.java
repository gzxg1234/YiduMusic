package com.sanron.yidumusic.util.baidu;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;

/**************************************
 * FileName : com.sanron.yidumusic.util.baidu
 * Author : Administrator
 * Time : 2016/7/15 15:58
 * Description :
 **************************************/
public class PhoneTool {

    public static final int IMSI_ERROR = -1;
    public static final int IMSI_CHINA_MOBILE = 0;//移动
    public static final int IMSI_CHINA_UNICOM = 1;//联通
    public static final int IMSI_CHINA_TELECOM = 2;//电信


    //获取IMSI国际移动用户识别码
    public static int getIMSI(Context context) {
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            String operator = tm.getSimOperator();
            if (operator != null) {
                if ("46000".equals(operator) || "46002".equals(operator) || "46007".equals(operator)) {
                    return IMSI_CHINA_MOBILE;
                } else if ("46001".equals(operator)) {
                    return IMSI_CHINA_UNICOM;
                } else if ("46003".equals(operator)) {
                    return IMSI_CHINA_TELECOM;
                }
            }
        } catch (Exception e) {
            Log.e("IMSI", "get imsi failed");
        }
        return IMSI_ERROR;
    }
}
