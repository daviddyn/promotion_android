package edu.neu.promotion.views;

import android.text.InputFilter;
import android.text.Spanned;

public class PhoneInputFilter implements InputFilter {

    private final StringBuilder stringBuilder;

    public PhoneInputFilter() {
        stringBuilder = new StringBuilder();
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        if (end == 0) {
            //要删除
            return null;
        }
        stringBuilder.delete(0, stringBuilder.length());
        for (int i = 0; i < source.length(); i++) {
            char exam = source.charAt(start);
            if (exam >= '0' && exam <= '9') {
                stringBuilder.append(exam);
            }
        }
        return stringBuilder.subSequence(0, Math.min(stringBuilder.length(), 11 - dest.length()));
    }
}
