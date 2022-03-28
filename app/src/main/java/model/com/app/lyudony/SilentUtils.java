package model.com.app.lyudony;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import model.com.app.processutil.AndroidAppProcess;
import model.com.app.processutil.ProcessManager;

import java.util.List;

/**
 * Author by Lyu
 * Date on 2021/7/23-17:56
 * Description:
 */
public class SilentUtils {

    public static boolean isForeground(Context context, String packageName) {
        return getLinuxCoreInfo(context, packageName);
    }

    /**
     * 无意中看到乌云上有人提的一个漏洞，Linux系统内核会把process进程信息保存在/proc目录下，
     * 使用Shell命令去获取的他，再根据进程的属性判断是否为前台
     *
     * @param packageName 需要检查是否位于栈顶的App的包名
     */
    public static boolean getLinuxCoreInfo(Context context, String packageName) {

        List<AndroidAppProcess> processes = ProcessManager.getRunningForegroundApps(context);
        for (AndroidAppProcess appProcess : processes) {
            if (appProcess.getPackageName().equals(packageName) && appProcess.foreground) {
                return true;
            }
        }
        return false;

    }


    public static void launchApp(Context context, String packageName) {
        try {
            Intent intent = new Intent();
            //通过包名启动
            PackageManager packageManager = context.getPackageManager();
            intent = packageManager.getLaunchIntentForPackage(packageName);
            if (null != intent) {
                context.startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
