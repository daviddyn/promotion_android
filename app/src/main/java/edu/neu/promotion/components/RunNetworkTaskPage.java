package edu.neu.promotion.components;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.davidsoft.utils.JsonNode;

import edu.neu.promotion.R;
import edu.neu.promotion.ServerInvoker;
import edu.neu.promotion.StorageManager;

public class RunNetworkTaskPage extends RunTaskPage {

    public static final int RESULT_NEED_LOGIN = -1;

    public static final int TIP_LABEL_NETWORK_ERROR = 0;

    private static final int STATE_NORMAL = 0;
    private static final int STATE_LOADING = 1;
    private static final int STATE_ERROR = 2;

    private int state;
    private View contentView;

    private ValueAnimator contentViewAlphaAnimator;
    private AnimationDrawable loadingDrawable;
    private ImageView loadingView;
    private LinearLayout errorTipView;

    public RunNetworkTaskPage(PageManager pageManager, Object... args) {
        super(pageManager, args);
    }

    @Override
    protected void setContentView(View v) {
        contentView = v;
        if (state == STATE_NORMAL) {
            if (contentViewAlphaAnimator != null && contentViewAlphaAnimator.isRunning()) {
                contentViewAlphaAnimator.cancel();
            }
            super.setContentView(v);
        }
    }

    @Override
    protected <T extends View> T findViewById(int viewId) {
        if (contentView == null) {
            return null;
        }
        return contentView.findViewById(viewId);
    }

    @Override
    protected void onDestroy() {
        if (loadingDrawable != null) {
            loadingDrawable.stop();
        }
        super.onDestroy();
    }

    @Override
    protected void onTaskFailed(int requestCode, int currentRetryTimes) {
        if (currentRetryTimes == 0) {
            Toast.makeText(getContext(), R.string.toast_network_error, Toast.LENGTH_SHORT).show();
            showTipLabel(TIP_LABEL_NETWORK_ERROR, getDrawable(R.drawable.ic_network_error), getString(R.string.tip_label_network_error), BaseActivity.TIP_LABEL_TYPE_CRITICAL, true, false);
        }
    }

    @Override
    protected void onTaskResult(int requestCode, Object result) {
        super.onTaskResult(requestCode, result);
        removeTipLabel(TIP_LABEL_NETWORK_ERROR);
        if (result instanceof ServerInvoker.InvokeResult) {
            Object resultContent = ((ServerInvoker.InvokeResult) result).getContent();
            if (resultContent instanceof JsonNode) {
                JsonNode node = ((JsonNode) resultContent).getField("code");
                if (node != null && node.isPlain()) {
                    int code;
                    try {
                        code = Integer.parseInt(node.getValue());
                    }
                    catch (NumberFormatException e) {
                        return;
                    }
                    if (code / 100 == 4) {
                        cancelAllTasks();
                        Toast.makeText(getContext(), R.string.toast_login_expired, Toast.LENGTH_LONG).show();
                        StorageManager.clear(getContext(), StorageManager.TOKEN);
                        StorageManager.clear(getContext(), StorageManager.TOKEN_TYPE);
                        notifyParent(RESULT_NEED_LOGIN);
                    }
                    else if (code != 200) {
                        node = ((JsonNode) resultContent).getField("data");
                        if (node != null && node.isPlain()) {
                            Toast.makeText(getContext(), node.getValue(), Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }
        }
    }

    @Override
    protected void onTipLabelAction(int requestCode, int action) {
        super.onTipLabelAction(requestCode, action);
        if (requestCode == TIP_LABEL_NETWORK_ERROR) {
            startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
        }
    }

    protected final void toLoadingState() {
        state = STATE_LOADING;
        if (loadingDrawable == null) {
            loadingDrawable = (AnimationDrawable) getDrawable(R.drawable.bg_loading);
            loadingView = new ImageView(getContext());
            loadingView.setScaleType(ImageView.ScaleType.CENTER);
            loadingView.setImageDrawable(loadingDrawable);
        }
        loadingDrawable.start();
        super.setContentView(loadingView);
    }

    protected final void toErrorState() {
        if (loadingDrawable != null) {
            loadingDrawable.stop();
        }
        state = STATE_ERROR;
        if (errorTipView == null) {
            TextView textView = new TextView(getContext());
            textView.setText(R.string.network_retry);
            textView.setTextColor(getColor(R.color.text_tertiary));
            textView.setTextSize(16);
            Drawable drawable = getDrawable(R.drawable.bg_network_error);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            textView.setCompoundDrawables(null, drawable, null, null);
            textView.setOnClickListener(v -> onErrorStateRetry());
            errorTipView = new LinearLayout(getContext());
            errorTipView.setGravity(Gravity.CENTER);
            errorTipView.addView(textView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        }
        super.setContentView(errorTipView);
    }

    protected final void toNormalState() {
        if (loadingDrawable != null) {
            loadingDrawable.stop();
        }
        state = STATE_NORMAL;
        super.setContentView(contentView);
        if (contentView == null) {
            return;
        }
        contentView.setAlpha(0);
        if (contentViewAlphaAnimator == null) {
            contentViewAlphaAnimator = ValueAnimator.ofFloat(0, 1);
            contentViewAlphaAnimator.setInterpolator(new DecelerateInterpolator());
            contentViewAlphaAnimator.setDuration(getResource().getInteger(android.R.integer.config_longAnimTime));
            contentViewAlphaAnimator.addUpdateListener(animation -> contentView.setAlpha((Float) animation.getAnimatedValue()));
        }
        contentViewAlphaAnimator.start();
    }

    protected void onErrorStateRetry() {}
}
