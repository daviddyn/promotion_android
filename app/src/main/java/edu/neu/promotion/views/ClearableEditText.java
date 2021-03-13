package edu.neu.promotion.views;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.core.content.ContextCompat;

import edu.neu.promotion.R;

public class ClearableEditText extends ViewGroup {

    public ClearableEditText(Context context) {
        super(context);
        construct();
    }

    public ClearableEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        construct();
    }

    public ClearableEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        construct();
    }

    private int outerPadding;
    private int innerPadding;
    private boolean firstLoad;

    private OnFocusChangeListener editTextFocusListener;
    private TextWatcher editTextTextWatcher;
    private OnClickListener clearButtonClickListener;

    private void construct() {
        Context context = getContext();

        outerPadding = (int) context.getResources().getDimension(R.dimen.input_padding_outer);
        innerPadding = (int) context.getResources().getDimension(R.dimen.input_padding_inner);
        setPadding(outerPadding, outerPadding, outerPadding, outerPadding);

        editTextFocusListener = (v, hasFocus) -> testClearButtonVisible();
        editTextTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                testClearButtonVisible();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        clearButtonClickListener = v -> ((EditText) getChildAt(0)).setText("");

        firstLoad = true;
    }

    public void testClearButtonVisible() {
        EditText editText = (EditText) getChildAt(0);
        ImageView clearButton = (ImageView) getChildAt(1);
        if (editText.hasFocus() && editText.length() > 0) {
            clearButton.setVisibility(VISIBLE);
            editText.setPadding(innerPadding, innerPadding, innerPadding + clearButton.getMeasuredWidth() + innerPadding, innerPadding);
        }
        else {
            clearButton.setVisibility(INVISIBLE);
            editText.setPadding(innerPadding, innerPadding, innerPadding, innerPadding);
        }
    }

    public EditText getEditText() {
        return (EditText) getChildAt(0);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        EditText editText = (EditText) getChildAt(0);
        editText.setOnFocusChangeListener(editTextFocusListener);
        editText.removeTextChangedListener(editTextTextWatcher);
        editText.addTextChangedListener(editTextTextWatcher);
        ImageView clearButton = (ImageView) getChildAt(1);
        clearButton.setOnClickListener(clearButtonClickListener);

        //测量删除按钮
        clearButton.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));

        //测量输入框
        int childWidthMeasureSpec = 0, childHeightMeasureSpec = 0;
        switch (widthMode) {
            case MeasureSpec.UNSPECIFIED:
                childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.UNSPECIFIED);
                break;
            case MeasureSpec.AT_MOST:
                childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(Math.max(widthSize - outerPadding - outerPadding, 0), MeasureSpec.AT_MOST);
                break;
            case MeasureSpec.EXACTLY:
                childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(Math.max(widthSize - outerPadding - outerPadding, 0), MeasureSpec.EXACTLY);
        }
        switch (heightMode) {
            case MeasureSpec.UNSPECIFIED:
                childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.UNSPECIFIED);
                break;
            case MeasureSpec.AT_MOST:
                childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(Math.max(heightSize - outerPadding - outerPadding, 0), MeasureSpec.AT_MOST);
                break;
            case MeasureSpec.EXACTLY:
                childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(Math.max(heightSize - outerPadding - outerPadding, 0), MeasureSpec.EXACTLY);
        }
        editText.measure(childWidthMeasureSpec, childHeightMeasureSpec);

        setMeasuredDimension(editText.getMeasuredWidth() + outerPadding + outerPadding, editText.getMeasuredHeight() + outerPadding + outerPadding);

        if (firstLoad) {
            firstLoad = false;
            testClearButtonVisible();
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int viewWidth = getMeasuredWidth();
        int viewHeight = getMeasuredHeight();
        EditText editText = (EditText) getChildAt(0);
        ImageView clearButton = (ImageView) getChildAt(1);
        editText.layout(outerPadding, outerPadding, viewWidth - outerPadding, viewHeight - outerPadding);
        int clearButtonLeft = viewWidth - outerPadding - innerPadding - clearButton.getMeasuredWidth();
        int clearButtonTop = (viewHeight - clearButton.getMeasuredHeight()) / 2;
        clearButton.layout(clearButtonLeft, clearButtonTop, clearButtonLeft + clearButton.getMeasuredWidth(), clearButtonTop + clearButton.getMeasuredHeight());
    }

    public void setTextChangedListener(TextWatcher textWatcher) {
        EditText editText = (EditText) getChildAt(0);
        editText.removeTextChangedListener(textWatcher);
        editText.addTextChangedListener(textWatcher);
    }
}
