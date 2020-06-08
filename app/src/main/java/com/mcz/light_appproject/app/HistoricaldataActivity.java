package com.mcz.light_appproject.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gyf.barlibrary.ImmersionBar;
import com.mcz.light_appproject.MainActivity;
import com.mcz.light_appproject.R;
import com.mcz.light_appproject.app.model.DataInfo;
import com.mcz.light_appproject.app.model.PullrefreshListviewAdapter;
import com.mcz.light_appproject.app.model.PullrefreshListviewAdapter2;
import com.mcz.light_appproject.app.utils.Config;
import com.mcz.light_appproject.app.utils.DataManager;
import com.mcz.light_appproject.app.utils.Data_untils;
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
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;

/**
 * Iot club all rights reserved
 * Created by Iot club on 2018/12/14.
 */
public class HistoricaldataActivity extends AppCompatActivity {
    @BindView(R.id.local_ip)
    TextView newlocal_ip;
    @BindView(R.id.iot_ip)
    TextView newiot_ip;
    @BindView(R.id.txt_ms)
    TextView newtxt_ms;
    @BindView(R.id.adress)
    TextView newaddress;
    Handler myhandler;
    String deviceinfo;
    //图表参数
    private LineChartView line_chart;
    private boolean hasAxes = true;
    private Axis axisX;
    private Axis axisY;
    private Line line1 = new Line();
    private Line line2 = new Line();
    private Line line0 = new Line();
    private int startIndex=0;
    private float maxlight;
    private float minxlight;
    public PullrefreshListviewAdapter2 adapter2;
    String deviceId="";
    String gatewayId="";
    String token2 = "";
    SharedPreferences sp2;
    String TIME;
//    @BindView(R.id.main_pull_refresh_his)
//    PullToRefreshListView mListView2;

    @BindView(R.id.main_relative_main)
    RelativeLayout rela_nodata2;

    @BindView(R.id.img_back)
    ImageView back;

//    private View mNoMoreView2;

//    private PullrefreshListviewAdapter2 adapter2;

    private List<DataInfo> mlist2 = null;

    boolean type_choose2 = true;

