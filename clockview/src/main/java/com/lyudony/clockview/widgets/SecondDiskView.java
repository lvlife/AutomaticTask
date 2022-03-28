package com.lyudony.clockview.widgets;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.Nullable;

import com.lyudony.clockview.utils.DisplayUtils;


public class SecondDiskView extends DiskView {
    Paint mPaint;

    //秒针点颜色
    int dotColor = 0xffffffff;

    //秒针指示点颜色
    int indicatorColor = 0xfff0ff00;

    public SecondDiskView(Context context) {
        this(context, null);
    }

    public SecondDiskView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public SecondDiskView(Context context, int radius) {
        super(context);
        mRadius = radius;
        initView(context);
    }

    private void initView(Context context) {
        if (mRadius == 0) {
            mRadius = 650;
        }
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(dotColor);
        mPaint.setTextAlign(Paint.Align.LEFT);
        mPaint.setTextSize(DisplayUtils.sp2px(mContext, 20));//默认20sp
    }

    private void initView(Context context, AttributeSet attrs) {
        if (mRadius == 0) {
            mRadius = 650;
        }
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(dotColor);
        mPaint.setTextAlign(Paint.Align.LEFT);
        mPaint.setTextSize(DisplayUtils.sp2px(mContext, 20));//默认20sp
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //画圆点
        mPaint.setColor(dotColor);
        for (int i = 0; i < 60; i++) {
            if (i == 0) {
                mPaint.setColor(indicatorColor);
                canvas.drawCircle(mRadius, 2 * mRadius, DisplayUtils.sp2px(mContext, 20) / 5, mPaint);
                mPaint.setColor(dotColor);
            } else {
                canvas.drawCircle(mRadius, 2 * mRadius, DisplayUtils.sp2px(mContext, 20) / 5, mPaint);
            }
            canvas.rotate(-6, mRadius, mRadius);
        }
    }

    /**
     * 显示当前的秒数
     */
    public void setCurTime(int second) {
        //换算成对应的角度
        int tmpDegree = 6 * (second - 1);
        if (tmpDegree == degree) {
            return;
        }
        animator = ValueAnimator.ofInt(degree, tmpDegree);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.setDuration(100);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                degree = (int) animation.getAnimatedValue();
                postInvalidate();
            }
        });
        animator.start();
    }
}
