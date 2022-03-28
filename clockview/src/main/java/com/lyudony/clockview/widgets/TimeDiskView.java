package com.lyudony.clockview.widgets;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lyudony.clockview.utils.DisplayUtils;
import com.lyudony.clockview.utils.LogUtils;

import java.lang.ref.WeakReference;
import java.util.Calendar;

/**
 * 时钟圆盘View
 */

public class TimeDiskView extends ViewGroup {
    private static final String TAG = TimeDiskView.class.getName();
    Context mContext;
    HourDiskView hourDiskView;//hour
    MinuteDiskView minuteDiskView;//minute
    SecondDiskView secondDiskView;//second
    TextView tv_time;//记录时间的文字
    AmPmDiskView amPmDiskView;
    Calendar calendar;
    int hour24 = 1, hour = 1, minute = 0, second = 0;
    /**
     * 当前时间是上午还是下午，true==>下午
     */
    boolean isNoon = false;

    int screenWidth, screenHeight;

    /**
     * 是否关闭时间走时
     */
    boolean isStop = true;

    public TimeDiskView(Context context) {
        this(context, null);
    }

    public TimeDiskView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView(context, attrs);
    }

    final static int MSG_GET_CUR_TIME = 0x1001;//获取当前时间
    final static int MSG_UPDATE_TV_TIME = 0X1002;//更新用户设置的时间

    MyHandler handler;

    static class MyHandler extends Handler {

        private WeakReference<TimeDiskView> mWeakReference;

        public MyHandler(TimeDiskView timeDiskView) {
            mWeakReference = new WeakReference<>(timeDiskView);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            TimeDiskView timeDiskView = mWeakReference.get();
            if (timeDiskView == null) {
                return;
            }
            switch (msg.what) {
                case MSG_UPDATE_TV_TIME:
                    LogUtils.i(TAG, "minute==>" + timeDiskView.minute + "second==>" + timeDiskView.second);
                    if (timeDiskView.isNoon) {
                        timeDiskView.hour24 = timeDiskView.hour + 12;
                        if (timeDiskView.hour24 == 24) {
                            timeDiskView.hour24 = 0;
                        }
                    } else {
                        timeDiskView.hour24 = timeDiskView.hour;
                    }
                    if (timeDiskView.hour24 >= 10) {
                        if (timeDiskView.minute >= 10) {
                            timeDiskView.tv_time.setText(timeDiskView.hour24 + ":" + timeDiskView.minute);
                        } else {
                            timeDiskView.tv_time.setText(timeDiskView.hour24 + ":0" + timeDiskView.minute);
                        }
                    } else {
                        if (timeDiskView.minute >= 10) {
                            timeDiskView.tv_time.setText("0" + timeDiskView.hour24 + ":" + timeDiskView.minute);
                        } else {
                            timeDiskView.tv_time.setText("0" + timeDiskView.hour24 + ":0" + timeDiskView.minute);
                        }
                    }
                    break;
                case MSG_GET_CUR_TIME:
                    timeDiskView.showCurTime();
                    if (!timeDiskView.isStop) {
                        sendEmptyMessageDelayed(MSG_GET_CUR_TIME, 1000);
                        timeDiskView.handler.sendEmptyMessage(MSG_UPDATE_TV_TIME);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private void initView(Context context, AttributeSet attrs) {
        if (handler == null) {
            handler = new MyHandler(this);
        }

        //获取屏幕宽高
        screenWidth = DisplayUtils.getDisplayPxWidth(mContext);
        screenHeight = DisplayUtils.getDisplayPxHeight(mContext) + 100;
        //按比例分配各自时间圆盘的半径
        amPmDiskView = new AmPmDiskView(mContext, (int) (screenWidth * 0.8 / 2) / 3);

        hourDiskView = new HourDiskView(mContext, (int) (screenWidth * 0.8) / 2);
        LayoutParams hourParam = new LayoutParams((int) (screenWidth * 0.8),
                (int) (screenWidth * 0.8));
        hourDiskView.setLayoutParams(hourParam);

        minuteDiskView = new MinuteDiskView(mContext, (int) (screenWidth * 1.1) / 2);
        LayoutParams minuteParam = new LayoutParams((int) (screenWidth * 1.1),
                (int) (screenWidth * 1.1));
        minuteDiskView.setLayoutParams(minuteParam);

        secondDiskView = new SecondDiskView(mContext, (int) (screenWidth * 1.2) / 2);
        LayoutParams secondParam = new LayoutParams((int) (screenWidth * 1.2),
                (int) (screenWidth * 1.2));
        secondDiskView.setLayoutParams(secondParam);

        tv_time = new TextView(mContext);
        tv_time.setTextSize(DisplayUtils.sp2px(mContext, 20));
        tv_time.setTextColor(Color.WHITE);
        tv_time.setText("00:00");
        LayoutParams tv_timeParam = new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
        tv_timeParam.width = 1000;
        tv_time.setLayoutParams(tv_timeParam);
        tv_time.setBackgroundColor(0x00000000);
        int widthMeasureSpec = MeasureSpec.makeMeasureSpec((1 << 30) - 1, MeasureSpec.AT_MOST);
        int heightMeasureSpec = MeasureSpec.makeMeasureSpec((1 << 30) - 1, MeasureSpec.AT_MOST);
        tv_time.measure(widthMeasureSpec, heightMeasureSpec);

        addView(secondDiskView);
        addView(minuteDiskView);
        addView(hourDiskView);
        addView(amPmDiskView);

        addView(tv_time);

        //默认处于设置时间模式
        amPmDiskView.setStr(isNoon ? "PM" : "AM");
        hourDiskView.isNeedReturn = false;
        minuteDiskView.isNeedReturn = false;
        secondDiskView.isNeedReturn = false;
        secondDiskView.setVisibility(GONE);

    }

    OnTimeChangedListener onTimeChangedListener;

    public void setOnTimeChangedListener(OnTimeChangedListener listener) {
        this.onTimeChangedListener = listener;
        amPmDiskView.setOnAPMChangedListener(new AmPmDiskView.OnAPMChangedListener() {
            @Override
            public void onAPMChanged(String str) {
                if (str.equals("AM")) {
                    hour24 = hour24 - 12;
                    isNoon = false;
                } else {
                    hour24 = hour + 12;
                    if (hour24 == 24) {
                        hour24 = 0;
                    }
                    isNoon = true;
                }
                handler.sendEmptyMessage(MSG_UPDATE_TV_TIME);
            }
        });
        hourDiskView.setOnHourChangedListener(new HourDiskView.OnHourChangedListener() {
            @Override
            public void onHourChanged(int mHour) {
                hour = mHour;
                onTimeChangedListener.onTimeChanged(isNoon, hour, minute, second);
                handler.sendEmptyMessage(MSG_UPDATE_TV_TIME);
            }
        });
        minuteDiskView.setOnMinuteChangedListener(new MinuteDiskView.OnMinuteChangedListener() {
            @Override
            public void onMinuteChanged(int mMinute) {
                minute = mMinute;
                onTimeChangedListener.onTimeChanged(isNoon, hour, minute, second);
                handler.sendEmptyMessage(MSG_UPDATE_TV_TIME);
            }
        });
    }

    public interface OnTimeChangedListener {
        void onTimeChanged(boolean apm, int hour, int minute, int second);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        secondDiskView.layout(-(secondDiskView.getmRadius() - screenWidth / 2),
                -secondDiskView.getmRadius() + hourDiskView.getmRadius() / 2,
                secondDiskView.getmRadius() - screenWidth / 2 + screenWidth,
                2 * secondDiskView.getmRadius() - (-secondDiskView.getmRadius() + hourDiskView.getmRadius() / 2));

        minuteDiskView.layout(-(minuteDiskView.getmRadius() - screenWidth / 2),
                -minuteDiskView.getmRadius() + hourDiskView.getmRadius() / 2,
                minuteDiskView.getmRadius() - screenWidth / 2 + screenWidth,
                2 * minuteDiskView.getmRadius() - (-minuteDiskView.getmRadius() + hourDiskView.getmRadius() / 2));

        hourDiskView.layout(screenWidth / 2 - hourDiskView.getmRadius(),
                -hourDiskView.getmRadius() / 2,
                screenWidth / 2 + hourDiskView.getmRadius(),
                hourDiskView.getmRadius() * 2 - hourDiskView.getmRadius() / 2);

        amPmDiskView.layout(screenWidth / 2 - amPmDiskView.getmRadius(),
                hourDiskView.getmRadius() / 2 - amPmDiskView.getmRadius(),
                screenWidth / 2 + amPmDiskView.getmRadius(),
                hourDiskView.getmRadius() / 2 + amPmDiskView.getmRadius() * 2);

        tv_time.layout(screenWidth / 2 - tv_time.getMeasuredWidth() / 2,
                screenHeight / 2,
                screenWidth / 2 + tv_time.getMeasuredWidth() / 2,
                screenHeight / 2 + tv_time.getMeasuredHeight());

    }

    /**
     * 隐藏时间文字
     */
    public void hideTimeText() {
        if (tv_time != null) {
            tv_time.setVisibility(GONE);
        }
    }

    /**
     * 显示时间文字
     */
    public void showTimeText() {
        if (tv_time != null) {
            tv_time.setVisibility(VISIBLE);
        }
    }

    /**
     * 开启时间显示
     */
    public void start() {
        hourDiskView.isNeedReturn = true;
        minuteDiskView.isNeedReturn = true;
        secondDiskView.isNeedReturn = true;
        secondDiskView.setVisibility(VISIBLE);
        if (isStop) {
            handler.sendEmptyMessageDelayed(MSG_GET_CUR_TIME, 100);
        }
        isStop = false;
    }

    /**
     * 显示当前时间
     */
    public void showCurTime() {
        calendar = Calendar.getInstance();

        isNoon = calendar.get(Calendar.AM_PM) == 1 ? true : false;

        hour24 = calendar.get(Calendar.HOUR_OF_DAY);
        hour = calendar.get(Calendar.HOUR);
        if (hour == 0) {
            hour = 12;
        }
        minute = calendar.get(Calendar.MINUTE);
        second = calendar.get(Calendar.SECOND);

        amPmDiskView.setStr(isNoon ? "PM" : "AM");
        hourDiskView.setCurTime(hour);
        minuteDiskView.setCurTime(minute);
        secondDiskView.setCurTime(second + 1);

        if (hour24 >= 10) {
            if (minute >= 10) {
                tv_time.setText(hour24 + ":" + minute);
            } else {
                tv_time.setText(hour24 + ":0" + minute);
            }
        } else {
            if (minute >= 10) {
                tv_time.setText("0" + hour24 + ":" + minute);
            } else {
                tv_time.setText("0" + hour24 + ":0" + minute);
            }
        }
    }

    /**
     * 显示对应的时间
     *
     * @param hour24
     * @param minute
     * @param second
     */
    public void showTime(int hour24, int minute, int second) {
        this.hour24 = hour24;
        this.minute = minute;
        this.second = second;
        if (this.hour24 > 12) {
            this.hour = hour24 - 12;
        } else {
            this.hour = hour24;
        }
        isNoon = this.hour24 > 12 ? true : false;
        amPmDiskView.setStr(isNoon ? "PM" : "AM");
        hourDiskView.setCurTime(hour);
        minuteDiskView.setCurTime(minute);
        secondDiskView.setCurTime(second + 1);
        handler.sendEmptyMessage(MSG_UPDATE_TV_TIME);
    }

    public int getHour24() {
        return hour24;
    }

    public void setHour24(int hour24) {
        this.hour24 = hour24;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public int getSecond() {
        return second;
    }

    public void setSecond(int second) {
        this.second = second;
    }

    public boolean isNoon() {
        return isNoon;
    }

    public void setNoon(boolean noon) {
        isNoon = noon;
    }
}
