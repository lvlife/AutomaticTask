package model.com.app.lyudony;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.app.Service;
import android.util.Log;

/**
 * Author by Lyu
 * Date on 2021/7/20-11:24
 * Description:电话呼叫 (暂未启用)
 */
public class TelephoneCallReceiver extends BroadcastReceiver {
    private static final String TAG = "TelephoneCallReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        // 如果是拨打电话
        if (intent.getAction() != null) {
            if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
                String phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
                Log.i(TAG, "call OUT:" + phoneNumber);
            } else {
                // 如果是来电
                TelephonyManager tManager = (TelephonyManager) context
                        .getSystemService(Service.TELEPHONY_SERVICE);
                assert tManager != null;
                switch (tManager.getCallState()) {
                    // 来电铃响时
                    case TelephonyManager.CALL_STATE_RINGING:
                        String incomingNumber = intent.getStringExtra("incoming_number");
                        //用你的电话往这个手机打电话就可以唤醒
                        if (incomingNumber != null && incomingNumber.equals("你的电话号码")) {
                            ImplementUtils.openTask(context);
                        }
                        break;
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                        break;
                    // 无任何状态
                    case TelephonyManager.CALL_STATE_IDLE:
                        break;
                }
            }
        }
    }
}