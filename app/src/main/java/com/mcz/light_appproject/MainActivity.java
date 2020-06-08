package com.mcz.light_appproject;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.baidu.speech.EventManager;
import com.baidu.speech.EventManagerFactory;
import com.baidu.speech.asr.SpeechConstant;
import com.gyf.barlibrary.ImmersionBar;
import com.mcz.light_appproject.app.model.DataInfo;
import com.mcz.light_appproject.app.model.PullrefreshListviewAdapter;

import com.mcz.light_appproject.app.ui.activity.InputManualActivity;
import com.mcz.light_appproject.app.ui.activity.LoginActivity;
import com.mcz.light_appproject.app.ui.zxing.zxing.new_CaptureActivity;
import com.mcz.light_appproject.app.utils.Config;
import com.mcz.light_appproject.app.utils.DataManager;
import com.mcz.light_appproject.app.utils.Ocean_method;
import com.mcz.light_appproject.app.view.view.IPullToRefresh;
import com.mcz.light_appproject.app.view.view.LoadingLayout;
import com.mcz.light_appproject.app.view.view.PullToRefreshBase;
import com.mcz.light_appproject.app.view.view.PullToRefreshFooter;
import com.mcz.light_appproject.app.view.view.PullToRefreshHeader;
import com.mcz.light_appproject.app.view.view.PullToRefreshListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Iot club all rights reserved
 * Created by Iot club on 2018/12/14.
 */
public class MainActivity extends AppCompatActivity implements com.baidu.speech.EventListener{

    public final static int POSITION_INPUTMANUAL = 100;
    private String totalCount;
    String datajson = "";
    @BindView(R.id.edt_dvid_search)
    EditText edtDvidSearch;
    @BindView(R.id.search_delete)
    ImageView searchDelete;
    @BindView(R.id.img_srearch)
    ImageView imgSrearch;
    @BindView(R.id.sound)
    ImageView soundbutton;

    //监听back键点击时间
    private long backfirsttime = 0;
    //    TextView txt;
    String token = "";
    SharedPreferences sp;
    @BindView(R.id.main_pull_refresh_lv)
    PullToRefreshListView mListView;
    @BindView(R.id.img_adddv)
    ImageView imgchoose;
    private View mNoMoreView;
    private String login_appid;
    private PullrefreshListviewAdapter adapter;
    private List<DataInfo> mlist = null;
    boolean type_choose = true;
    private EventManager asr;
    Handler newhandler;
    String ser_data;
    Boolean reflash;

