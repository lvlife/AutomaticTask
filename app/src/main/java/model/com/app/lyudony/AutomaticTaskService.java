package model.com.app.lyudony;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.KeyguardManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;


import androidx.core.app.NotificationCompat;


import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Author by Lyu
 * Date on 2021/7/20-10:24
 * Description:自动服务
 */
public class AutomaticTaskService extends Service {

    private static final String TAG = "AutomaticTaskService";
    private static final int NOTIFICATION = R.string.tip;

    private static final int ONE_MINUTE = 20 * 1000;
    private static final int PENDING_REQUEST = 0;

    private NotificationManager notificationManager;
    private MediaPlayer bgMediaPlayer;
    private boolean isRun = true;


    @Override
    public void onCreate() {
        super.onCreate();
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Log.e(TAG, "-------------onCreate");
    }

    @SuppressLint("NewApi")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "-------------onStartCommand");
        final Context context = this;
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setSmallIcon(R.mipmap.timer_task);
        builder.setContentTitle("新消息").setContentText("这是一条定时任务");
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.timer_task));
        // 8.0设置Notification的Channel_ID,否则不能正常显示
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId("notification_id");
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel("notification_id", "notification_name", NotificationManager.IMPORTANCE_LOW);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);
        }
        //启用前台服务 其目的是为了保活 提高Service的优先级
        startForeground(1, builder.build());


        //2.开启线程（或者需要定时操作的事情）
        new Thread() {
            @Override
            public void run() {
                super.run();
                while (isRun) {
                    //你需要执行的任务
                    try {
                        Thread.sleep(50000);
                    } catch (InterruptedException es) {
                        es.printStackTrace();
                    }

                    //实例化SharedPreferences
                    SharedPreferences mySharedPreferences = getSharedPreferences("shared", Activity.MODE_PRIVATE);

                    String starTime = mySharedPreferences.getString("startTime", "");
                    String endTime = mySharedPreferences.getString("endTime", "");

                    Log.e(TAG, " starTime -------- " + starTime);
                    Log.e(TAG, " endTime  --------- " + endTime);

                    String strTime = getSystemTime() + "";
                    strTime = strTime.substring(0, 5);
                    Log.e(TAG, " 系统时间 --------- " + strTime);
                    if (strTime.equals(starTime)) {
                        //设置了上班打卡时间
                        ImplementUtils.openTask(getBaseContext());
                        wakeUpAndUnlock(context);
                    } else if (strTime.equals(endTime)) {
                        //设置了下班打卡时间
                        ImplementUtils.openTask(getBaseContext());
                        wakeUpAndUnlock(context);
                    } else if (strTime.equals("10:00")) {
                        //设置断开连接
                        Log.e(TAG, "执行了 断开连接");
                        SilentUtils.launchApp(getApplication().getBaseContext(), "model.com.app.lyudony");
                        wakeUpAndUnlock(context);
                    } else if (strTime.equals("22:00")) {
                        //设置断开连接
                        Log.e(TAG, "执行了 断开连接");
                        SilentUtils.launchApp(getApplication().getBaseContext(), "model.com.app.lyudony");
                        wakeUpAndUnlock(context);
                    }
                }
            }
        }.start();

        if (bgMediaPlayer == null) {
            //通过音乐播放器的模式 只要在播放状态下，就算休眠也不会被杀
            bgMediaPlayer = MediaPlayer.create(this, R.raw.silent);
            //设置循环播放
            bgMediaPlayer.setLooping(true);
            bgMediaPlayer.start();
        }

        //走OnDestory停止的服务，系统保留了service的onStartCommand方法中的变量，等待系统重启此服务
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        isRun = false;
        stopForeground(true);
        if (bgMediaPlayer != null) {
            bgMediaPlayer.release();
            bgMediaPlayer = null;
        }
        stopSelf();
        notificationManager.cancel(NOTIFICATION);
        super.onDestroy();
        Log.e(TAG, "-------------onDestroy");
    }

    @SuppressLint("SimpleDateFormat")
    public static String getSystemTime() {
        return new SimpleDateFormat("kk:mm:ss").format(new Date(System.currentTimeMillis()));
    }

    @SuppressLint("InvalidWakeLockTag")
    public static void wakeUpAndUnlock(Context context) {
        //屏锁管理器
        KeyguardManager km = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock kl = km.newKeyguardLock("unLock");
        //解锁
        kl.disableKeyguard();
        //获取电源管理器对象
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        //获取PowerManager.WakeLock对象,后面的参数|表示同时传入两个值,最后的是LogCat里用的Tag
        assert pm != null;
        final PowerManager.WakeLock wakeLock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP |
                PowerManager.SCREEN_DIM_WAKE_LOCK, "bright");
        //点亮屏幕
        wakeLock.acquire();
        wakeLock.release();

    }
}
