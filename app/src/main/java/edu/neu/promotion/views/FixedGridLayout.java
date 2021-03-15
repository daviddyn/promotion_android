package edu.neu.promotion.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import edu.neu.promotion.R;

public class FixedGridLayout extends ViewGroup {

    public static class LayoutParams extends ViewGroup.MarginLayoutParams {

        public int columnSpan;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            TypedArray typedArray = c.obtainStyledAttributes(attrs, R.styleable.FixedGridLayout_Layout);
            columnSpan = typedArray.getInt(R.styleable.FixedGridLayout_Layout_layout_columnSpan, 1);
            typedArray.recycle();
        }

        public LayoutParams(int width, int height) {
            super(width, height);
            columnSpan = 1;
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
            columnSpan = 1;
        }
    }

    public FixedGridLayout(Context context) {
        super(context);
        loadDefaultAttrs();
        construct();
    }

    public FixedGridLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        applyAttrs(attrs);
        construct();
    }

    public FixedGridLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        applyAttrs(attrs);
        construct();
    }

    private int girdHeight;
    private int columnCount;

    private float elementWidth;

    private void loadDefaultAttrs() {
        columnCount = 1;
    }

    private void applyAttrs(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.FixedGridLayout);
        girdHeight = (int) typedArray.getDimension(R.styleable.FixedGridLayout_gridHeight, 0);
        columnCount = typedArray.getInt(R.styleable.FixedGridLayout_columnCount, 1);
        typedArray.recycle();
    }

    private void construct() {
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (MeasureSpec.getMode(widthMeasureSpec) != MeasureSpec.EXACTLY) {
            throw new RuntimeException("此View不支持自适应宽度");
        }
        int viewWidth = MeasureSpec.getSize(widthMeasureSpec);
        int containsWidth = viewWidth - getPaddingLeft() - getPaddingRight();
        elementWidth = containsWidth / (float)columnCount;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == GONE) {
                continue;
            }
            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            int colSpan = Math.max(Math.min(lp.columnSpan, columnCount), 1);
            int desiredWidth = ((int) (elementWidth * colSpan)) - lp.leftMargin - lp.rightMargin;
            int desiredHeight = girdHeight - lp.topMargin - lp.bottomMargin;
            child.measure(
                    MeasureSpec.makeMeasureSpec(Math.max(desiredWidth, 0), MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(Math.max(desiredHeight, 0), MeasureSpec.EXACTLY)
            );
        }
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        switch (heightMode) {
            case MeasureSpec.UNSPECIFIED:
                setMeasuredDimension(viewWidth, measureContentHeight());
                break;
            case MeasureSpec.AT_MOST:
                setMeasuredDimension(viewWidth, Math.min(measureContentHeight(), heightSize));
                break;
            case MeasureSpec.EXACTLY:
                setMeasuredDimension(viewWidth, heightSize);
                break;
        }
    }

    private int measureContentHeight() {
        int rowCount = 0;
        int col = 0;
        for (int i = 0; i <getChildCount(); i++) {
            View v = getChildAt(i);
            if (v.getVisibility() == GONE) {
                continue;
            }
            int colSpan = Math.min(((LayoutParams) v.getLayoutParams()).columnSpan, columnCount);
            if (col + colSpan > columnCount) {
                col = 0;
            }
            if (col == 0) {
                rowCount++;
            }
            col += colSpan;
            if (col == columnCount) {
                col = 0;
            }
        }
        return rowCount * girdHeight + getPaddingTop() + getPaddingBottom();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int paddingLeft = getPaddingLeft();
        int currentTop = getPaddingTop();
        int col = 0;
        for (int i = 0; i <getChildCount(); i++) {
            View v = getChildAt(i);
            if (v.getVisibility() == GONE) {
                continue;
            }
            LayoutParams lp = (LayoutParams) v.getLayoutParams();
            int colSpan = Math.max(Math.min(lp.columnSpan, columnCount), 1);
            int desiredWidth = ((int) (elementWidth * colSpan)) - lp.leftMargin - lp.rightMargin;
            int desiredHeight = girdHeight - lp.topMargin - lp.bottomMargin;
            if (col + colSpan > columnCount) {
                currentTop += girdHeight;
                col = 0;
            }
            int left = (int) (paddingLeft + elementWidth * col) + lp.leftMargin;
            int top = currentTop + lp.topMargin;
            getChildAt(i).layout(left, top, left + Math.max(desiredWidth, 0), top + Math.max(desiredHeight, 0));
            col += colSpan;
            if (col == columnCount) {
                currentTop += girdHeight;
                col = 0;
            }
        }
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(getContext(), null);
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }
}