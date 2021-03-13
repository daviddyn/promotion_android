package edu.neu.promotion.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Scroller;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class BottomToolBarView extends FrameLayout {

    public BottomToolBarView(@NonNull Context context) {
        super(context);
        construct();
    }

    public BottomToolBarView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        construct();
    }

    public BottomToolBarView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        construct();
    }

    private boolean animating;
    private Scroller scroller;

    private int maxChildHeight;

    private void construct() {
        scroller = new Scroller(getContext());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        maxChildHeight = 0;
        for (int i = 0; i < getChildCount(); i++) {
            int childHeight = getChildAt(i).getMeasuredHeight();
            if (childHeight > maxChildHeight) {
                maxChildHeight = childHeight;
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        int contentLeft = getPaddingLeft();
        int contentWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
        int viewHeight = getMeasuredHeight();
        for (int i = 0; i < getChildCount(); i++) {
            View v = getChildAt(i);
            int viewWidth = v.getMeasuredWidth();
            LayoutParams lp = (LayoutParams) v.getLayoutParams();
            left = (contentWidth - lp.leftMargin - lp.rightMargin - viewWidth) / 2 + contentLeft + lp.leftMargin;
            v.layout(left, viewHeight, left + viewWidth, viewHeight + v.getMeasuredHeight());
        }
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (!animating) {
            return;
        }
        if (scroller.computeScrollOffset()) {
            setScrollY(scroller.getCurrY());
        }
        else {
            animating = false;
        }
        invalidate();
    }

    public void show() {
        animating = true;
        scroller.startScroll(0, getScrollY(), 0, maxChildHeight - getScrollY());
        invalidate();
    }

    public void hide() {
        animating = true;
        scroller.startScroll(0, getScrollY(), 0, -getScrollY());
        invalidate();
    }
}