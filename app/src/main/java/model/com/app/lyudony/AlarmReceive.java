package model.com.app.lyudony;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Author by Lyu
 * Date on 2021/7/26-11:03
 * Description:自启
 */
public class AlarmReceive extends BroadcastReceiver {

    public AlarmReceive() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        //此处及是重启的之后，打开我们app的方法
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            Intent intent2= new Intent(context, MainActivity.class);
            //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 非常重要，如果缺少的话，程序将在启动时报错
            intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //自启动APP（Activity）
            context.startActivity(intent2);
            //自启动服务（Service）
            //context.startService(intent);
        }
    }

}
