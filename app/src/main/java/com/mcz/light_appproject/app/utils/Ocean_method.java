package com.mcz.light_appproject.app.utils;

import android.content.Context;
import android.util.Log;

import com.mcz.light_appproject.MainActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.ContentHandler;

public class Ocean_method {
    private String login_appid;
    private String token;

    public Ocean_method(String login_appid, String token) {
        this.login_appid = login_appid;
        this.token = token;
    }

    public boolean GET_Led_staus(Context mycontext, String dvid) {
        boolean led_s=false;
        try {
            String add_url = Config.all_url + "/iocm/app/cmd/v1.4.0/deviceCommands?&pageNo=0&pageSize=100"+"&deviceId="+dvid;
            String json = DataManager.Dv_get_Cammand(mycontext, add_url, login_appid, token);
            JSONObject jsonobject_command = new JSONObject(json);
            JSONArray jsonArray_data=jsonobject_command.getJSONArray("data");
            Log.i("Light_State","====="+jsonArray_data);
            for (int i = 0; i < jsonArray_data.length(); i++) {
                String status=jsonArray_data.getJSONObject(i).optString("status");
                if(status.equals("SUCCESSFUL"))
                {
                    Log.i("Light_State","=========================");
                    JSONObject rs_obeject= new JSONObject(jsonArray_data.getJSONObject(i).optString("result"));
                    Log.i("Light_State","====="+rs_obeject.optString("Light_State"));
                    if(rs_obeject.optInt("Light_State")== 1)
                    {
                        // TODO 这里是灯表示开
                        led_s=true ;

                    }
                    return led_s;
                }
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean GET_Motor_staus(Context mycontext, String dvid) {
        boolean motor_s=false;
        try {
            String add_url = Config.all_url + "/iocm/app/cmd/v1.4.0/deviceCommands?&pageNo=0&pageSize=100"+"&deviceId="+dvid;
            String json = DataManager.Dv_get_Cammand(mycontext, add_url, login_appid, token);
            JSONObject jsonobject_command = new JSONObject(json);
            JSONArray jsonArray_data=jsonobject_command.getJSONArray("data");
            Log.i("Motor_State","====="+jsonArray_data);
            for (int i = 0; i < jsonArray_data.length(); i++) {
                String status=jsonArray_data.getJSONObject(i).optString("status");
                if(status.equals("SUCCESSFUL"))
                {
                    Log.i("Motor_State","=========================");
                    JSONObject rs_obeject= new JSONObject(jsonArray_data.getJSONObject(i).optString("result"));
                    Log.i("Motor_State","====="+rs_obeject.optString("Motor_State"));
                    if(rs_obeject.optInt("Motor_State")== 1)
                    {
                        // TODO 这里是灯表示开
                        motor_s=true ;

                    }
                    return motor_s;
                }
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean GET_Luminance_value(Context mycontext, String dvid) {
        boolean led_s=false;
        try {
            String add_url = Config.all_url + "/iocm/app/cmd/v1.4.0/deviceCommands?&pageNo=0&pageSize=100"+"&deviceId="+dvid;
            String json = DataManager.Dv_get_Cammand(mycontext, add_url, login_appid, token);
            JSONObject jsonobject_command = new JSONObject(json);
            JSONArray jsonArray_data=jsonobject_command.getJSONArray("data");
            Log.i("Luminance_Value","====="+jsonArray_data);
            for (int i = 0; i < jsonArray_data.length(); i++) {
                String status=jsonArray_data.getJSONObject(i).optString("status");
                if(status.equals("SUCCESSFUL"))
                {
                    Log.i("Light_State","=========================");
                    JSONObject rs_obeject= new JSONObject(jsonArray_data.getJSONObject(i).optString("result"));
                    Log.i("Light_State","====="+rs_obeject.optString("Light_State"));
                    if(rs_obeject.optInt("Light_State")== 1)
                    {
                        // TODO 这里是灯表示开
                        led_s=true ;

                    }
                    return led_s;
                }
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * @param mycontext
     * @param dvid
     * @return int
     * 0 光灯 1开灯 2 发送中
     */
    public int GET_Led_first_staus(Context mycontext, String dvid) {
        int led_s=0;
        try {
            String add_url = Config.all_url + "/iocm/app/cmd/v1.4.0/deviceCommands?&pageNo=0&pageSize=100"+"&deviceId="+dvid;
            String json = DataManager.Dv_get_Cammand(mycontext, add_url, login_appid, token);
            JSONObject jsonobject_command = new JSONObject(json);
            JSONArray jsonArray_data=jsonobject_command.getJSONArray("data");


                String status=jsonArray_data.getJSONObject(0).optString("status");
                if(status.equals("SENT"))
                {
                    led_s=2;
                }
                else
                {
                    Log.i("Light_State","=========================");
                    JSONObject rs_obeject= new JSONObject(jsonArray_data.getJSONObject(0).optString("result"));
                    Log.i("Light_State","====="+rs_obeject.optString("Light_State"));
                    if(rs_obeject.optString("Light_State")=="1")
                    {
                        led_s=1 ;
                    }
                }
        } catch ( Exception e ) {
            e.printStackTrace();
        }
        return  led_s;
    }
}
