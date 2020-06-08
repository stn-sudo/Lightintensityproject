package com.mcz.light_appproject.app.ui.activity;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.mcz.light_appproject.MainActivity;
import com.mcz.light_appproject.R;
import com.mcz.light_appproject.app.ui.base.LoginBaseActivity;
import com.mcz.light_appproject.app.utils.Config;
import com.mcz.light_appproject.app.utils.DataManager;

import org.json.JSONObject;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.view.View.VISIBLE;
import static com.mcz.light_appproject.app.utils.Config.APPID;
import static com.mcz.light_appproject.app.utils.Config.IP;
import static com.mcz.light_appproject.app.utils.Config.PORT;
import static com.mcz.light_appproject.app.utils.Config.SECRET;

/**
 * Iot club all rights reserved
 * Created by Iot club on 2018/12/14.
 */

public class LoginActivity extends LoginBaseActivity {


    @BindView(R.id.ed_service_address)
    EditText edServiceAddress;
    @BindView(R.id.iv_clean_address)
    ImageView ivCleanAddress;
    @BindView(R.id.et_username)
    EditText etUsername;
    @BindView(R.id.iv_clean_username)
    ImageView ivCleanUsername;
    @BindView(R.id.imageView3)
    ImageView imageView3;
    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.clean_password)
    ImageView cleanPassword;
    @BindView(R.id.iv_show_pwd)
    ImageView ivShowPwd;
    @BindView(R.id.btn_login)
    Button btnLogin;

    @BindView(R.id.ed_service_port)
    EditText edt_port;
    @BindView(R.id.iv_clean_port)
    ImageView img_cleanport;

    SharedPreferences sp;
    String user = "", pwd = "", address = "",port="";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        ButterKnife.bind(this);
        //设置沉浸式标题栏
        setStatusBar();
        init();
    }

    /**
     * 初始化设置默认参数配置
     * 设置清楚按钮可以用
     */
    private void init() {
        String usname = sp.getString("appId", APPID);
        String uspwd = sp.getString("userpwd", SECRET);
        String usaddress = sp.getString("seraddress", IP);
        String usport = sp.getString("post", PORT);
        if (!usaddress.equals("")) {
            edServiceAddress.setText(usaddress);
            edServiceAddress.setSelection(usaddress.length());
        }
        if (!usname.equals("")) {
            edt_port.setText(usport);
        }
        if (!usname.equals("")) {
            etUsername.setText(usname);
        }
        if (!uspwd.equals("")) {
            etPassword.setText(uspwd);
        }
//        ivCleanAddress.setVisibility(VISIBLE);
//        ivCleanUsername.setVisibility(VISIBLE);
//        cleanPassword.setVisibility(VISIBLE);
//        img_cleanport.setVisibility(VISIBLE);
    }

    /**
     * @param view
     * 点击按钮清空
     */
    @OnClick({R.id.iv_clean_address, R.id.iv_clean_username, R.id.clean_password, R.id.iv_show_pwd, R.id.iv_clean_port, R.id.btn_login})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_clean_address:
                edServiceAddress.setText("");
                break;
            case R.id.iv_clean_port:
                edt_port.setText("");
                break;
            case R.id.iv_clean_username:
                etUsername.setText("");
                break;
            case R.id.clean_password:
                etPassword.setText("");
                break;
            case R.id.iv_show_pwd:
                pwd_show_A_hide();
                break;
            case R.id.btn_login:
                //登录
                Login_into();
                break;
        }
    }

    /**
     * 密码显示与隐藏
     */
    private void pwd_show_A_hide() {
        if (etPassword.getInputType() != InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
            etPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            ivShowPwd.setImageResource(R.drawable.img_show_pwd);
        } else {
            etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            ivShowPwd.setImageResource(R.drawable.img_hide_pwd);
        }
        String pwds = etPassword.getText().toString();
        if (!TextUtils.isEmpty(pwds))
            etPassword.setSelection(pwds.length());
    }

    private void Login_into() {
        address = edServiceAddress.getText().toString().trim();
        port=edt_port.getText().toString().trim();
        user = etUsername.getText().toString().trim();
        pwd = etPassword.getText().toString().trim();
        Config.all_url="https://"+address+":"+port;
        if (TextUtils.isEmpty(address)) {
            Toast.makeText(LoginActivity.this, "请输入IP", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(port)){
            Toast.makeText(LoginActivity.this, "请输入端口号", Toast.LENGTH_SHORT).show();
        }
        else {
            if (TextUtils.isEmpty(user) || TextUtils.isEmpty(pwd)) {
                Toast.makeText(LoginActivity.this, "用户信息不能为空", Toast.LENGTH_SHORT).show();
            } else {
                    Init_Demand_HTTPS();
            }
        }
    }

    /**
     *
     * 用户信息请求
     */
    private void Init_Demand_HTTPS() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                Message msg = new Message();
                Bundle data = new Bundle();
                try {
                    String url="https://"+address+":"+port+"/iocm/app/sec/v1.1.0/login";
                    String json= DataManager.Login_Request(LoginActivity.this,url,user,pwd);
                    data.putString("json", json);
                } catch ( Exception e ) {
                    data.putString("errmsg", e.getMessage());
                }
                msg.setData(data);
                loginhandler.sendMessage(msg);
            }
        }).start();


    }

    private Handler loginhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            String json = data.getString("json");
            Log.i("aaa", "josn1" + json);
//            {
//                "accessToken": "dc4d1368-2e43-4185-b22b-13714e22eb92",
//                    "tokenType": "bearer",
//                    "refreshToken": "a769565a-7d0a-4079-9fe3-1a97b84309a8",
//                    "expiresIn": 43200,
//                    "scope": "[read, write]"
//            }
            String token="";
            try {
                JSONObject jsono = new JSONObject(json);
                token=jsono.getString("accessToken");
                sp.edit().putString("token",token).commit();
                sp.edit().putString("appId",etUsername.getText().toString().trim()).commit();
                sp.edit().putString("userpwd",etPassword.getText().toString().trim()).commit();
                sp.edit().putString("seraddress",edServiceAddress.getText().toString().trim()).commit();
                sp.edit().putString("post",edt_port.getText().toString().trim()).commit();
            }catch (Exception e ){
                e.printStackTrace();
                Toast.makeText(LoginActivity.this, "请求失败,请检查参数设置！", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent=new Intent(getApplication(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    };



}