    private String totalCount;
    private int inttotalCount;
    private int pageon=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historicaldata);//開始運行main_activity_layout界面
        ButterKnife.bind(this);
        ImmersionBar.with(this)
                .statusBarColor(R.color.toolbars)
                .fitsSystemWindows(true)
                .init();
        sp2 = PreferenceManager.getDefaultSharedPreferences(this);
        init();

        Intent intent=getIntent();
        deviceId=intent.getStringExtra("deviceId");
        gatewayId=intent.getStringExtra("gatewayId");
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        TextView titel_txt=(TextView)findViewById(R.id.titel_txt);
        titel_txt.setText("数据图线");

        myhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 10) {
                try {
                    JSONObject jsaa = new JSONObject(deviceinfo);
                    newlocal_ip.setText(jsaa.getString("name"));
                    newiot_ip.setText(jsaa.getString("manufacturerName"));
                    newtxt_ms.setText(jsaa.getString("deviceType"));
                    newaddress.setText(TIME);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    };





    }

    private void hintKbTwo() {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm.isActive()&&getCurrentFocus()!=null){
            if (getCurrentFocus().getWindowToken()!=null) {
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }
    private void init() {
        token2 = sp2.getString("token", "");
        hintKbTwo();
        rela_nodata2.setVisibility(View.GONE);
        line_chart = findViewById(R.id.line_chart);
        generateData();

        new LoadDataAsyncTask().execute();
    }
    /**
     * listview添加数据
     *

     * @return
     */
//查詢當個設備方法
    /**
     * listview添加数据
     *

     * @return
     */
//查詢當個設備方法
    private List<DataInfo> ListviewADD_char() {
            JSONObject jo = null;
            DataInfo dataInfo = null;
            String login_appid = sp2.getString("appId","");
            String add_url = Config.all_url + "/iocm/app/dm/v1.3.0/devices?appId=" + login_appid + "&pageNo=" + 0 + "&pageSize=" + 5;
//            while(true)
//            {
                try {
                    String json = DataManager.Txt_REQUSET(HistoricaldataActivity.this, add_url, login_appid, token2);
                    jo = new JSONObject(json);
//                    break;
                }catch(Exception e)
                    {
                        e.printStackTrace();
                    }
//                }
            ///////////////////////////************************2.3.2 查询单个设备信息***********************////////////////////////////
//            String add_url = Config.all_url + "/iocm/app/dm/v1.3.0/devices?appId=" + login_appid
//                    + "&pageNo=" + Fnum + "&pageSize=" + Onum;
            ////////////////////////////////////////////************************查询设备历史数据*****************/////////////////////////////////////
//            String add_url = Config.all_url + "/iocm/app/data/v1.3.0/deviceDataHistory?deviceId="+deviceId+"&gatewayId="+gatewayId;
//            String json = DataManager.Txt_REQUSET(HistoricaldataActivity.this, add_url, login_appid, token2);
        try {
            mlist2 = new ArrayList<DataInfo>();
            totalCount=jo.optString("totalCount");
            JSONArray jsonArray = jo.getJSONArray("devices");
            for (int i = 0; i < jsonArray.length(); i++) {
                dataInfo = new DataInfo();
                String timestamp = jsonArray.getJSONObject(i).getString("lastModifiedTime");
                Integer TIMESHI =  Integer.parseInt(timestamp.substring(9,11));
                TIMESHI +=8;
                TIME= timestamp.substring(0,4)+"-"+timestamp.substring(4,6)+"-"+timestamp.substring(6,8)+"  "+TIMESHI+":"+timestamp.substring(11,13)+":"+timestamp.substring(13,15);
                deviceinfo = jsonArray.getJSONObject(i).getString("deviceInfo"); //get会抛出异常
                if (!deviceinfo.isEmpty()) {
                        Message msg = new Message();
                        msg.what = 10;
                        myhandler.sendMessage(msg);
                }
                String servicesinfo = jsonArray.getJSONObject(i).optString("services"); //opt会抛出异常
                if (!servicesinfo.isEmpty()) {
                    JSONArray jsa = new JSONArray(servicesinfo);
                    for (int j = 0; j < jsa.length(); j++) {
                        String ser_data = jsa.getJSONObject(j).getString("data");
                        JSONObject jsonObject = new JSONObject(ser_data);
                        dataInfo.setDevicelight(jsonObject.optString("luminance"));
                        dataInfo.setDevicehumidity(jsonObject.optString("Humidity"));
                        dataInfo.setDevicetemperature(jsonObject.optString("Temperature"));
                        mlist2.add(dataInfo);
                    }
                }
            }}catch ( Exception e ) {
            e.printStackTrace();
        }

        return mlist2;
    }


    private class LoadDataAsyncTask extends AsyncTask<Void, Void, DataInfo>
    {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

    Handler kHandler = new Handler(){
        public void handleMessage(Message msgs){
            if(msgs.what == 6)
            {
                new Thread(new Runnable(){
                    public void run() {
                        try {
                        List<DataInfo> dataInfos = ListviewADD_char();
                        float data1 = Data_untils.convertToFloat(dataInfos.get(0).getDevicelight(), 0);
                        float data2 = Data_untils.convertToFloat(dataInfos.get(0).getDevicetemperature(), 0);
                        float data3 = Data_untils.convertToFloat(dataInfos.get(0).getDevicehumidity(), 0);
                        addDataPoint(data1,data2,data3);
                    }catch(Exception  e)
                    {
                        e.printStackTrace();
                    }
                    }
                }).start();
            }
        }
    };
        //@Override
        protected DataInfo doInBackground(Void... voids) {
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    Message msgs = new Message();
                    msgs.what = 6;
                    kHandler.sendMessage(msgs);
                }
            },200,2000);
            return null;
        }


        @Override
        protected void onPostExecute(DataInfo dataInfos) {
            super.onPostExecute(dataInfos);


        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
    }
    /**
     * 异步加载任务
     */
    //下标标识
