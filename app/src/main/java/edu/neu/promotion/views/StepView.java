package edu.neu.promotion.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;

public class StepView extends ViewGroup {

    public StepView(Context context) {
        super(context);
        construct();
    }

    public StepView(Context context, AttributeSet attrs) {
        super(context, attrs);
        construct();
    }

    public StepView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        construct();
    }

    private boolean animating;
    private Scroller scroller;
    private int animationDuration;

    private int currentItem;
    private int stepIntent;

    private boolean removeViewsWhenStop;

    private void construct() {
        scroller = new Scroller(getContext());
        animationDuration = getResources().getInteger(android.R.integer.config_longAnimTime);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        //根据自己的模式决定如何测量孩子
        int childWidthMeasureSpec = 0, childHeightMeasureSpec = 0;
        switch (widthMode) {
            case MeasureSpec.UNSPECIFIED:
                childWidthMeasureSpec = widthMeasureSpec;
                break;
            case MeasureSpec.AT_MOST:
                childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(Math.max(widthSize - getPaddingLeft() - getPaddingRight(), 0), MeasureSpec.AT_MOST);
                break;
            case MeasureSpec.EXACTLY:
                childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(Math.max(widthSize - getPaddingLeft() - getPaddingRight(), 0), MeasureSpec.EXACTLY);
                break;
        }
        switch (heightMode) {
            case MeasureSpec.UNSPECIFIED:
                childHeightMeasureSpec = heightMeasureSpec;
                break;
            case MeasureSpec.AT_MOST:
                childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(Math.max(heightSize - getPaddingTop() - getPaddingBottom(), 0), MeasureSpec.AT_MOST);
                break;
            case MeasureSpec.EXACTLY:
                childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(Math.max(heightSize - getPaddingTop() - getPaddingBottom(), 0), MeasureSpec.EXACTLY);
                break;
        }

        //测量孩子，并计算此View宽高
        int contentWidth = 0, contentHeight = 0;
        for (int i = 0; i < getChildCount(); i++) {
            View v = getChildAt(i);
            v.measure(childWidthMeasureSpec, childHeightMeasureSpec);
            if (v.getMeasuredWidth() > contentWidth) {
                contentWidth = v.getMeasuredWidth();
            }
            if (v.getMeasuredHeight() > contentHeight) {
                contentHeight = v.getMeasuredHeight();
            }
        }

        setMeasuredDimension(contentWidth, contentHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        placeChildren();
    }

    private void placeChildren() {
        if (getChildCount() == 0) {
            return;
        }
        for (int i = 0; i < getChildCount(); i++) {
            if (i == currentItem) {
                placeToCurrent(i);
            }
            else if (i == stepIntent) {
                if (i > currentItem) {
                    placeToNext(i);
                }
                else {
                    placeToPrevious(i);
                }
            }
            else {
                placeToUnused(i);
            }
        }
    }

    private void placeToCurrent(int item) {
        View v = getChildAt(item);
        v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
    }

    private void placeToUnused(int item) {
        View v = getChildAt(item);
        int viewWidth = getMeasuredWidth();
        v.layout(viewWidth + viewWidth, 0, viewWidth + viewWidth + v.getMeasuredWidth(), v.getMeasuredHeight());
    }

    private void placeToNext(int item) {
        View v = getChildAt(item);
        int viewWidth = getMeasuredWidth();
        v.layout(viewWidth, 0, viewWidth + v.getMeasuredWidth(), v.getMeasuredHeight());
    }

    private void placeToPrevious(int item) {
        View v = getChildAt(item);
        int viewWidth = getMeasuredWidth();
        v.layout(-viewWidth, 0, v.getMeasuredWidth() - viewWidth, v.getMeasuredHeight());
    }

    @Override
    public void computeScroll() {
        if (animating) {
            if (scroller.computeScrollOffset()) {
                setScrollX(scroller.getCurrX());
            }
            else {
                animating = false;
                if (removeViewsWhenStop) {
                    for (int i = getChildCount() - 1; i > stepIntent; i--) {
                        removeViewAt(i);
                    }
                }
                else {
                    placeToUnused(currentItem);
                }
                placeToCurrent(stepIntent);
                currentItem = stepIntent;
                setScrollX(0);
            }
            invalidate();
        }
        super.computeScroll();
    }

    private void navigateToInner(int position) {
        stepIntent = position;
        animating = true;
        if (position > currentItem) {
            placeToNext(position);
            scroller.startScroll(0, 0, getMeasuredWidth(), 0, animationDuration);
        }
        else {
            placeToPrevious(position);
            scroller.startScroll(0, 0, -getMeasuredWidth(), 0, animationDuration);
        }
        invalidate();
    }

    public void navigateTo(int position) {
        if (position == currentItem) {
            return;
        }
        removeViewsWhenStop = false;
        navigateToInner(position);
    }

    public void stepForward(View v) {
        addView(v);
        removeViewsWhenStop = false;
        navigateToInner(getChildCount() - 1);
    }

    public void stepBackwardTo(int position) {
        if (position >= currentItem) {
            return;
        }
        removeViewsWhenStop = true;
        navigateToInner(position);
    }
}
