package org.itri.tomato;

/**
 * Created by heiruwu on 7/24/15.
 */
import android.annotation.TargetApi;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.FrameLayout;

import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.Scrollable;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.view.ViewHelper;

public abstract class ToolbarControlBaseActivity<S extends Scrollable> extends AppCompatActivity implements ObservableScrollViewCallbacks {

    private Toolbar mToolbar;
    private S mScrollable;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResId());

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setElevation(10);
        setSupportActionBar(mToolbar);

        mScrollable = createScrollable();
        mScrollable.setScrollViewCallbacks(this);
    }

    protected abstract int getLayoutResId();

    protected abstract S createScrollable();

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
    }

    @Override
    public void onDownMotionEvent() {
    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
        Log.e("DEBUG", "onUpOrCancelMotionEvent: " + scrollState);
        if (scrollState == ScrollState.UP) {
            if (toolbarIsShown()) {
                hideToolbar();
            }
        } else if (scrollState == ScrollState.DOWN) {
            if (toolbarIsHidden()) {
                showToolbar();
            }
        }
    }

    private boolean toolbarIsShown() {
        return ViewHelper.getTranslationY(mToolbar) == 0;
    }

    private boolean toolbarIsHidden() {
        return ViewHelper.getTranslationY(mToolbar) == -mToolbar.getHeight();
    }

    private void showToolbar() {
        moveToolbar(0);
    }

    private void hideToolbar() {
        moveToolbar(-mToolbar.getHeight());
    }

    private void moveToolbar(float toTranslationY) {
        if (ViewHelper.getTranslationY(mToolbar) == toTranslationY) {
            return;
        }
        ValueAnimator animator = ValueAnimator.ofFloat(ViewHelper.getTranslationY(mToolbar), toTranslationY).setDuration(200);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float translationY = (float) animation.getAnimatedValue();
                ViewHelper.setTranslationY(mToolbar, translationY);
                ViewHelper.setTranslationY((View) mScrollable, translationY);
                FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) ((View) mScrollable).getLayoutParams();
                Display display = getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                int height = size.y;
                lp.height = (int) -translationY + height - lp.topMargin;
                ((View) mScrollable).requestLayout();
            }
        });
        animator.start();
    }
}
