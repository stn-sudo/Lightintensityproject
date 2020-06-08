package com.mcz.light_appproject.app.model;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.mcz.light_appproject.MainActivity;
import com.mcz.light_appproject.R;
import com.mcz.light_appproject.app.HistoricaldataActivity;
import com.mcz.light_appproject.app.utils.Config;
import com.mcz.light_appproject.app.utils.DataManager;
import com.mcz.light_appproject.app.utils.Ocean_method;
import com.mcz.light_appproject.app.utils.Utiltoast;
import com.mcz.light_appproject.app.view.view.SwipeLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Iot club all rights reserved
 * Created by Iot club on 2018/12/14.
 */

public class PullrefreshListviewAdapter extends BaseAdapter {
    public  Context mContext;
    private List<DataInfo> mlist=null;
    private String login_appid = "";
    private String token = "";
    private List<String> gatewayId=new ArrayList<String>();
    private List<String> deviceId=new ArrayList<String>();
    private final String tuatus="201";
    private final String deletetuatus="204";
    private String json=null;
    public  ViewHolder vh;
    private Ocean_method ocean_method;
    public  Switch switch_led;
    public  Switch switch_motor;
    public  Switch switch_beep;

    public  PullrefreshListviewAdapter( Context context){
         this.mContext=context;
         mlist=new ArrayList<DataInfo>();
    }

    public void settoken(String st){this.token=st;}
    public void setlogin_appid(String sl){this.login_appid=sl;}
    public void clearItem() {
        mlist.clear();
    }
    public void addItem(DataInfo item) {
        mlist.add(item);
    }
    @Override
    public int getCount() {
        return mlist.size();
    }

    @Override
    public DataInfo getItem(int position) {
        DataInfo item = null;
        if (position >= 0 && getCount() > position) {
            item = mlist.get(position);
        }
        return item;
    }


    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.main_listitemagriculture, parent, false);
            vh = new ViewHolder();
            vh.tv_name = (TextView) convertView.findViewById(R.id.txt_name_data);
            vh.tv_stutas = (TextView) convertView.findViewById(R.id.txt_status_data);
            vh.tv_DeviceLight = (TextView) convertView.findViewById(R.id.light);
            vh.tv_DeviceTemperature = (TextView) convertView.findViewById(R.id.temperature);
            vh.tv_DeviceHumidity = (TextView) convertView.findViewById(R.id.humidity);
            vh.swipeLayout = (SwipeLayout) convertView.findViewById(R.id.swipe);
            vh.img_stutas = (ImageView) convertView.findViewById(R.id.img_status);
            vh.deng_s = (ImageView) convertView.findViewById(R.id.deng);
            vh.laba_s = (ImageView) convertView.findViewById(R.id.laba);
            vh.dianji_s = (ImageView) convertView.findViewById(R.id.dianji);
            switch_led = (Switch) convertView.findViewById(R.id.switch_led);
            switch_motor = (Switch) convertView.findViewById(R.id.switch_motor);
            switch_beep = (Switch) convertView.findViewById(R.id.switch_beep);
            convertView.setTag(vh);
            ocean_method = new Ocean_method(login_appid, token);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        DataInfo info = getItem(position);
        vh.tv_name.setText(info.getDeviceName());
        vh.tv_DeviceLight.setText(info.getDevicelight());
        vh.tv_DeviceTemperature.setText(info.getDevicetemperature());
        vh.tv_DeviceHumidity.setText(info.getDevicehumidity());


        if (info.getDeviceStatus().contains("OFFLINE")){
            vh.tv_stutas.setText("离线");
            vh.img_stutas.setImageDrawable(ContextCompat.getDrawable(mContext,R.drawable.img_red_dot));
        }else if(info.getDeviceStatus().contains("ONLINE"))
            {
            vh.tv_stutas.setText("在线");
            vh.img_stutas.setImageDrawable(ContextCompat.getDrawable(mContext,R.drawable.img_green_dot));
        }
        else if(info.getDeviceStatus().contains("ABNORMAL"))
        {
            vh.tv_stutas.setText("异常");
            vh.img_stutas.setImageDrawable(ContextCompat.getDrawable(mContext,R.drawable.img_red));
        }
        ison_light(info.isLed_s());
        ison_motor(info.ismotor_s());
        if(!deviceId.contains(info.getDeviceId()))
        {
            deviceId.add(info.getDeviceId());
        }
        if(!gatewayId.contains(info.getGatewayId()))
        {
            gatewayId.add(info.getGatewayId());
        }
        vh.swipeLayout.setClickToClose(true);
        vh.swipeLayout.close(true);
        vh.swipeLayout.setOnDoubleClickListener(new SwipeLayout.DoubleClickListener() {
            @Override
            public void onDoubleClick(SwipeLayout layout, boolean surface) {
                Intent mIntent = new Intent(mContext, HistoricaldataActivity.class);
//                String deviceId = mlist.get(position).getDeviceId();
//                String gatewayId = mlist.get(position).getGatewayId();
                mIntent.putExtra("deviceId", deviceId.get(position));
                mIntent.putExtra("gatewayId", gatewayId.get(position));
                mContext.startActivity(mIntent);

            }
        });
