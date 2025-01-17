package org.itri.tomato;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * TODO: document your custom view class.
 */
public class WhenDoIconView extends View {

    private Paint mTextPaint = new Paint();
    private Paint mWhenPaint = new Paint();
    private Paint mDoPaint = new Paint();
    private int mWidthRatio;
    private int mHalfWidthRation;
    private int mWidthMiddle;
    private int mBarHeight;
    private int mOffset = 40;
    private Bitmap mBitmapWhen;
    private Bitmap mBitmapDo;

    public WhenDoIconView(Context context) {
        super(context);
        init(null, 0);
    }

    public WhenDoIconView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public WhenDoIconView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        mTextPaint.setTextSize(80);
        mTextPaint.setColor(getResources().getColor(android.R.color.white));
        mWhenPaint.setColor(getResources().getColor(R.color.when_color));
        mDoPaint.setColor(getResources().getColor(R.color.do_color));
    }

    public void setIcon(Bitmap whenIcon, Bitmap doIcon, int width, int barheight) {
        mWidthMiddle = width / 2;
        mWidthRatio = width / 6;
        mHalfWidthRation = mWidthRatio / 2;
        mBarHeight = barheight;
        mBitmapWhen = Bitmap.createScaledBitmap(whenIcon, mWidthRatio, mWidthRatio, false);
        mBitmapDo = Bitmap.createScaledBitmap(doIcon, mWidthRatio, mWidthRatio, false);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        RectF rWhen = new RectF();
        rWhen.left = mWidthRatio;
        rWhen.top = mHalfWidthRation + mBarHeight;
        rWhen.right = mWidthRatio * 3;
        rWhen.bottom = mHalfWidthRation + mWidthRatio * 2 + mBarHeight;
        canvas.drawRoundRect(rWhen, 40, 40, mWhenPaint);
        RectF rDo = new RectF();
        rDo.left = mWidthRatio * 3;
        rDo.top = mHalfWidthRation + mBarHeight;
        rDo.right = mWidthRatio * 5;
        rDo.bottom = mHalfWidthRation + mWidthRatio * 2 + mBarHeight;
        canvas.drawRoundRect(rDo, 40, 40, mDoPaint);
        canvas.drawText("When", mWidthRatio + mOffset, mWidthRatio + mBarHeight - mOffset, mTextPaint);
        canvas.drawText("Do", mWidthRatio * 3 + mOffset, mWidthRatio + mBarHeight - mOffset, mTextPaint);

        if (mBitmapWhen != null) {
            canvas.drawBitmap(mBitmapWhen, mWidthRatio + mHalfWidthRation, mWidthRatio + mBarHeight, null);
            canvas.drawBitmap(mBitmapDo, mWidthMiddle + mHalfWidthRation, mWidthRatio + mBarHeight, null);
        }
    }
}
