package edu.neu.promotion.views;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.core.content.ContextCompat;

import edu.neu.promotion.R;

public class PasswordEditText extends ViewGroup {

    public PasswordEditText(Context context) {
        super(context);
        construct();
    }

    public PasswordEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        construct();
    }

    public PasswordEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        construct();
    }

    private int outerPadding;
    private int innerPadding;
    private boolean firstLoad;

    private OnClickListener changeVisibleButtonClickListener;
    private TextWatcher innerTextWatcher;

    private boolean visible;

    private TextWatcher textWatcher;

    private void construct() {
        Context context = getContext();

        outerPadding = (int) context.getResources().getDimension(R.dimen.input_padding_outer);
        innerPadding = (int) context.getResources().getDimension(R.dimen.input_padding_inner);
        setPadding(outerPadding, outerPadding, outerPadding, outerPadding);

        changeVisibleButtonClickListener = v -> {
            if (visible) {
                toInvisibleMode();
            }
            else {
                toVisibleMode();
            }
        };

        innerTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (textWatcher != null) {
                    textWatcher.beforeTextChanged(s, start, count, after);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (textWatcher != null) {
                    textWatcher.onTextChanged(s, start, before, count);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (textWatcher != null) {
                    textWatcher.afterTextChanged(s);
                }
            }
        };

        firstLoad = true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        EditText passwordEdit = (EditText) getChildAt(0);
        EditText visiblePasswordEdit = (EditText) getChildAt(1);
        ImageView changeButton = (ImageView) getChildAt(2);
        changeButton.setOnClickListener(changeVisibleButtonClickListener);

        //测量改变按钮
        changeButton.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));

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
        passwordEdit.measure(childWidthMeasureSpec, childHeightMeasureSpec);
        visiblePasswordEdit.measure(childWidthMeasureSpec, childHeightMeasureSpec);

        int contentWidth = Math.max(passwordEdit.getMeasuredWidth(), visiblePasswordEdit.getMeasuredHeight());
        int contentHeight = Math.max(passwordEdit.getMeasuredHeight(), visiblePasswordEdit.getMeasuredHeight());

        setMeasuredDimension(contentWidth + outerPadding + outerPadding, contentHeight + outerPadding + outerPadding);

        if (firstLoad) {
            firstLoad = false;
            toInvisibleMode();
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int viewWidth = getMeasuredWidth();
        int viewHeight = getMeasuredHeight();
        EditText passwordEdit = (EditText) getChildAt(0);
        EditText visiblePasswordEdit = (EditText) getChildAt(1);
        ImageView changeButton = (ImageView) getChildAt(2);
        passwordEdit.layout(outerPadding, outerPadding, viewWidth - outerPadding, viewHeight - outerPadding);
        visiblePasswordEdit.layout(outerPadding, outerPadding, viewWidth - outerPadding, viewHeight - outerPadding);
        int changeButtonLeft = viewWidth - outerPadding - innerPadding - changeButton.getMeasuredWidth();
        int changeButtonTop = (viewHeight - changeButton.getMeasuredHeight()) / 2;
        changeButton.layout(changeButtonLeft, changeButtonTop, changeButtonLeft + changeButton.getMeasuredWidth(), changeButtonTop + changeButton.getMeasuredHeight());
    }

    public EditText getEditText() {
        return (EditText) getChildAt(visible ? 1 : 0);
    }

    public void toVisibleMode() {
        EditText passwordEdit = (EditText) getChildAt(0);
        EditText visiblePasswordEdit = (EditText) getChildAt(1);
        ImageView changeButton = (ImageView) getChildAt(2);
        boolean hasFocus = passwordEdit.hasFocus();
        passwordEdit.setVisibility(GONE);
        visiblePasswordEdit.setVisibility(VISIBLE);
        passwordEdit.removeTextChangedListener(innerTextWatcher);
        changeButton.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.ic_input_visible));
        visiblePasswordEdit.setText(passwordEdit.getText());
        visiblePasswordEdit.setSelection(passwordEdit.getSelectionStart(), passwordEdit.getSelectionEnd());
        visiblePasswordEdit.setPadding(innerPadding, innerPadding, changeButton.getMeasuredWidth() + innerPadding + innerPadding, innerPadding);
        visible = true;
        if (hasFocus) {
            visiblePasswordEdit.requestFocus();
        }
        visiblePasswordEdit.addTextChangedListener(innerTextWatcher);
    }

    public void toInvisibleMode() {
        EditText passwordEdit = (EditText) getChildAt(0);
        EditText visiblePasswordEdit = (EditText) getChildAt(1);
        ImageView changeButton = (ImageView) getChildAt(2);
        boolean hasFocus = visiblePasswordEdit.hasFocus();
        passwordEdit.setVisibility(VISIBLE);
        visiblePasswordEdit.setVisibility(GONE);
        visiblePasswordEdit.removeTextChangedListener(innerTextWatcher);
        changeButton.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.ic_input_invisible));
        passwordEdit.setText(visiblePasswordEdit.getText());
        passwordEdit.setSelection(visiblePasswordEdit.getSelectionStart(), visiblePasswordEdit.getSelectionEnd());
        passwordEdit.setPadding(innerPadding, innerPadding, changeButton.getMeasuredWidth() + innerPadding + innerPadding, innerPadding);
        visible = false;
        if (hasFocus) {
            passwordEdit.requestFocus();
        }
        passwordEdit.addTextChangedListener(innerTextWatcher);
    }

    public void setTextChangedListener(TextWatcher textWatcher) {
        this.textWatcher = textWatcher;
    }
}