//
//        class ToastShow {
//            private Context context;
//            private Toast toast = null;
//            public ToastShow(Runnable context) {
//                this.context = context;
//            }
//            public void toastShow(String text) {
//                if(toast == null)
//                {
//                    toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
//                }
//                else {
//                    toast.setText(text);
//                }
//                toast.show();
//            }
//        }
//        new ToastShow(ToastShow.context);

        ImageView trash=(ImageView)convertView.findViewById(R.id.trash);
        final View finalConvertView = convertView;
        trash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                       String add_url = Config.all_url + "/iocm/app/dm/v1.1.0/devices/"+deviceId.get(position)+"?appId=" + login_appid;
                        try {
                            json= DataManager.Delete_DEVICEID(mContext,add_url, login_appid, token);
                            if(json.equals(deletetuatus))
                            {
                                Looper.prepare();
                                mlist.remove(position);

                                Utiltoast.showToast(finalConvertView.getContext(),"删除设备成功");
                                //Toast.makeText(finalConvertView.getContext(),"删除设备成功",Toast.LENGTH_SHORT).show();
                                deviceId.remove(position);
                                Message message=new Message();
                                message.what=1;
                                handler.sendMessage(message);
                                Looper.loop();
                            }
                            else
                            {
                                Looper.prepare();
                                Utiltoast.showToast(finalConvertView.getContext(),"删除设备失败");
                                //Toast.makeText(finalConvertView.getContext(),"删除设备失败",Toast.LENGTH_SHORT).show();
                                Looper.loop();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });

        switch_led.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(final CompoundButton buttonView, boolean isChecked) {
               if (isChecked)
               {
                   new Thread(new Runnable() {
                       @Override
                       public void run() {
                           String add_url = Config.all_url + "/iocm/app/cmd/v1.4.0/deviceCommands?appId=" + login_appid;
                           try {
                               json = DataManager.ElightComened_DEVICEID(mContext, add_url, login_appid, token,deviceId.get(position),"ON");
                               Log.v("MainActivity",json);
                               if (json.equals(tuatus))
                               {
                                   Message message=new Message();
                                   Bundle bundle = new Bundle();
                                   bundle.putInt("position", position);// 将服务器返回的订单号传到Bundle中，，再通过handler传出
                                   message.setData(bundle);
                                   message.what=2;
                                   handler.sendMessage(message);

                                   Looper.prepare();
                                   Utiltoast.showToast(finalConvertView.getContext(),"开灯成功");
                                   vh.deng_s.setImageDrawable(ContextCompat.getDrawable(mContext,R.drawable.deng));
                                   Looper.loop();
                               }
                               else
                               {
                                   Looper.prepare();
                                   Utiltoast.showToast(finalConvertView.getContext(),"开灯失败");
                                   Looper.loop();

                               }


                           } catch (Exception e) {
                               e.printStackTrace();
                           }



                       }
                   }).start();

               }
               else
               {
                   new Thread(new Runnable() {
                       @Override
                       public void run() {

                           String add_url = Config.all_url + "/iocm/app/cmd/v1.4.0/deviceCommands?appId=" + login_appid;
                           try {

                                json = DataManager.ElightComened_DEVICEID(mContext, add_url, login_appid, token,deviceId.get(position),"OFF");
                               if (json.equals(tuatus))
                               {
                                   Message message=new Message();
                                   Bundle bundle = new Bundle();
                                   bundle.putInt("position", position);// 将服务器返回的订单号传到Bundle中，，再通过handler传出
                                   message.setData(bundle);
                                   message.what=2;
                                   handler.sendMessage(message);

                                   Looper.prepare();
                                   Utiltoast.showToast(finalConvertView.getContext(),"关灯成功");
                                   vh.deng_s.setImageDrawable(ContextCompat.getDrawable(mContext,R.drawable.deng1));
                                   Looper.loop();
                               }
                               else
                               {
                                   Looper.prepare();
                                   Utiltoast.showToast(finalConvertView.getContext(),"关灯失败");
                                   Looper.loop();
                               }

                           } catch (Exception e) {
                               e.printStackTrace();
                           }


                       }
                   }).start();

               }
            }
        });

        switch_motor.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(final CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            String add_url = Config.all_url + "/iocm/app/cmd/v1.4.0/deviceCommands?appId=" + login_appid;
                            try {

                                json = DataManager.MOTORComened_DEVICEID(mContext, add_url, login_appid, token,deviceId.get(position),"ON");
                                if (json.equals(tuatus))
                                {

                                    Message message=new Message();
                                    Bundle bundle = new Bundle();
                                    bundle.putInt("position", position);// 将服务器返回的订单号传到Bundle中，，再通过handler传出
                                    message.setData(bundle);
                                    message.what=2;
                                    handler.sendMessage(message);

                                    Looper.prepare();
                                    Utiltoast.showToast(finalConvertView.getContext(),"开电机成功");
                                    vh.dianji_s.setImageDrawable(ContextCompat.getDrawable(mContext,R.drawable.dianji));
                                    Looper.loop();

                                }
                                else
                                {
                                    Looper.prepare();
                                    Utiltoast.showToast(finalConvertView.getContext(),"开电机失败");
                                    Looper.loop();

                                }


                            } catch (Exception e) {
                                e.printStackTrace();
                            }



                        }
                    }).start();

                }
                else
                {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            String add_url = Config.all_url + "/iocm/app/cmd/v1.4.0/deviceCommands?appId=" + login_appid;
                            try {

                                json = DataManager.MOTORComened_DEVICEID(mContext, add_url, login_appid, token,deviceId.get(position),"OFF");
                                if (json.equals(tuatus))
                                {
                                    Message message=new Message();
                                    Bundle bundle = new Bundle();
                                    bundle.putInt("position", position);// 将服务器返回的订单号传到Bundle中，，再通过handler传出
                                    message.setData(bundle);
                                    message.what=2;
                                    handler.sendMessage(message);

                                    Looper.prepare();
                                    Utiltoast.showToast(finalConvertView.getContext(),"关电机成功");
                                    vh.dianji_s.setImageDrawable(ContextCompat.getDrawable(mContext,R.drawable.dianji1));
                                    Looper.loop();
                                }
                                else
                                {
                                    Looper.prepare();
                                    Utiltoast.showToast(finalConvertView.getContext(),"关电机失败");
                                    Looper.loop();
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                        }
                    }).start();

                }
            }
        });

        switch_beep.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(final CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            String add_url = Config.all_url + "/iocm/app/cmd/v1.4.0/deviceCommands?appId=" + login_appid;
                            try {

                                json = DataManager.BEEPComened_DEVICEID(mContext, add_url, login_appid, token,deviceId.get(position),"ON");
                                if (json.equals(tuatus))
                                {

                                    Message message=new Message();
                                    Bundle bundle = new Bundle();
                                    bundle.putInt("position", position);// 将服务器返回的订单号传到Bundle中，，再通过handler传出
                                    message.setData(bundle);
                                    message.what=2;
                                    handler.sendMessage(message);

                                    Looper.prepare();
                                    Utiltoast.showToast(finalConvertView.getContext(),"开蜂鸣器成功");
                                    Looper.loop();



                                }
                                else
                                {
                                    Looper.prepare();
                                    Utiltoast.showToast(finalConvertView.getContext(),"开蜂鸣器失败");
                                    Looper.loop();

                                }


                            } catch (Exception e) {
                                e.printStackTrace();
                            }



                        }
                    }).start();

                }
                else
                {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            String add_url = Config.all_url + "/iocm/app/cmd/v1.4.0/deviceCommands?appId=" + login_appid;
                            try {

                                json = DataManager.BEEPComened_DEVICEID(mContext, add_url, login_appid, token,deviceId.get(position),"OFF");
                                if (json.equals(tuatus))
                                {
                                    Message message=new Message();
                                    Bundle bundle = new Bundle();
                                    bundle.putInt("position", position);// 将服务器返回的订单号传到Bundle中，，再通过handler传出
                                    message.setData(bundle);
                                    message.what=2;
                                    handler.sendMessage(message);

                                    Looper.prepare();
                                    Utiltoast.showToast(finalConvertView.getContext(),"关蜂鸣器失败");
                                    vh.laba_s.setImageDrawable(ContextCompat.getDrawable(mContext,R.drawable.laba1));
                                    Looper.loop();
                                }
                                else
                                {
                                    Looper.prepare();
                                    Utiltoast.showToast(finalConvertView.getContext(),"关蜂鸣器失败");
                                    Looper.loop();
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                        }
                    }).start();

                }
            }
        });

        return convertView;
    }
    public class Intenet extends AsyncTask<Integer, Integer, Integer> {
        private int position;

        public Intenet(int position) {
            this.position = position;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(Integer... integers) {
            int count = 0;
             int led_s= ocean_method.GET_Led_first_staus(mContext,deviceId.get(position));
             Log.i("doInBackground","=====led_s"+led_s);
             while (led_s == 2&&count<10)
             {
                 count++;
                 try {
                     Thread.sleep(1000);
                     led_s= ocean_method.GET_Led_first_staus(mContext,deviceId.get(position));
                     Log.i("doInBackground","=====led_s"+led_s);
                 } catch (InterruptedException e) {
                     e.printStackTrace();
                 }

             }

            return led_s;
        }
//        @Override
//        protected void onPostExecute(Integer led_s) {
//            super.onPostExecute(led_s);
//            Log.i("onPostExecute","=====led_s"+led_s);
//            led_s =1;
//            if(led_s==1)
//            {
//                Toast.makeText(mContext,"开灯成功",Toast.LENGTH_SHORT).show();
//                View view = null;
//                vh.deng_s.setImageDrawable(ContextCompat.getDrawable(mContext,R.drawable.deng));
//                switch_led.setEnabled(true);
//            }
//            else if(led_s==0)
//            {
//                Toast.makeText(mContext,"关灯成功",Toast.LENGTH_SHORT).show();
//                vh.deng_s.setImageDrawable(ContextCompat.getDrawable(mContext,R.drawable.deng1));
//                switch_led.setEnabled(true);
//            }
//            else
//            {
//                Toast.makeText(mContext,"无响应",Toast.LENGTH_SHORT).show();
//            }
//        }
    }
private Handler handler=new Handler()
{
    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
    switch (msg.what)
    {
        case 1:notifyDataSetChanged();
               vh.swipeLayout.close(true);
               break;
        case 2:
            Bundle bundle=msg.getData();
            get_Intenet(bundle.getInt("position"));
               break;
       default:break;
    }
    }

};
private void get_Intenet(int position)
{
    new Intenet(position).execute();
}
public void ison_light(boolean led)
{
    if(led)
    {
        vh.deng_s.setImageDrawable(ContextCompat.getDrawable(mContext,R.drawable.deng));
        switch_led.setChecked(true);
    }
    else
    {
        vh.deng_s.setImageDrawable(ContextCompat.getDrawable(mContext,R.drawable.deng1));
        switch_led.setChecked(false);
    }
}
public void ison_motor(boolean motor)
{
    if(motor)
    {
        vh.dianji_s.setImageDrawable(ContextCompat.getDrawable(mContext,R.drawable.dianji));
        switch_motor.setChecked(true);
    }
    else
    {
        vh.dianji_s.setImageDrawable(ContextCompat.getDrawable(mContext,R.drawable.dianji1));
        switch_motor.setChecked(false);
    }
}
    public class ViewHolder{
        public TextView tv_name;
        public TextView tv_stutas;
        public TextView tv_DeviceLight;
        public TextView tv_DeviceTemperature;
        public TextView tv_DeviceHumidity;
        public SwipeLayout swipeLayout;
        public ImageView img_stutas;
        public ImageView deng_s;
        public ImageView laba_s;
        public ImageView dianji_s;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
