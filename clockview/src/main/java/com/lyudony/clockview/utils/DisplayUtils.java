package com.lyudony.clockview.utils;

import android.content.Context;
import android.util.DisplayMetrics;

public class DisplayUtils {

    private DisplayUtils() {

    }

    /**
     * 获取屏幕宽、高；返回一个数组，[0] = width, [1] = height
     */
    public static final int[] getDisplayPxArray(Context context) {
        int displays[] = new int[2];
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        displays[0] = dm.widthPixels;
        displays[1] = dm.heightPixels;
        return displays;
    }

    /**
     * 获取屏幕宽；返回int
     */
    public static final int getDisplayPxHeight(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm.heightPixels;
    }

    /**
     * 获取屏幕宽；返回int
     */
    public static final int getDisplayPxWidth(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm.widthPixels;
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     *
     * @param pxValue
     * @param （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue
     * @param （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }
}