    private Ocean_method ocean_method;//连接平台的方法
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_layout);
        ButterKnife.bind(this);
        ImmersionBar.with(this)
                .statusBarColor(R.color.toolbars)
                .fitsSystemWindows(true)
                .init();
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        init();
        mListView = (PullToRefreshListView) findViewById(R.id.main_pull_refresh_lv); //下拉刷新的项目

        asr = EventManagerFactory.create(this, "asr");//注册自己的输出事件类
        asr.registerListener(this);                  // 调用 EventListener 中 onEvent方法

        soundbutton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.RECORD_AUDIO}, 1);
                        start();
                        soundbutton.setImageDrawable(ContextCompat.getDrawable(adapter.mContext, R.drawable.yuyingrey));
                        break;
                    case MotionEvent.ACTION_UP:
                        stop();
                        soundbutton.setImageDrawable(ContextCompat.getDrawable(adapter.mContext, R.drawable.yuyinblue));
                        break;
                    default:
                        return false;
                }

                return true;
            }
        });
        new Timer().schedule(new TimerTask() {
            public void run() {
                Message message = new Message();
                message.what = 5;
                mHandler.sendMessage(message);
            }
        }, 4000, 1000);

        newhandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 9 && reflash == true) {
                    JSONObject jsonObject = null;
//                    while(true)
//                    {
                        try {
                            jsonObject = new JSONObject(ser_data);
                            adapter.vh.tv_DeviceLight.setText(jsonObject.optString("luminance"));
                            adapter.vh.tv_DeviceTemperature.setText(jsonObject.optString("Temperature"));
                            adapter.vh.tv_DeviceHumidity.setText(jsonObject.optString("Humidity"));
//                            break;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
//                    }


                }
            }

        };
    }
    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            if(msg.what == 5) {
                new Thread(new Runnable(){
                    public void run() {
                        JSONObject jo1 = null;
                        String add_url = Config.all_url + "/iocm/app/dm/v1.3.0/devices?appId=" + login_appid + "&pageNo=" + 0 + "&pageSize=" + 5;
                        while(true)
                        {
                            try {
                                    String json = DataManager.Txt_REQUSET(MainActivity.this, add_url, login_appid, token);
                                    jo1 = new JSONObject(json);
                                    break;
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                        }
                        try {
                        JSONArray jsonArray = jo1.getJSONArray("devices");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                String servicesinfo = jsonArray.getJSONObject(i).optString("services");
                                if (!servicesinfo.isEmpty()) {
                                    JSONArray jsa = new JSONArray(servicesinfo);
                                    for (int j = 0; j < jsa.length(); j++) {
                                        ser_data = jsa.getJSONObject(j).getString("data");
                                        Message msgnew = new Message();
                                        msgnew.what = 9;
                                        newhandler.sendMessage(msgnew);
                                        }
                                    }
                                }
                            }catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }).start();


                }               // dataInfo.setDevicelight(jsonObject.optString("Luminance"));
                //ListviewADD_Data(0, 5);
                //adapter.getView(0,adapter.convertView,adapter.parent);
//                List<DataInfo> result = ,
                 //new LoadDataAsyncTask(true,true).execute();
                //adapter.getItem(0);
                //adapter.vh.tv_DeviceLight.setText(jsonObject.optString("Luminance"));
                //这里可以进行UI操作，如Toast，Dialog等
            }
    };

    private void start() {
        Map<String, Object> params = new LinkedHashMap<>();//传递Map<String,Object>的参数，会将Map自动序列化为json
        params.put(SpeechConstant.ACCEPT_AUDIO_VOLUME, false);//回调当前音量
        String json = new JSONObject(params).toString();//demo用json数据来做数据交换的方式
        asr.send(SpeechConstant.ASR_START, json, null, 0, 0);// 初始化EventManager对象,这个实例只能创建一次，就是我们上方创建的asr，此处开始传入
    }

    private void stop() {
        asr.send(SpeechConstant.ASR_STOP, null, null, 0, 0);//此处停止
    }

    int item_position = 0;
    //隐藏输入法窗口
    private void hintKbTwo() {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm.isActive()&&getCurrentFocus()!=null){
            if (getCurrentFocus().getWindowToken()!=null) {
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }
    private void init() {
        login_appid = sp.getString("appId","");
        token = sp.getString("token", "");
        hintKbTwo();
        mNoMoreView = getLayoutInflater().inflate(R.layout.no_device_more_footer, null);
        mListView.setLoadingLayoutCreator(new PullToRefreshBase.LoadingLayoutCreator() {
            @Override
            public LoadingLayout create(Context context, boolean headerOrFooter,
                                        PullToRefreshBase.Orientation orientation) {
                if (headerOrFooter)
                    return new PullToRefreshHeader(context);
                else
                    return new PullToRefreshHeader(context);
                  // return new PullToRefreshFooter(context, PullToRefreshFooter.Style.EMPTY_NO_MORE.EMPTY_NO_MORE);

            }
        });
        mListView.getRefreshableView().setSelector(getResources().getDrawable(R.color.transparent));
        //设置可上拉刷新和下拉刷新
        /**
         4          * 设置刷新的模式：常用的有三种
         5          * PullToRefreshBase.Mode.BOTH  //上下拉刷新都可以
         6          * PullToRefreshBase.Mode.PULL_FROM_START  //只允许下拉刷新
         7          * PullToRefreshBase.Mode.PULL_FROM_END   //只允许上拉刷新
         8         */
        mListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        //异步加载数据
        mListView.setOnRefreshListener(new IPullToRefresh.OnRefreshListener<ListView>() {

            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView,
                                  boolean headerOrFooter) {
                new LoadDataAsyncTask(headerOrFooter, false).execute();
            }
        });
        adapter = new PullrefreshListviewAdapter( this);
        adapter.setlogin_appid(login_appid);
        adapter.settoken(token);
        mListView.getRefreshableView().addFooterView(mNoMoreView);
        mListView.setAdapter(adapter);
        mListView.getRefreshableView().removeFooterView(mNoMoreView);
        ocean_method=new Ocean_method(login_appid,token);//初始化方法
    }

    /**
     * @param view
     * 主页面点击按钮处罚事件
     */
    @OnClick({R.id.img_adddv, R.id.img_srearch, R.id.search_delete, R.id.img_back})
    public void onClick(View view) {
        switch (view.getId()) {//点击添加设备
            case R.id.img_adddv: //加号
                Choose_item_ADD();
                item_position++;
                if (item_position == 1) {   //点一下
                    popupWindow.showAsDropDown(view);// 选择弹出框，
                } else if (item_position == 2) {
                    popupWindow.dismiss();
                    item_position = 0;
                }
                break;
            case R.id.img_back: //加号
                Intent intent=new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                break;
            case R.id.img_srearch://搜索栏点击搜索
                if (!edtDvidSearch.getText().toString().trim().equals("")){
                    adapter.clearItem();
                    mlist=new ArrayList<DataInfo>();
                    datajson=edtDvidSearch.getText().toString().trim();
                    new LoadDataAsyncTask(true,true).execute();
                    hintKbTwo();

                }else{
                    new LoadDataAsyncTask(true, true).execute();//查询所有
                    hintKbTwo();
                }
                break;
            case R.id.search_delete://搜索栏点击删除搜索内容
                edtDvidSearch.setText("");
                break;
        }
    }


    /**
     * listview添加数据
     *
     * @param Fnum
     * @param Onum
     * @return
     */
    private List<DataInfo> ListviewADD_Data(int Fnum, int Onum) {
        reflash = false;
        boolean isadditem=true;
            JSONObject jo2 = null;
            String add_url = Config.all_url + "/iocm/app/dm/v1.3.0/devices?appId=" + login_appid
                    + "&pageNo=" + Fnum + "&pageSize=" + Onum;
            mlist = new ArrayList<DataInfo>();
//            while(true)
//            {
                try {
                    String json = DataManager.Txt_REQUSET(MainActivity.this, add_url, login_appid, token);
                    jo2 = new JSONObject(json);
//                    break;
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
//            }
//            try {
//            String json = DataManager.Txt_REQUSET(MainActivity.this, add_url, login_appid, token);
//            mlist = new ArrayList<DataInfo>();
//            jo = new JSONObject(json);
//            } catch (Exception e1) {
//                try {
//                    String json = DataManager.Txt_REQUSET(MainActivity.this, add_url, login_appid, token);
//                    jo = new JSONObject(json);
//                } catch (Exception e2) {
//                    try {
//                        String json = DataManager.Txt_REQUSET(MainActivity.this, add_url, login_appid, token);
//                        jo = new JSONObject(json);
//                    } catch (Exception e3) {
//                        try {
//                            String json = DataManager.Txt_REQUSET(MainActivity.this, add_url, login_appid, token);
//                            jo = new JSONObject(json);
//                        } catch (Exception e4) {
//                        }
//                    }
//                }
//            }
          try {
            totalCount=jo2.getString("totalCount");
            JSONArray jsonArray = jo2.getJSONArray("devices");
            for (int i = 0; i < jsonArray.length(); i++) {
                DataInfo dataInfo = new DataInfo();
                String deviceinfo = jsonArray.getJSONObject(i).getString("deviceInfo"); //get会抛出异常
                String servicesinfo = jsonArray.getJSONObject(i).optString("services"); //opt会抛出异常
                if (!servicesinfo.isEmpty()){
                    JSONArray jsa=new JSONArray(servicesinfo);
                    for (int j = 0; j < jsa.length(); j++) {
                        String ser_data = jsa.getJSONObject(j).getString("data");
                        JSONObject jsonObject = new JSONObject(ser_data);

                        dataInfo.setDevicelight(jsonObject.optString("luminance"));
                        dataInfo.setDevicetemperature(jsonObject.optString("Temperature"));
                        dataInfo.setDevicehumidity(jsonObject.optString("Humidity"));
                    }
                    dataInfo.setDeviceId(jsonArray.getJSONObject(i).optString("deviceId"));
                    boolean led_s=ocean_method.GET_Led_staus(MainActivity.this,jsonArray.getJSONObject(i).optString("deviceId"));//获取该灯的状态
                    boolean motor_s=ocean_method.GET_Motor_staus(MainActivity.this,jsonArray.getJSONObject(i).optString("deviceId"));//获取该灯的状态
                    dataInfo.setLed_s(led_s);
                    dataInfo.setMotor_s(motor_s);
                    dataInfo.setGatewayId(jsonArray.getJSONObject(i).optString("gatewayId"));  //获取网关id
                    dataInfo.setLasttime(jsonArray.getJSONObject(i).optString("lastModifiedTime"));   //获取最后调整时间
                    JSONObject object = new JSONObject(deviceinfo);
                    dataInfo.setDeviceName(object.optString("name"));
                    dataInfo.setDeviceStatus(object.optString("status"));
                    if(isadditem)
                    mlist.add(dataInfo);
                }
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        }
        reflash = true;
        return mlist;

    }
//查询前一百页的命令下发数据


    PopupWindow popupWindow;
    private void Choose_item_ADD() {
        View popView = LayoutInflater.from(this).inflate(
                R.layout.popmenu, null);
        popupWindow = new PopupWindow(popView, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setOutsideTouchable(true);
        setBackgroundAlpha(0.8f);//设置屏幕透明度
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                // popupWindow隐藏时恢复屏幕正常透明度
                setBackgroundAlpha(1.0f);
                item_position = 0;
            }
        });
        popView.findViewById(R.id.txt_manual).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                //手动输入设备号
                item_position = 0;
                Intent mIntent = new Intent(MainActivity.this, InputManualActivity.class);
                startActivityForResult(mIntent, POSITION_INPUTMANUAL);
                finish();
            }
        });
        popView.findViewById(R.id.txt_scan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                item_position = 0;
                Intent mIntent = new Intent(MainActivity.this, new_CaptureActivity.class);
                startActivity(mIntent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
    }


    private List<DataInfo> Listview_Inputmanual(String dv_name) {
        try {
            String add_url = Config.all_url + "/iocm/app/dm/v1.3.0/devices?appId=" + login_appid
                    + "&pageNo=0"  + "&pageSize=" + totalCount;
            String json = DataManager.Txt_REQUSET(MainActivity.this, add_url, login_appid, token);
            Log.i("==========","====="+json);
            mlist = new ArrayList<DataInfo>();
            JSONObject jo = new JSONObject(json);
            totalCount=jo.getString("totalCount");
            JSONArray jsonArray = jo.getJSONArray("devices");
            for (int i = 0; i < jsonArray.length(); i++) {

                String deviceinfo = jsonArray.getJSONObject(i).getString("deviceInfo");
                JSONObject object = new JSONObject(deviceinfo);
                if(object.optString("name").contains(dv_name))
                {
                    DataInfo dataInfo = new DataInfo();
                    dataInfo.setDeviceName(object.optString("name"));
                    //  dataInfo.setDeviceType(object.optString("deviceType"));//model  deviceType
                    dataInfo.setDeviceStatus(object.optString("status"));
                    String servicesinfo = jsonArray.getJSONObject(i).optString("services");
                    if (!servicesinfo.isEmpty()){
                        JSONArray jsa=new JSONArray(servicesinfo);
                        for (int j = 0; j < jsa.length(); j++) {
                            String ser_data = jsa.getJSONObject(j).getString("data");
                            JSONObject jsonObject = new JSONObject(ser_data);
                            dataInfo.setDevicelight(jsonObject.optString("Luminance"));
                            dataInfo.setDevicehumidity(jsonObject.optString("Humidity"));
                            dataInfo.setDevicetemperature(jsonObject.optString("Temperature"));
                        }
//
                        dataInfo.setDeviceId(jsonArray.getJSONObject(i).optString("deviceId"));
                        dataInfo.setGatewayId(jsonArray.getJSONObject(i).optString("gatewayId"));
                        dataInfo.setLasttime(jsonArray.getJSONObject(i).optString("lastModifiedTime"));

                        mlist.add(dataInfo);
                    }else{
                        dataInfo.setDeviceId(jsonArray.getJSONObject(i).optString("deviceId"));
                        dataInfo.setGatewayId(jsonArray.getJSONObject(i).optString("gatewayId"));
                        dataInfo.setLasttime(jsonArray.getJSONObject(i).optString("lastModifiedTime"));
                        dataInfo.setDeviceName(object.optString("name"));
                        //  dataInfo.setDeviceType(object.optString("deviceType"));//model  deviceType
                        dataInfo.setDeviceStatus(object.optString("status"));
                        dataInfo.setDevicelight("暂无数据");
                        dataInfo.setDevicetemperature("暂无数据");
                        dataInfo.setDevicehumidity("暂无数据");
                        mlist.add(dataInfo);
                    }
                }
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        }

        return mlist;
    }
    public void setBackgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getWindow()
                .getAttributes();
        lp.alpha = bgAlpha;
        getWindow().setAttributes(lp);
    }
    /**
     * 异步加载任务
     */
    //下标标识


    public void onEvent(String name, String params, byte[] data, int offset, int length) {
        String resultTxt = null;
        if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_PARTIAL)) {//识别结果参数
            if (params.contains("\"final_result\"")) {//语义结果值
                try {
                    JSONObject json = new JSONObject(params);
                    String result = json.getString("best_result");//取得key的识别结果
                    resultTxt = result;
                    if(resultTxt .trim().contains("灯") || resultTxt .trim().contains("登") || resultTxt .trim().contains("脑") || resultTxt .trim().contains("的"))
                    {
                        if(resultTxt .trim().contains("开") || resultTxt .trim().contains("打") || resultTxt .trim().contains("打"))
                        {
                            adapter.switch_led.setChecked(true);
                        }
                        else if(resultTxt .trim().contains("关") || resultTxt .trim().contains("灭") || resultTxt .trim().contains("上") || resultTxt .trim().contains("观"))
                        {
                            adapter.switch_led.setChecked(false);
                        }
                    }
                    else if(resultTxt .trim().contains("蜂鸣") || resultTxt .trim().contains("方便") || resultTxt .trim().contains("风景") || resultTxt .trim().contains("云")  || resultTxt .trim().contains("锋面")  || resultTxt .trim().contains("起") || resultTxt .trim().contains("气"))
                    {
                        if(resultTxt .trim().contains("开") || resultTxt .trim().contains("打"))
                        {
                            adapter.switch_beep.setChecked(true);
                        }
                        else if(resultTxt .trim().contains("关") || resultTxt .trim().contains("灭") || resultTxt .trim().contains("上") || resultTxt .trim().contains("观"))
                        {
                            adapter.switch_beep.setChecked(false);
                        }
                    }
                    else if(resultTxt .trim().contains("机") || resultTxt .trim().contains("鸡") || resultTxt .trim().contains("系") || resultTxt .trim().contains("你")  || resultTxt .trim().contains("级")  || resultTxt .trim().contains("链接"))
                    {
                        if(resultTxt .trim().contains("开") || resultTxt .trim().contains("打"))
                        {
                            adapter.switch_motor.setChecked(true);
                        }
                        else if(resultTxt .trim().contains("关") || resultTxt .trim().contains("灭") || resultTxt .trim().contains("上") || resultTxt .trim().contains("观"))
                        {
                            adapter.switch_motor.setChecked(false);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class LoadDataAsyncTask extends AsyncTask<Void, Void, List<DataInfo>> {
        private boolean mHeaderOrFooter;
        private boolean is_Add;
        private int numbers = 5;
        private int begin = 0;
        public LoadDataAsyncTask(boolean headerOrFooter, boolean isadd) {
            mHeaderOrFooter = headerOrFooter;
            is_Add = isadd;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (mHeaderOrFooter) {
                mListView.setVisibility(View.VISIBLE);
            }
            mListView.getRefreshableView().removeFooterView(mNoMoreView);
        }
        @Override
        protected List<DataInfo> doInBackground(Void... params) {
            if (MainActivity.this.isFinishing()) {
                return null;
            }
            List<DataInfo> result = null;
            try {
                //首先判断是否查询所有还是单个
                if (!is_Add) {
                    if (mHeaderOrFooter && type_choose) {
                        //下拉刷新走的方法
                        if (type_choose == false) {
                            adapter.clearItem();
                            mlist = new ArrayList<DataInfo>();
                        }
                        type_choose = true;
                        numbers = 5;
                        result = ListviewADD_Data(0, numbers);
                        //new LoadDataAsyncTask(true,true).execute(); //自己加的
                        begin=5;
                    } else {
//                        if (type_choose == false) {
//                            adapter.clearItem();
//                            mlist = new ArrayList<DataInfo>();
//                            numbers = 5;
//                            result = ListviewADD_Data(0, numbers);
//                            begin = 5;
//                            type_choose = true;
//                        } else {
//                            begin = begin + 5;
//                            result = ListviewADD_Data(0, begin);
//                            type_choose = true;
//                        }
                    }
                } else {
                    //更据ID查询
                    result = Listview_Inputmanual(datajson);
                }
                return result;
            } catch ( Exception e ) {
                e.printStackTrace();
                return null;
            }
        }
        /**
         * 完成时的方法
         */
        @Override
        protected void onPostExecute(List<DataInfo> result) {
            super.onPostExecute(result);
            mListView.onRefreshComplete();
            if (MainActivity.this.isFinishing()) {
                return;
            }
            if (result != null) {
                if (mHeaderOrFooter) {
                    CharSequence dateText = DateFormat.format("yyyy-MM-dd kk:mm:ss", new Date());
                    for (LoadingLayout layout : mListView.getLoadingLayoutProxy(true, false).getLayouts()) {
                        ((PullToRefreshHeader) layout).setLastRefreshTime(":" + dateText);
                    }
                    adapter.clearItem();
                    mlist = new ArrayList<DataInfo>();
                }
                if (adapter.getCount() == 0 && result.size() == 0) {

                    mListView.getRefreshableView().removeFooterView(mNoMoreView);

                } else if (result.size()>5) {
                    //在这里判断数据的多少确定下一步能否上拉加载
//                    if (result.size()<5){
//                        mListView.getRefreshableView().removeFooterView(mNoMoreView);
//                    }else{
                    if (result.size()%5==0){
                    }else{
                        mListView.setFooterRefreshEnabled(false);
                        mListView.getRefreshableView().addFooterView(mNoMoreView);
//                        rela_nodata.setVisibility(View.GONE);
//                        mListView.setVisibility(View.VISIBLE);
                    }
//                    }
                } else if (mHeaderOrFooter) {

                    if (result.size()>0){
//                        rela_nodata.setVisibility(View.GONE);
//                        mListView.setVisibility(View.VISIBLE);
                    }
                    mListView.setFooterRefreshEnabled(true);
                    mListView.getRefreshableView().removeFooterView(mNoMoreView);
                }
//                ???
//                relat.setVisibility(View.GONE);
                //通过additem方法把刷新后拿到的数据循环添加到adapter中去
//                if ((result.size()==1)) {
////                    if (!result.get(0).getError_code().equals("")){
////                        rela_nodata.setVisibility(View.VISIBLE);
////                        Toast.makeText(MainActivity.this, "暂无该设备信息", Toast.LENGTH_SHORT).show();
////                    }else {

////                    }
//                }else {
                    addlistdata(result);
//                mListView.setFooterRefreshEnabled(false);
//                mListView.getRefreshableView().removeFooterView(mNoMoreView);
//                    rela_nodata.setVisibility(View.GONE);
                    adapter.notifyDataSetChanged();//刷新完成
//                }
            }else{
//                rela_nodata.setVisibility(View.VISIBLE);
                Toast.makeText(MainActivity.this, "暂无设备信息", Toast.LENGTH_SHORT).show();

            }

        }
    }
    /**
     * 数据循环添加
     *
     * @param result
     */
    private void addlistdata(List<DataInfo> result) {
        int count = result.size();
        if(result.size()>0){
//            sp.edit().putBoolean("isplayer",true).commit();
        }
        adapter.clearItem();
        for (int i = 0; i < count; i++) {
            adapter.addItem(result.get(i));
            mlist.add(result.get(i));
            //mlist集合是用于存放界面中的值  并在跳转时传入item界面
        }
    }

    /**
     * 在进入界面的时候自动加载一遍
     * 第一次有数据显示
     */
    private void refreshButtonClicked() {
//        mListView.setVisibility(View.VISIBLE);
        mListView.setMode(IPullToRefresh.Mode.BOTH);
        mListView.setRefreshing();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if ((adapter != null && adapter.getCount() == 0)) {
            refreshButtonClicked();
        }
        hintKbTwo();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        refreshButtonClicked();
        hintKbTwo();
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        /**
         * 监听返回按钮 (back键)
         * 使用系统时间里判断用户是否需要退出
         */
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (System.currentTimeMillis() - backfirsttime > 2000) {
                Toast.makeText(getApplication(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                backfirsttime = System.currentTimeMillis();
            } else {

                System.exit(0);
//                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
