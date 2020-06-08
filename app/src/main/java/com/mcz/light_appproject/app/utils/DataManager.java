package com.mcz.light_appproject.app.utils;

import android.content.Context;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;

import static com.mcz.light_appproject.app.utils.Config.SELFCERTPATH;
import static com.mcz.light_appproject.app.utils.Config.SELFCERTPWD;
import static com.mcz.light_appproject.app.utils.Config.TRUSTCAPATH;
import static com.mcz.light_appproject.app.utils.Config.TRUSTCAPWD;

/**
 * Iot club all rights reserved
 * Created by Iot club on 2018/12/14.
 */

public class DataManager {
    /**
     *
     * 登录鉴权
     * @param mContext
     * @param serverurl
     * @param userID
     * @param tokenpwd
     * @return
     * @throws Exception
     */
    public static String Login_Request(Context mContext, String serverurl,String userID,String tokenpwd)
            throws Exception{
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("appId", userID));
        params.add(new BasicNameValuePair("secret", tokenpwd));
        String json = NetConnect.request(mContext, NetConnect.REQUEST_TYPE_POST,
                serverurl, params);
        return json;
    }

    public static String Txt_REQUSET(Context mContext, String serverurl,String app_key,String accessToken)
            throws Exception{
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        String json = NetConnectHeaderData.request(mContext, NetConnectHeaderData.REQUEST_TYPE_GET,
                serverurl,app_key,accessToken, params);
        return json;
    }
    public static String Register_DEVICEID(Context mContext, String serverurl,String app_key,String accessToken,String nodeID)
            throws Exception{
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("verifyCode", nodeID));
        params.add(new BasicNameValuePair("nodeId", nodeID));
        params.add(new BasicNameValuePair("timeout", "600"));
        String json = NetConnectHeaderDataJSON.request(mContext, NetConnectHeaderDataJSON.REQUEST_TYPE_POST,
                serverurl,app_key,accessToken, params);
        return json;
    }
//命令下发
    public static String ElightComened_DEVICEID(Context mContext, String serverurl,String app_key,String accessToken,String deviceId,String LED)
            throws Exception{
        JSONObject JsonData=new JSONObject();
        JSONObject command=new JSONObject();
        JSONObject paras=new JSONObject();
        paras.put("Light",LED);
        command.put("serviceId","Agriculture");
        command.put("method","Agriculture_Control_Light");
        command.put("paras",paras);
        JsonData.put("deviceId",deviceId);
        JsonData.put("command",command);
        JsonData.put("expireTime",0);
        String json = NETCommned.request(mContext, NETCommned.REQUEST_TYPE_POST,
                serverurl,app_key,accessToken,JsonData);
        return json;
    }
    public static String BEEPComened_DEVICEID(Context mContext, String serverurl,String app_key,String accessToken,String deviceId,String LED)
            throws Exception{
        JSONObject JsonData=new JSONObject();
        JSONObject command=new JSONObject();
        JSONObject paras=new JSONObject();
        paras.put("Beep",LED);
        command.put("serviceId","Agriculture");
        command.put("method","Agriculture_Control_Beep");
        command.put("paras",paras);
        JsonData.put("deviceId",deviceId);
        JsonData.put("command",command);
        JsonData.put("expireTime",0);
        String json = NETCommned.request(mContext, NETCommned.REQUEST_TYPE_POST,
                serverurl,app_key,accessToken,JsonData);
        return json;
    }
    public static String MOTORComened_DEVICEID(Context mContext, String serverurl,String app_key,String accessToken,String deviceId,String LED)
            throws Exception{
        JSONObject JsonData=new JSONObject();
        JSONObject command=new JSONObject();
        JSONObject paras=new JSONObject();
        paras.put("Motor",LED);
        command.put("serviceId","Agriculture");
        command.put("method","Agriculture_Control_Motor");
        command.put("paras",paras);
        JsonData.put("deviceId",deviceId);
        JsonData.put("command",command);
        JsonData.put("expireTime",0);
        String json = NETCommned.request(mContext, NETCommned.REQUEST_TYPE_POST,
                serverurl,app_key,accessToken,JsonData);
        return json;
    }
    /**
     *
     * 查询设备命令
     */
    public static String Dv_get_Cammand(Context mContext, String serverurl,String app_key,String accessToken)
            throws Exception{
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        String json =  NetConnectHeaderData.request(mContext, NetConnectHeaderData.REQUEST_TYPE_GET,
                serverurl,app_key,accessToken, params);
        return json;
    }
//删除直连设备
    public static String Delete_DEVICEID(Context mContext, String serverurl,String app_key,String accessToken)
            throws Exception{

        String json = DeletingConnect.request(mContext, DeletingConnect.DELETE,
                serverurl,app_key,accessToken);
        return json;
    }

    /**
     *
     * 修改设备信息
     */
    public static String UPDATE_DEVICEID(Context mContext, String serverurl,String app_key,String accessToken,String name)
            throws Exception{
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("name", name));
        params.add(new BasicNameValuePair("deviceType", "StreeLight"));
        params.add(new BasicNameValuePair("model", "E53Light"));
        params.add(new BasicNameValuePair("manufacturerId", "IoTclub"));
        params.add(new BasicNameValuePair("manufacturerName", "IoTclub"));
        params.add(new BasicNameValuePair("protocolType", "CoAP"));
        params.add(new BasicNameValuePair("location", "Shenzhen"));
        params.add(new BasicNameValuePair("mute", "FALSE"));

        String json = NetConnectHeaderDataJSON.request(mContext, NetConnectHeaderDataJSON.REQUEST_TYPE_PUT,
                serverurl,app_key,accessToken, params);
        return json;
    }


    /**
     *
     * https连接认证
     * @param mContext
     * @return
     * @throws IOException
     * @throws KeyStoreException
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     */
    public static SSLSocketFactory  GETSSLinitHttpClientBook(Context mContext) throws IOException,KeyStoreException,NoSuchAlgorithmException,KeyManagementException{
        // 服务器端需要验证的客户端证书
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        // 客户端信任的服务器端证书
        KeyStore trustStore = KeyStore.getInstance("BKS");
        // 读取证书输入流
        InputStream ksIn = mContext.getAssets().open(SELFCERTPATH);
        InputStream tsIn = mContext.getAssets().open(TRUSTCAPATH);
        try {
            keyStore.load(ksIn, SELFCERTPWD.toCharArray());
            trustStore.load(tsIn, TRUSTCAPWD.toCharArray());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //关闭流
            try {
                ksIn.close();
            } catch (Exception ignore) {
            }
            try {
                tsIn.close();
            } catch (Exception ignore) {
            }
        }

        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(trustStore);
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("X509");
        SSLSocketFactory ssl=null;
        try {
            keyManagerFactory.init(keyStore, SELFCERTPWD.toCharArray());
            ssl = new SSLSocketFactory(keyStore, SELFCERTPWD, trustStore);
        } catch ( UnrecoverableKeyException e ) {
            e.printStackTrace();
        }
        ssl.setHostnameVerifier(new AllowAllHostnameVerifier());

        return ssl;
    }


}