//////////
//    private class LoadDataAsyncTask2 extends AsyncTask<Void, Void, List<DataInfo>> {
//        private boolean mHeaderOrFooter;
//        private boolean is_Add;
//
//        LoadDataAsyncTask2(boolean headerOrFooter, boolean isadd) {
//            mHeaderOrFooter = headerOrFooter;
//            is_Add = isadd;
//        }
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            if (mHeaderOrFooter) {
//                mListView2.setVisibility(View.VISIBLE);
//            }
//            mListView2.getRefreshableView().removeFooterView(mNoMoreView2);
//        }
//        @Override
//        protected List<DataInfo> doInBackground(Void... params) {
//            if (HistoricaldataActivity.this.isFinishing()) {
//                return null;
//            }
//            List<DataInfo> result = null;
//            try {
//                //首先判断是否查询所有还是单个
//                if (!is_Add) {
//                    if (mHeaderOrFooter && type_choose2) {
//                        //下拉刷新走的方法
//                        if (type_choose2 == false) {
//                            adapter2.clearItem();
//                            mlist2 = new ArrayList<DataInfo>();
//                        }
//                        type_choose2 = true;
//                        result = ListviewADD_Data(0);
////                    begin=7;
//                    } else {
////                    //上拉加载走的方法
//                        if (type_choose2 == false) {
//                            adapter2.clearItem();
//                            mlist2 = new ArrayList<DataInfo>();
//                            result = ListviewADD_Data(0);
//                            type_choose2 = true;
//                        } else {
////                            adapter.clearItem();
//                            result = ListviewADD_Data(0);
//                            type_choose2 = true;
//                        }
//                    }
//                } else {
//                    //更据ID查询
//                    //  result = Listview_Inputmanual(datajson);
//                }
//                return result;
//            } catch ( Exception e ) {
//                e.printStackTrace();
//                return null;
//            }
//        }
//        /**
//         * 完成时的方法
//         */
//        @Override
//        protected void onPostExecute(List<DataInfo> result) {
//            super.onPostExecute(result);
//            mListView2.onRefreshComplete();
//            if (HistoricaldataActivity.this.isFinishing()) {
//                return;
//            }
//            if (result != null) {
//                if (mHeaderOrFooter) {
//                    CharSequence dateText = DateFormat.format("yyyy-MM-dd kk:mm:ss", new Date());
//                    for (LoadingLayout layout : mListView2.getLoadingLayoutProxy(true, false).getLayouts()) {
//                        ((PullToRefreshHeader) layout).setLastRefreshTime(":" + dateText);
//                    }
//                    adapter2.clearItem();
//                    mlist2 = new ArrayList<DataInfo>();
//                }
//                if (adapter2.getCount() == 0 && result.size() == 0) {
//                    mListView2.setVisibility(View.GONE);
//                    mListView2.getRefreshableView().removeFooterView(mNoMoreView2);
//                    rela_nodata2.setVisibility(View.VISIBLE);
//                } else if (result.size()>5) {
//                    //在这里判断数据的多少确定下一步能否上拉加载
////                    if (result.size()<5){
////                        mListView.getRefreshableView().removeFooterView(mNoMoreView);
////                    }else{
//                    if (result.size()%5==0){
//                    }else{
//                        mListView2.setFooterRefreshEnabled(false);
//                        mListView2.getRefreshableView().addFooterView(mNoMoreView2);
//                        rela_nodata2.setVisibility(View.GONE);
//                        mListView2.setVisibility(View.VISIBLE);
//                    }
////                    }
//                } else if (mHeaderOrFooter) {
//
//                    if (result.size()>0){
//                        rela_nodata2.setVisibility(View.GONE);
//                        mListView2.setVisibility(View.VISIBLE);
//                    }
//                    mListView2.setFooterRefreshEnabled(true);
//                    mListView2.getRefreshableView().removeFooterView(mNoMoreView2);
//                }
//
//                addlistdata(result);
//                adapter2.notifyDataSetChanged();//刷新完成
////                }
//            }else{
//                rela_nodata2.setVisibility(View.VISIBLE);
//                Toast.makeText(HistoricaldataActivity.this, "暂无设备信息", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }


    /**
     * 数据循环添加
     *
     * @param result
     */
//    private void addlistdata(List<DataInfo> result) {
//        int count = result.size();
//        if(result.size()>0){
//            adapter2.clearItem();
//            for (int i = 0; i < count; i++) {
//                adapter2.addItem(result.get(i));
//                mlist2.add(result.get(i));
//                //mlist集合是用于存放界面中的值  并在跳转时传入item界面
//            }
//        }
//    }

    /**
     * 在进入界面的时候自动加载一遍
     * 第一次有数据显示
     */
//    private void refreshButtonClicked() {
//        mListView2.setVisibility(View.VISIBLE);
//        mListView2.setMode(IPullToRefresh.Mode.BOTH);
//        mListView2.setRefreshing();
//    }
    //MyThread线程任务类
    @Override
    protected void onResume() {
        super.onResume();
        //实列话MyThread类

//        if ((adapter2 != null && adapter2.getCount() == 0)) {
//            refreshButtonClicked();
//        }
        hintKbTwo();
    }
    @Override
    protected void onRestart() {
        super.onRestart();
//        refreshButtonClicked();
        hintKbTwo();
    }
    /**
     * 初始化图表 初始化4条线
     *
     * @param
     */
    private void generateData() {

        List<Line> lines = new ArrayList<Line>();
        line1.setStrokeWidth(2);
        line1.setColor(Color.parseColor("#29BD25"));
        line1.setShape(ValueShape.CIRCLE);
        line1.setCubic(true);//曲线是否平滑，即是曲线还是折线
        line1.setFilled(false);//是否填充曲线的面积
        line1.setHasLabels(true);//曲线的数据坐标是否加上备注
        line1.setHasLabelsOnlyForSelected(true);//点击数据坐标提示数据（设置了这个line.setHasLabels(true);就无效）
        line1.setHasLines(true);//是否用线显示。如果为false 则没有曲线只有点显示
        line1.setHasPoints(false);//是否显示圆点 如果为false 则没有原点只有点显示（每个数据点都是个大的圆点）
        lines.add(line1);

        line2.setStrokeWidth(2);
        line2.setColor(Color.parseColor("#DF45E3"));
        line2.setShape(ValueShape.CIRCLE);
        line2.setCubic(true);//曲线是否平滑，即是曲线还是折线
        line2.setFilled(false);//是否填充曲线的面积
        line2.setHasLabels(true);//曲线的数据坐标是否加上备注
        line2.setHasLabelsOnlyForSelected(true);//点击数据坐标提示数据（设置了这个line.setHasLabels(true);就无效）
        line2.setHasLines(true);//是否用线显示。如果为false 则没有曲线只有点显示
        line2.setHasPoints(false);//是否显示圆点 如果为false 则没有原点只有点显示（每个数据点都是个大的圆点）
        lines.add(line2);

        line0.setStrokeWidth(2);
        line0.setColor(Color.parseColor("#FAFF4D"));
        line0.setShape(ValueShape.CIRCLE);
        line0.setCubic(true);//曲线是否平滑，即是曲线还是折线
        line0.setFilled(false);//是否填充曲线的面积
        line0.setHasLabels(true);//曲线的数据坐标是否加上备注
        line0.setHasLabelsOnlyForSelected(true);//点击数据坐标提示数据（设置了这个line.setHasLabels(true);就无效）
        line0.setHasLines(true);//是否用线显示。如果为false 则没有曲线只有点显示
        line0.setHasPoints(false);//是否显示圆点 如果为false 则没有原点只有点显示（每个数据点都是个大的圆点）
        lines.add(line0);
        LineChartData data = new LineChartData(lines);
        if (hasAxes) {
            axisX = new Axis().setHasLines(true).setLineColor(Color.parseColor("#2b4d61"));
            axisY = new Axis().setHasLines(true).setLineColor(Color.parseColor("#2b4d61"));
            axisX.setMaxLabelChars(20);
            axisX.setMaxLabelChars(2);
            data.setAxisXBottom(axisX);
            data.setAxisYLeft(axisY);
        } else {
            data.setAxisXBottom(null);
            data.setAxisYLeft(null);
        }
        data.setBaseValue(Float.NEGATIVE_INFINITY);
        line_chart.setInteractive(true);
        line_chart.setZoomType(ZoomType.HORIZONTAL);
        line_chart.setMaxZoom((float) 2);//最大方法比例
        line_chart.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);
        line_chart.setLineChartData(data);//刷新数据
        line_chart.setVisibility(View.VISIBLE);
        Viewport v = new Viewport(line_chart.getMaximumViewport());
        v.left = 0;
        v.right = 10;
        line_chart.setCurrentViewport(v);
        line_chart.setMaximumViewport(v);

    }
    /**
     * 动态添加点到图表
     * @param light
     *
     */
    private void addDataPoint(float light,float tempreture,float humidity) {
        //这里先做一个记录最大值的函数

        maxlight=Data_untils.record_max(maxlight,light);
        List<PointValue> values0 = line0.getValues();
        List<PointValue> values1 = line1.getValues();
        List<PointValue> values2 = line2.getValues();
        startIndex = values0.size();
        values0.add(new PointValue(startIndex,light));
        values1.add(new PointValue(startIndex,tempreture));
        values2.add(new PointValue(startIndex,humidity));
        line0.setValues(values0);
        line1.setValues(values1);
        line2.setValues(values2);
        List<Line> lines = new ArrayList<>();
        lines.add(line0);
        lines.add(line1);
        lines.add(line2);
        LineChartData lineData = new LineChartData(lines);
        lineData.setAxisXBottom(axisX);
        lineData.setAxisYLeft(axisY);
        line_chart.setLineChartData(lineData);
        Viewport port = null;
        port = initViewPort_change(maxlight);
        //根据点的横坐标实时变换X坐标轴的视图范围
        line_chart.setMaximumViewport(port);
        line_chart.setCurrentViewport(port);

    }
    private Viewport initViewPort_change(float max) {
        Viewport port = new Viewport();//rsrp,rsii,rsrq,sinr
        port.top =max+20;//Y轴上限，固定(不固定上下限的话，Y轴坐标值可自适应变化)
        port.bottom =0;//Y轴下限，固定
        port.left = (float) 0;//X轴左边界，变化
        port.right = startIndex+10;//X轴右边界，变化
        return port;
    }


}
