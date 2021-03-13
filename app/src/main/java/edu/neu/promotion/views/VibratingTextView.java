package edu.neu.promotion.views;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class VibratingTextView extends androidx.appcompat.widget.AppCompatTextView {

    public VibratingTextView(@NonNull Context context) {
        super(context);
        construct();
    }

    public VibratingTextView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        construct();
    }

    public VibratingTextView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        construct();
    }

    private static final int VIBE_TIMES = 8;
    private static final int VIBE_AMPLITUDE_DIP = 12;

    private ValueAnimator vibeAnimation;

    private void construct() {
        vibeAnimation = ValueAnimator.ofFloat(0, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, VIBE_AMPLITUDE_DIP, getResources().getDisplayMetrics()));
        vibeAnimation.setInterpolator(input -> (float) ((-input + 1) * Math.sin(input * VIBE_TIMES * Math.PI)));
        vibeAnimation.addUpdateListener(animation -> {
            setTranslationX(((float) animation.getAnimatedValue()));
        });
    }

    public void startVibe() {
        vibeAnimation.start();
    }
}
