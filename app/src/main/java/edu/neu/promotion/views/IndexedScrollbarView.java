package edu.neu.promotion.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.List;

import edu.neu.promotion.R;

public class IndexedScrollbarView extends View {

    public IndexedScrollbarView(Context context) {
        super(context);
        construct();
    }

    public IndexedScrollbarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        construct();
    }

    public IndexedScrollbarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        construct();
    }

    public interface OnSelectChangedListener {
        void onSelectChanged(IndexedScrollbarView who, int selectedItem);
    }

    private int textColor;
    private int primaryColor;
    private int onPrimaryColor;

    private TextPaint stickTextPaint;
    private float stickTextHeight;
    private float stickTextDrawOffsetY;
    private float stickElementMargin;
    private float stickHorizontalMargin;
    private float stickRadius;
    private float stickAreaLeft;
    private float stickDrawTextCenterX;
    private float stickDrawTextStartY;

    private TextPaint bigTextPaint;
    private float bigTextPadding;
    private float bigTextDrawOffsetY;
    private float bigTextHeight;
    private float bigTextAreaRadius;
    private float bigTextAreaMargin;
    private float bigTextAreaRight;

    private Paint highlightPaint;

    private List<String> index;
    private float[] textWidths;
    private int highlightItem = -1;
    private int selectedItem = -1;

    private OnSelectChangedListener onSelectChangedListener;

    private void construct() {
        textColor = getResources().getColor(R.color.text_tertiary);
        primaryColor = getResources().getColor(R.color.primary);
        onPrimaryColor = getResources().getColor(R.color.on_primary);

        stickTextPaint = new TextPaint();
        stickTextPaint.setAntiAlias(true);
        stickTextPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 9, getResources().getDisplayMetrics()));
        Paint.FontMetrics fontMetrics = new Paint.FontMetrics();
        stickTextPaint.getFontMetrics(fontMetrics);
        stickTextHeight = fontMetrics.bottom - fontMetrics.top;
        stickTextDrawOffsetY = -fontMetrics.top;
        stickElementMargin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12, getResources().getDisplayMetrics());
        stickHorizontalMargin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());
        bigTextPaint = new TextPaint();
        bigTextPaint.setAntiAlias(true);
        bigTextPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 24, getResources().getDisplayMetrics()));
        bigTextPaint.setColor(onPrimaryColor);
        bigTextPaint.getFontMetrics(fontMetrics);
        bigTextHeight = fontMetrics.bottom - fontMetrics.top;
        bigTextDrawOffsetY = -fontMetrics.top;
        bigTextPadding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics());
        bigTextAreaRadius = bigTextHeight / 2 + bigTextPadding;
        bigTextAreaMargin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 28, getResources().getDisplayMetrics());

        highlightPaint = new Paint();
        highlightPaint.setAntiAlias(true);
        highlightPaint.setColor(primaryColor);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (index == null || index.isEmpty()) {
            return;
        }
        float y = stickDrawTextStartY;
        for (int i = 0; i < index.size(); i++) {
            if (i == highlightItem) {
                float centerY = y + stickTextHeight / 2;
                float textLeft = bigTextAreaRight - bigTextAreaRadius - textWidths[i + index.size()];
                stickTextPaint.setColor(onPrimaryColor);
                canvas.drawCircle(stickDrawTextCenterX, centerY, stickRadius, highlightPaint);
                canvas.drawRoundRect(textLeft - bigTextAreaRadius, centerY - bigTextAreaRadius, bigTextAreaRight, centerY + bigTextAreaRadius, bigTextAreaRadius, bigTextAreaRadius, highlightPaint);
                canvas.drawText(index.get(i), textLeft, centerY - bigTextAreaRadius + bigTextPadding + bigTextDrawOffsetY, bigTextPaint);
            }
            else if (i == selectedItem) {
                stickTextPaint.setColor(primaryColor);
            }
            else {
                stickTextPaint.setColor(textColor);
            }
            canvas.drawText(
                    index.get(i).substring(0, 1),
                    stickDrawTextCenterX - textWidths[i] / 2,
                    y + stickTextDrawOffsetY,
                    stickTextPaint
            );
            y = y + stickTextHeight + stickElementMargin;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        calcDrawParams();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (index == null || index.isEmpty()) {
            return false;
        }
        float x, y;
        int highlightItem;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x = event.getX();
                if (x < stickAreaLeft) {
                    return false;
                }
                y = event.getY();
                highlightItem = (int) ((y - (stickDrawTextStartY - stickElementMargin / 2)) / (stickTextHeight + stickElementMargin));
                if (highlightItem < 0 || highlightItem >= index.size()) {
                    return false;
                }
                this.highlightItem = highlightItem;
                if (selectedItem != highlightItem) {
                    selectedItem = highlightItem;
                    if (onSelectChangedListener != null) {
                        onSelectChangedListener.onSelectChanged(this, selectedItem);
                    }
                }
                invalidate();
                return true;
            case MotionEvent.ACTION_MOVE:
                y = event.getY();
                this.highlightItem = (int) ((y - (stickDrawTextStartY - stickElementMargin / 2)) / (stickTextHeight + stickElementMargin));
                if (this.highlightItem < 0) {
                    this.highlightItem = 0;
                }
                if (this.highlightItem >= index.size()) {
                    this.highlightItem = index.size() - 1;
                }
                if (selectedItem != this.highlightItem) {
                    selectedItem = this.highlightItem;
                    if (onSelectChangedListener != null) {
                        onSelectChangedListener.onSelectChanged(this, selectedItem);
                    }
                }
                invalidate();
                return true;
            case MotionEvent.ACTION_UP:
                this.highlightItem = -1;
                invalidate();
                return true;
        }
        return super.onTouchEvent(event);
    }

    private void calcDrawParams() {
        if (index == null || index.isEmpty()) {
            return;
        }
        //计算最大宽度
        float maxTextWidth = 0;
        for (int i = 0; i < index.size(); i++) {
            String indexText = index.get(i);
            textWidths[i] = indexText.length() == 0 ? 0 : stickTextPaint.measureText(indexText.substring(0, 1));
            textWidths[i + index.size()] = bigTextPaint.measureText(indexText);
            if (textWidths[i] > maxTextWidth) {
                maxTextWidth = textWidths[i];
            }
        }
        //条宽度是
        float stickWidth = Math.max(maxTextWidth, stickTextHeight) + stickHorizontalMargin + stickHorizontalMargin;
        stickRadius = stickWidth / 2;
        stickAreaLeft = getMeasuredWidth() - getPaddingRight() - stickWidth;
        stickDrawTextCenterX = stickAreaLeft + stickRadius;
        stickDrawTextStartY = (getMeasuredHeight() - (stickTextHeight + stickElementMargin) * index.size() + stickElementMargin) / 2;
        //计算高亮
        bigTextAreaRight = stickAreaLeft - bigTextAreaMargin;
    }

    public void setIndex(List<String> index) {
        this.index = index;
        if (index != null && !index.isEmpty() && (textWidths == null || textWidths.length < index.size() << 1)) {
            textWidths = new float[index.size() << 1];
        }
        requestLayout();
    }

    public void setSelectedItem(int selectedItem) {
        this.selectedItem = selectedItem;
        invalidate();
    }

    public void setOnSelectChangedListener(OnSelectChangedListener onSelectChangedListener) {
        this.onSelectChangedListener = onSelectChangedListener;
    }
}