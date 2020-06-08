package com.mcz.light_appproject.app.utils;

import android.text.TextUtils;

public class Data_untils {

    /*
     * 计算当前最大值
     * parameter target    输入目标值
     * parameter max       现有的最大值
     * return   max        经过比较之后的最大值
     * */
    public static float record_max(float target,float max )
    {
        if(max==0)
        {
            max=target;
        }
        else
        {
            if(max<target)
            {
                max=target;
            }
        }
        return max;
    }
    /*
     * 计算当前最小值
     * parameter target    输入目标值
     * parameter max       现有的最小值
     * return   max        经过比较之后的最小值
     * */
    public static float record_min(float target ,float min)
    {
        if(min==0)
        {
            min=target;
        }
        else
        {
            if(min>target)
            {
                min=target;
            }
        }
        return min;
    }
    public static String bytes2HexString(byte[] b) {
        String r = "";

        for (int i = 0; i < b.length; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            r += hex.toUpperCase();
        }

        return r;
    }
    public static float convertToFloat(String number, float defaultValue) {
        if (TextUtils.isEmpty(number)) {
            return defaultValue;
        }
        try {
            return Float.parseFloat(number);
        } catch (Exception e) {
            return defaultValue;
        }

    }

    //把String转化为double
    public static double convertToDouble(String number, double defaultValue) {
        if (TextUtils.isEmpty(number)) {
            return defaultValue;
        }
        try {
            return Double.parseDouble(number);
        } catch (Exception e) {
            return defaultValue;
        }

    }

    //把String转化为int
    public static int convertToInt(String number, int defaultValue) {
        if (TextUtils.isEmpty(number)) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(number);
        } catch (Exception e) {
            return defaultValue;
        }
    }

}
