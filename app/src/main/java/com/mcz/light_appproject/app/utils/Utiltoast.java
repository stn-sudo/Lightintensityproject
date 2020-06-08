package com.mcz.light_appproject.app.utils;

import android.content.Context;
import android.widget.Toast;

public class Utiltoast {

    private static Toast toast;

    public static void showToast(Context context, String content) {
        if (toast == null) {
            toast = Toast.makeText(context,
                    content,
                    Toast.LENGTH_SHORT);
        } else {
            toast.cancel(); //取消
            toast = Toast.makeText(context,
                    content,
                    Toast.LENGTH_SHORT);
        }
        toast.show();
    }

}