package edu.sysu.showtime.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

public class ApplyForPermission extends Activity {
    public static void applyPermission(Context context, String[] permissionLists, int request_code)
    {
        for(String permission : permissionLists){
            //验证每一个权限是否都许可了权限
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                //申请permission权限
                ActivityCompat.requestPermissions((Activity) context, permissionLists, request_code);
            }
        }
    }
}
