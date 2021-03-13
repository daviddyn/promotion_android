package edu.neu.promotion.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.method.KeyListener;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.core.widget.TextViewCompat;

import edu.neu.promotion.R;

public class CaptchaInputView extends ViewGroup {

    public CaptchaInputView(Context context) {
        super(context);
        construct();
    }

    public CaptchaInputView(Context context, AttributeSet attrs) {
        super(context, attrs);
        construct();
    }

    public CaptchaInputView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        construct();
    }

    private static final ActionMode.Callback nullActionCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {

        }
    };

    private abstract class BaseInputFilter implements InputFilter {

        final EditText who;
        private boolean processing;

        BaseInputFilter(EditText who) {
            this.who = who;
        }

        /**
         * @param source 输入的文字
         * @param start 输入-0，删除-0
         * @param end 输入-文字的长度，删除-0
         * @param dest 原先显示的内容
         * @param dstart 输入-原光标位置，删除-光标删除结束位置
         * @param dend  输入-原光标位置，删除-光标删除开始位置
         */
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            if (setFromCode || processing) {
                return null;
            }
            processing = true;
            int operatingPos;
            for (operatingPos = 0; operatingPos < digitEdits.length; operatingPos++) {
                if (digitEdits[operatingPos] == who) {
                    break;
                }
            }
            if (operatingPos == digitEdits.length) {
                processing = false;
                return null;
            }
            if (end == 0) {
                who.setText("");
                //聚焦到前一个
                if (operatingPos > 0) {
                    digitEdits[operatingPos - 1].requestFocus();
                    digitEdits[operatingPos - 1].setSelection(digitEdits[operatingPos - 1].length());
                }
            }
            else {
                char exam = source.charAt(start);
                if (examineChar(exam)) {
                    who.setText(String.valueOf(exam).toUpperCase());
                    if (isFinish()) {
                        who.clearFocus();
                        if (onInputFinishListener != null) {
                            onInputFinishListener.OnInputFinish(CaptchaInputView.this, getInput());
                        }
                    }
                    else {
                        //聚焦到下一个
                        if (operatingPos < digitEdits.length - 1) {
                            digitEdits[operatingPos + 1].requestFocus();
                        }
                    }
                }
            }
            processing = false;
            return "";
        }

        abstract boolean examineChar(char c);
    }

    private final class DigitalInputFilter extends BaseInputFilter {

        DigitalInputFilter(EditText who) {
            super(who);
        }

        @Override
        boolean examineChar(char c) {
            return '0' <= c && c <= '9';
        }
    }

    private final class AlphaInputFilter extends BaseInputFilter {

        AlphaInputFilter(EditText who) {
            super(who);
        }

        @Override
        boolean examineChar(char c) {
            return '0' <= c && c <= '9' || 'A' <= c && c <= 'Z' || 'a' <= c && c <= 'z';
        }
    }

    public interface OnInputFinishListener {
        void OnInputFinish(CaptchaInputView who, String content);
    }

    private LayoutInflater layoutInflater;
    private boolean setFromCode;
    private EditText[] digitEdits;
    private int maxItemInterval;
    private int actualItemInterval;

    private KeyListener digitKeyListener;
    private KeyListener alphaKeyListener;

    private OnInputFinishListener onInputFinishListener;

    private void construct() {
        layoutInflater = LayoutInflater.from(getContext());
        maxItemInterval = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 18, getResources().getDisplayMetrics());
        digitKeyListener = new KeyListener() {
            @Override
            public int getInputType() {
                return InputType.TYPE_CLASS_NUMBER;
            }

            @Override
            public boolean onKeyDown(View view, Editable text, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    int operatingPos;
                    for (operatingPos = 0; operatingPos < digitEdits.length; operatingPos++) {
                        if (digitEdits[operatingPos] == view) {
                            break;
                        }
                    }
                    if (operatingPos == digitEdits.length) {
                        return false;
                    }
                    setFromCode = true;
                    ((EditText) view).setText("");
                    setFromCode = false;
                    if (operatingPos > 0) {
                        digitEdits[operatingPos - 1].requestFocus();
                        digitEdits[operatingPos - 1].setSelection(digitEdits[operatingPos - 1].length());
                    }
                    return true;
                }
                return false;
            }

            @Override
            public boolean onKeyUp(View view, Editable text, int keyCode, KeyEvent event) {
                return false;
            }

            @Override
            public boolean onKeyOther(View view, Editable text, KeyEvent event) {
                return false;
            }

            @Override
            public void clearMetaKeyState(View view, Editable content, int states) {

            }
        };
        alphaKeyListener = new KeyListener() {
            @Override
            public int getInputType() {
                return InputType.TYPE_CLASS_TEXT;
            }

            @Override
            public boolean onKeyDown(View view, Editable text, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    int operatingPos;
                    for (operatingPos = 0; operatingPos < digitEdits.length; operatingPos++) {
                        if (digitEdits[operatingPos] == view) {
                            break;
                        }
                    }
                    if (operatingPos == digitEdits.length) {
                        return false;
                    }
                    setFromCode = true;
                    ((EditText) view).setText("");
                    setFromCode = false;
                    if (operatingPos > 0) {
                        digitEdits[operatingPos - 1].requestFocus();
                        digitEdits[operatingPos - 1].setSelection(digitEdits[operatingPos - 1].length());
                    }
                    return true;
                }
                return false;
            }

            @Override
            public boolean onKeyUp(View view, Editable text, int keyCode, KeyEvent event) {
                return false;
            }

            @Override
            public boolean onKeyOther(View view, Editable text, KeyEvent event) {
                return false;
            }

            @Override
            public void clearMetaKeyState(View view, Editable content, int states) {

            }
        };
        reloadEditTexts(4, true);
    }

    @SuppressLint("InflateParams")
    private void reloadEditTexts(int count, boolean numberOnly) {
        Context context = getContext();
        Resources resources = getResources();

        removeAllViews();

        digitEdits = new EditText[count];
        for (int i = 0; i < digitEdits.length; i++) {
            digitEdits[i] = (EditText) layoutInflater.inflate(R.layout.edit_digit, null);
            if (numberOnly) {
                digitEdits[i].setFilters(new InputFilter[]{new DigitalInputFilter(digitEdits[i])});
                digitEdits[i].setKeyListener(digitKeyListener);
            }
            else {
                digitEdits[i].setFilters(new InputFilter[]{new AlphaInputFilter(digitEdits[i])});
                digitEdits[i].setKeyListener(alphaKeyListener);
            }
            TextViewCompat.setCustomSelectionActionModeCallback(digitEdits[i], nullActionCallback);
            addView(digitEdits[i]);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (digitEdits == null) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }

        int widthMode = MeasureSpec.getMode(widthMeasureSpec), widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec), heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int childWidthMeasureSpec, childHeightMeasureSpec;

        if (widthMode == MeasureSpec.UNSPECIFIED) {
            childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        }
        else {
            childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(
                    Math.max((widthSize - getPaddingLeft() - getPaddingRight()) / digitEdits.length, 0),
                    MeasureSpec.AT_MOST
            );
        }
        if (heightMode == MeasureSpec.UNSPECIFIED) {
            childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        }
        else {
            childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(
                    Math.max(heightSize - getPaddingTop() - getPaddingBottom(), 0),
                    MeasureSpec.AT_MOST
            );
        }

        int itemWidth, itemHeight;
        for (EditText e : digitEdits) {
            e.measure(childWidthMeasureSpec, childHeightMeasureSpec);
        }
        itemWidth = digitEdits[0].getMeasuredWidth();
        itemHeight = digitEdits[0].getMeasuredHeight();

        int viewWidth = 0, viewHeight = 0;
        switch (widthMode) {
            case MeasureSpec.UNSPECIFIED:
                viewWidth = itemWidth * digitEdits.length + Math.max(digitEdits.length - 1, 0) * maxItemInterval + getPaddingLeft() + getPaddingRight();
                actualItemInterval = maxItemInterval;
                break;
            case MeasureSpec.AT_MOST:
                viewWidth = itemWidth * digitEdits.length + Math.max(digitEdits.length - 1, 0) * maxItemInterval + getPaddingLeft() + getPaddingRight();
                if (viewWidth > widthSize) {
                    viewWidth = widthSize;
                    if (digitEdits.length == 1) {
                        actualItemInterval = maxItemInterval;
                    }
                    else {
                        actualItemInterval = (widthSize - getPaddingLeft() - getPaddingRight() - itemWidth * digitEdits.length) / (digitEdits.length - 1);
                    }
                }
                else {
                    actualItemInterval = maxItemInterval;
                }
                break;
            case MeasureSpec.EXACTLY:
                viewWidth = widthSize;
                if (digitEdits.length == 1) {
                    actualItemInterval = maxItemInterval;
                }
                else {
                    actualItemInterval = (widthSize - getPaddingLeft() - getPaddingRight() - itemWidth * digitEdits.length) / (digitEdits.length - 1);
                }
                break;
        }
        switch (heightMode) {
            case MeasureSpec.UNSPECIFIED:
                viewHeight = itemHeight + getPaddingTop() + getPaddingBottom();
                break;
            case MeasureSpec.AT_MOST:
                viewHeight = Math.min(itemHeight + getPaddingTop() + getPaddingBottom(), heightSize);
                break;
            case MeasureSpec.EXACTLY:
                viewHeight = heightSize;
                break;
        }

        setMeasuredDimension(viewWidth, viewHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (digitEdits == null) {
            return;
        }
        int itemWidth = digitEdits[0].getMeasuredWidth();
        int itemHeight = digitEdits[0].getMeasuredHeight();
        int contentWidth = itemWidth * digitEdits.length + actualItemInterval * (digitEdits.length - 1);
        int startX = getPaddingLeft() + (getMeasuredWidth() - getPaddingLeft() - getPaddingRight() - contentWidth) / 2;
        int y = getPaddingTop() + (getMeasuredHeight() - getPaddingTop() - getPaddingBottom() - itemHeight) / 2;
        for (EditText e : digitEdits) {
            e.layout(startX, y, startX + itemWidth, y + itemHeight);
            startX = startX + itemWidth + actualItemInterval;
        }
    }

    private boolean isFinish() {
        if (digitEdits == null) {
            return false;
        }
        for (EditText e : digitEdits) {
            if (e.length() == 0) {
                return false;
            }
        }
        return true;
    }

    public void setInputContent(int digitCount, boolean numberOnly) {
        reloadEditTexts(digitCount, numberOnly);
    }

    public void clear() {
        if (digitEdits == null) {
            return;
        }
        setFromCode = true;
        for (EditText e : digitEdits) {
            e.setText("");
        }
        setFromCode = false;
    }

    public void setOnInputFinishListener(OnInputFinishListener onInputFinishListener) {
        this.onInputFinishListener = onInputFinishListener;
    }

    public String getInput() {
        if (digitEdits == null) {
            return null;
        }
        StringBuilder builder = new StringBuilder(digitEdits.length);
        for (int i = 0; i < digitEdits.length; i++) {
            if (digitEdits[i].length() > 0) {
                builder.append(digitEdits[i].getText().charAt(0));
            }
        }
        return builder.toString();
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        for (EditText digitEdit : digitEdits) {
            digitEdit.setEnabled(enabled);
        }
    }
}
