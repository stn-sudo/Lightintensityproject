package com.mcz.light_appproject.app.model;

import java.io.Serializable;

/**
 * Iot club all rights reserved
 * Created by Iot club on 2018/12/14.
 */

public class DataInfo implements Serializable{
    public String DeviceId ;
    public String DeviceName;
    public String DeviceStatus;
    public String DeviceType;
    public String Devicelight;
    public String Devicetemperature;
    public String Devicehumidity;
    public String error_code;
    ////////////////////////////////
    public String lasttime;
    public String Devicetimestamp;
    public boolean led_s;//表示灯的状态
    public boolean laba_s;//表示灯的状态
    public boolean motor_s;//表示灯的状态

    public boolean isLed_s() {
        return led_s;
    }

    public boolean isLaba_s() {
        return laba_s;
    }

    public boolean ismotor_s() {
        return motor_s;
    }

    public void setLed_s(boolean led_s) {
        this.led_s = led_s;
    }

    public void setMotor_s(boolean motor_s) {
        this.motor_s = motor_s;
    }




    public void setDevicelight(String dv_data1) {
        Devicelight = dv_data1;
    }

    public void setDevicetemperature(String dv_data2) {
        Devicetemperature = dv_data2;
    }

    public void setDevicehumidity(String dv_data3) {
        Devicehumidity = dv_data3;
    }

    public String getDevicelight() {
        return Devicelight;
    }

    public String getDevicetemperature() {
        return Devicetemperature;
    }

    public String getDevicehumidity() {
        return Devicehumidity;
    }

    public String getError_code() {
        return error_code;
    }

    public void setError_code(String error_code) {
        this.error_code = error_code;
    }



    public String getLasttime() {
        return lasttime;
    }

    public void setLasttime(String lasttime) {
        this.lasttime = lasttime;
    }

    public  String getDeviceName() {
        return DeviceName;
    }

    public void setDeviceName(String deviceName) {
        DeviceName = deviceName;
    }


    public String getDeviceStatus() {
        return DeviceStatus;
    }

    public void setDevicetimestamp(String timestamp) {
        Devicetimestamp = timestamp;
    }



    public String getDevicetimestamp() {
        return Devicetimestamp;
    }

    public void setDeviceStatus(String deviceStatus) {
        DeviceStatus = deviceStatus;
    }

    public String getDeviceType() {
        return DeviceType;
    }

    public void setDeviceType(String deviceType) {
        DeviceType = deviceType;
    }

    public String getDeviceId() {
        return DeviceId;
    }

    public void setDeviceId(String deviceId) {
        this.DeviceId = deviceId;
    }
    public String getGatewayId() {
        return DeviceId;
    }

    public void setGatewayId(String deviceId) {
        this.DeviceId = deviceId;
    }
}
