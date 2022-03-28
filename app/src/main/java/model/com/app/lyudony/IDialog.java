package model.com.app.lyudony;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TimePicker;


/**
 * Author by Lyu
 * Date on 2021/7/20-17:24
 * Description:弹框
 */
public class IDialog extends Dialog {

    //    style引用style样式
    public IDialog(Context context, int style) {
        super(context, style);
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_time_selector);
        TimePicker timePicker = findViewById(R.id.time_picker);
        timePicker.setIs24HourView(true); //设置24小时制
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int i, int i1) {
                if (mOnTimePickerClickListener != null) {
                    String hour = "";
                    if (i == 1) hour = "01";
                    else if (i == 2) hour = "02";
                    else if (i == 3) hour = "03";
                    else if (i == 4) hour = "04";
                    else if (i == 5) hour = "05";
                    else if (i == 6) hour = "06";
                    else if (i == 7) hour = "07";
                    else if (i == 8) hour = "08";
                    else if (i == 9) hour = "09";
                    else if (i == 0) hour = "24";
                    else hour = "" + i;

                    String min = "";
                    if (i1 == 0) min = "00";
                    else if (i1 == 1) min = "01";
                    else if (i1 == 2) min = "02";
                    else if (i1 == 3) min = "03";
                    else if (i1 == 4) min = "04";
                    else if (i1 == 5) min = "05";
                    else if (i1 == 6) min = "06";
                    else if (i1 == 7) min = "07";
                    else if (i1 == 8) min = "08";
                    else if (i1 == 9) min = "09";
                    else min = "" + i1;
                    Log.e("TAG", "-----hour------" + hour + "-------min------" + min);
                    mOnTimePickerClickListener.setTime(hour, ":" + min);
                }
            }
        });
    }

    //接口回调
    private OnTimePickerClickListener mOnTimePickerClickListener;

    public interface OnTimePickerClickListener {
        void setTime(String hour, String minute);
    }

    public void setOnTimePickerClickListener(OnTimePickerClickListener onTimePickerClickListener) {
        mOnTimePickerClickListener = onTimePickerClickListener;
    }
}
