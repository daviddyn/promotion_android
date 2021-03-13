package edu.neu.promotion.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import edu.neu.promotion.R;

public class PageTabBarView extends View {

    public PageTabBarView(Context context) {
        super(context);
        loadDefaultAttrs();
        construct();
    }

    public PageTabBarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        applyAttrs(attrs);
        construct();
    }

    public PageTabBarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        applyAttrs(attrs);
        construct();
    }

    private Paint drawPaint;
    private RectF drawRect;

    private int pageCount;
    private int pagePosition;
    private float offsetRate;

    private void loadDefaultAttrs() {
        drawPaint = new Paint();
        drawPaint.setColor(getResources().getColor(R.color.on_primary));
    }

    private void applyAttrs(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.PageTabBarView);
        drawPaint = new Paint();
        drawPaint.setColor(typedArray.getColor(R.styleable.PageTabBarView_color, getResources().getColor(R.color.on_primary)));
        typedArray.recycle();
    }

    private void construct() {
        drawRect = new RectF();
        pageCount = 1;
    }

    public void setPagePosition(int pageCount, int pagePosition, float offsetRate) {
        this.pageCount = pageCount;
        this.pagePosition = pagePosition;
        this.offsetRate = offsetRate;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float viewWidth = getMeasuredWidth();
        float itemWidth = viewWidth / pageCount;
        drawRect.set(viewWidth * pagePosition / pageCount + itemWidth * offsetRate, 0, viewWidth * (pagePosition + 1) / pageCount + itemWidth * offsetRate, getMeasuredHeight());
        canvas.drawRect(drawRect, drawPaint);
    }
}