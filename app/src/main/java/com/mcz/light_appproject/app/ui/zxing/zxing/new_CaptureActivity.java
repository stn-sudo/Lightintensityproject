package com.mcz.light_appproject.app.ui.zxing.zxing;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.google.zxing.Result;
import com.gyf.barlibrary.ImmersionBar;
import com.mcz.light_appproject.R;
import com.mcz.light_appproject.app.ui.activity.InputCodeActivity;
import com.yzq.zxinglibrary.android.CaptureActivity;

import java.net.URLEncoder;

/**
 * Iot club all rights reserved
 * Created by Iot club on 2018/12/14.
 */

public class new_CaptureActivity extends CaptureActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ImmersionBar.with(this)
                .statusBarColor(R.color.transparent)
                .fitsSystemWindows(true)
                .init();
    }
    public static void gowxScan(Context context){
        try {
            Intent intent = context.getPackageManager().getLaunchIntentForPackage("com.tencent.mm");
            intent.putExtra("LauncherUI.From.Scaner.Shortcut", true);
            context.startActivity(intent);

        } catch (Exception e) {

        }
    }
    private void toWeChatScan(String result) {//weixin://dl/business/?ticket=Di1fR1fEmgrlrRMk93gc
        try {
            String[] other = result.split(":");
            if(other[0].trim().equals("http")|| other[0].trim().equals("https")) {
                Uri uri = Uri.parse(result);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }

        } catch (Exception e) {
            //若无法正常跳转，在此进行错误处理
        }
    }
    @Override
    public void handleDecode(Result rawResult) {
        super.handleDecode(rawResult);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        if (rawResult.getText() == null) {
            Log.i(" handleDecode", "resultString is null");
            Toast.makeText(new_CaptureActivity.this, "Scan failed!", Toast.LENGTH_SHORT).show();
        } else {
            String result = rawResult.getText();
            toWeChatScan(result);
//            String imei=rawResult.getText().substring(0,rawResult.getText().indexOf(";"));
//            Intent intent = new Intent(getApplication(), InputCodeActivity.class);
//            intent.putExtra("json", imei);
//            startActivity(intent);
        }
    }
}
