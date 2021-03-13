package edu.neu.promotion.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import edu.neu.promotion.R;

public class SimpleProgressView extends View {

    public SimpleProgressView(Context context) {
        super(context);
        construct();
    }

    public SimpleProgressView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        construct();
    }

    public SimpleProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        construct();
    }

    private Paint solidPaint;
    private float value;

    private void construct() {
        solidPaint = new Paint();
        solidPaint.setColor(getResources().getColor(R.color.primary));
        solidPaint.setStyle(Paint.Style.FILL);
    }

    //0~1
    public void setValue(float value) {
        this.value = Math.min(Math.max(value, 0), 1);
        invalidate();
    }

    //0~1
    public float getValue() {
        return value;
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        canvas.drawRect(0, 0, getMeasuredWidth() * value, getMeasuredHeight(), solidPaint);
    }
}
