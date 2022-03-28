package model.com.app.lyudony;


import java.util.List;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.util.Log;
import android.widget.Toast;

/**
 * Author by Lyu
 * Date on 2021/7/20-14:24
 * Description:跳转的业务
 */
public class ImplementUtils {
    private static final String TAG = "ImplementUtils";


    public static void openTask(Context context) {
        SharedPreferences mySharedPreferences = context.getSharedPreferences("shared", Activity.MODE_PRIVATE);
        String model = mySharedPreferences.getString("model", "");
        Log.e(TAG, "收到您设置的打卡模式为 ------- " + model);

        PackageManager packageManager = context.getPackageManager();
        PackageInfo packageInfo = null;
        try {
            if (model.equals("")) {
                Log.e(TAG, "未设置打卡模式");
                Toast.makeText(context, "请先设置打卡模式", Toast.LENGTH_LONG).show();
                return;
            }
            //获取企业微信的地址
            if (model.equals("wechat")) {
                packageInfo = packageManager.getPackageInfo("com.tencent.wework", 0);
            } else if (model.equals("dingding")) {
                //钉钉
                packageInfo = packageManager.getPackageInfo("com.alibaba.android.rimet", 0);
            }
        } catch (Exception ignored) {
            Log.e(TAG, "Exception ----" + ignored.getMessage());
        }
        //设置意图
        Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
        resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        if (packageInfo != null) {
            resolveIntent.setPackage(packageInfo.packageName);
        }
        List<ResolveInfo> apps = packageManager.queryIntentActivities(resolveIntent, 0);
        ResolveInfo resolveInfo = apps.iterator().next();
        if (resolveInfo != null && packageInfo != null) {
            String className = resolveInfo.activityInfo.name;
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Log.e(TAG, "packageName ------------" + packageInfo.packageName);
            ComponentName cn = new ComponentName(packageInfo.packageName, className);
            intent.setComponent(cn);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            try {
                pendingIntent.send();
            } catch (PendingIntent.CanceledException e) {
                e.printStackTrace();
                Log.e(TAG, "PendingIntent error --------" + e.getMessage());
            }

        }
    }
}