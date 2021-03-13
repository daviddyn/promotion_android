package edu.neu.promotion.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import edu.neu.promotion.R;

public class BadgeView extends FrameLayout {

    public BadgeView(@NonNull Context context) {
        super(context);
        construct();
    }

    public BadgeView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        construct();
    }

    public BadgeView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        construct();
    }

    private Drawable badgeDrawable;
    private int badgeSize;
    private int badgeLeft;
    private int badgeTop;

    private boolean badgeVisible;

    public void construct() {
        setWillNotDraw(false);
        badgeDrawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_badge);
        badgeSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6, getResources().getDisplayMetrics());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        badgeLeft = getMeasuredWidth() - getPaddingRight() - badgeSize;
        badgeTop = getPaddingTop();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        int viewWidth = getMeasuredWidth();
        int viewHeight = getMeasuredHeight();
        for (int i = 0; i < getChildCount(); i++) {
            View v = getChildAt(i);
            int childLeft = (viewWidth - v.getMeasuredWidth()) / 2;
            int childTop = (viewHeight - v.getMeasuredHeight()) / 2;
            v.layout(childLeft, childTop, childLeft + v.getMeasuredWidth(), childTop + v.getMeasuredHeight());
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        badgeDrawable.setBounds(badgeLeft, badgeTop, badgeLeft + badgeSize, badgeTop + badgeSize);
        if (badgeVisible) {
            badgeDrawable.draw(canvas);
        }
    }

    public boolean isBadgeVisible() {
        return badgeVisible;
    }

    public void setBadgeVisible(boolean badgeVisible) {
        this.badgeVisible = badgeVisible;
        invalidate();
    }
}
