package edu.neu.promotion.components;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.widget.TextViewCompat;

import edu.neu.promotion.R;
import edu.neu.promotion.views.BadgeView;

public class BaseActivity extends AppCompatActivity {

    public static final int RESULT_NEED_FINISH = RESULT_FIRST_USER;

    public static final int ACTIONBAR_STYLE_NORMAL = 0;
    public static final int ACTIONBAR_STYLE_NO_TITLE = 1;
    public static final int ACTIONBAR_STYLE_NO_BACK = 2;
    public static final int ACTIONBAR_STYLE_HIDE = 3;

    public static final int TIP_LABEL_TYPE_TIP = 0;
    public static final int TIP_LABEL_TYPE_ADV = 1;
    public static final int TIP_LABEL_TYPE_CRITICAL = 2;

    private View.OnClickListener onClickListener;
    private View.OnClickListener onActionbarButtonClickListener;
    private View.OnLongClickListener onActionbarButtonLongClickListener;
    private View.OnClickListener onTipLabelClickListener;
    private View.OnClickListener onTipLabelCancelListener;

    private View actionbarArea;
    private View backButton;
    private TextView titleTextView;
    private RelativeLayout.LayoutParams titleTextViewLayoutParams;
    private LinearLayout actionbarButtonArea;
    private View actionbarSplitter;
    private LinearLayout tipLabelArea;
    private FrameLayout contentViewContainer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        onClickListener = v -> {
            if (v.getId() == R.id.backButton) {
                onBackPressed();
            }
        };
        onActionbarButtonClickListener = v -> {
            onActionbarButtonClicked(actionbarButtonArea.indexOfChild(v), v);
        };
        onActionbarButtonLongClickListener = v-> {
            Toast.makeText(BaseActivity.this, (String)v.getTag(), Toast.LENGTH_SHORT).show();
            return true;
        };
        onTipLabelClickListener = v -> {
            onTipLabelAction(((int) v.getTag()), RESULT_OK);
        };
        onTipLabelCancelListener = v -> {
            v = (View) v.getParent();
            tipLabelArea.removeView(v);
            onTipLabelAction(((int) v.getTag()), RESULT_CANCELED);
        };

        super.setContentView(R.layout.activity_base);

        actionbarArea = super.findViewById(R.id.actionbarArea);
        backButton = super.findViewById(R.id.backButton);
        backButton.setOnClickListener(onClickListener);
        titleTextView = super.findViewById(R.id.titleTextView);
        titleTextViewLayoutParams = (RelativeLayout.LayoutParams) titleTextView.getLayoutParams();
        actionbarButtonArea = super.findViewById(R.id.actionbarButtonArea);
        actionbarSplitter = super.findViewById(R.id.actionbarSplitter);
        tipLabelArea = super.findViewById(R.id.tipLabelArea);
        contentViewContainer = super.findViewById(R.id.contentViewContainer);
    }

    @Override
    public void setContentView(int layoutResID) {
        contentViewContainer.removeAllViews();
        getLayoutInflater().inflate(layoutResID, contentViewContainer);
    }

    @Override
    public void setContentView(View view) {
        contentViewContainer.removeAllViews();
        if (view != null) {
            contentViewContainer.addView(view, -1, -1);
        }
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        contentViewContainer.removeAllViews();
        if (view != null) {
            contentViewContainer.addView(view, params);
        }
    }

    @Override
    public <T extends View> T findViewById(int id) {
        return contentViewContainer.findViewById(id);
    }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);
        titleTextView.setText(title);
    }

    @Override
    public void startActivity(Intent intent, @Nullable Bundle options) {
        super.startActivityForResult(intent, 0, options);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode, @Nullable Bundle options) {
        super.startActivityForResult(intent, requestCode + 1, options);
    }

    @Override
    protected final void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_NEED_FINISH) {
            setResult(RESULT_NEED_FINISH);
            finish();
        }
        else if (requestCode > 0) {
            onOverrideActivityResult(requestCode - 1, resultCode, data);
        }
    }

    protected void onOverrideActivityResult(int requestCode, int resultCode, @Nullable Intent data) {}

    //标题栏风格管理

    public void setActionbarStyle(int style) {
        switch (style) {
            case ACTIONBAR_STYLE_NORMAL:
                actionbarArea.setVisibility(View.VISIBLE);
                backButton.setVisibility(View.VISIBLE);
                titleTextView.setVisibility(View.VISIBLE);
                titleTextViewLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
                titleTextView.setLayoutParams(titleTextViewLayoutParams);
                actionbarSplitter.setVisibility(View.VISIBLE);
                break;
            case ACTIONBAR_STYLE_NO_TITLE:
                actionbarArea.setVisibility(View.VISIBLE);
                backButton.setVisibility(View.VISIBLE);
                titleTextView.setVisibility(View.GONE);
                titleTextViewLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
                titleTextView.setLayoutParams(titleTextViewLayoutParams);
                actionbarSplitter.setVisibility(View.GONE);
                break;
            case ACTIONBAR_STYLE_NO_BACK:
                actionbarArea.setVisibility(View.VISIBLE);
                backButton.setVisibility(View.GONE);
                titleTextView.setVisibility(View.VISIBLE);
                titleTextViewLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, 0);
                titleTextView.setLayoutParams(titleTextViewLayoutParams);
                actionbarSplitter.setVisibility(View.GONE);
                break;
            case ACTIONBAR_STYLE_HIDE:
                actionbarArea.setVisibility(View.GONE);
                actionbarSplitter.setVisibility(View.GONE);
                break;
        }
    }

    //标题栏按钮管理

    public void addActionbarButton(Drawable icon, String toolTip) {
        int position = actionbarButtonArea.getChildCount();
        getLayoutInflater().inflate(R.layout.item_actionbar_button, actionbarButtonArea);
        BadgeView badgeView = (BadgeView) actionbarButtonArea.getChildAt(position);
        badgeView.setOnClickListener(onActionbarButtonClickListener);
        if (toolTip != null) {
            badgeView.setTag(toolTip);
            badgeView.setOnLongClickListener(onActionbarButtonLongClickListener);
        }
        ((ImageView) badgeView.getChildAt(0)).setImageDrawable(icon);
    }

    public void addActionbarButton(Drawable icon, int toolTipStringResId) {
        addActionbarButton(icon, getString(toolTipStringResId));
    }

    public int getActionbarButtonCount() {
        return actionbarButtonArea.getChildCount();
    }

    public void removeAllActionbarButtons() {
        actionbarButtonArea.removeAllViews();
    }

    public void removeActionBarButton(int position) {
        actionbarButtonArea.removeViewAt(position);
    }

    public void setActionbarButtonBadge(int position, boolean badge) {
        ((BadgeView) actionbarButtonArea.getChildAt(position)).setBadgeVisible(badge);
    }

    protected void onActionbarButtonClicked(int position, View viewForAnchor) {}

    //提示条管理

    protected final void showTipLabel(int requestCode, Drawable icon, String tipText, int type, boolean clickable, boolean cancelable) {
        int position;
        for (position = 0; position < tipLabelArea.getChildCount() && ((int) tipLabelArea.getChildAt(position).getTag()) != requestCode; position++);
        View tipLabelView;
        if (position == tipLabelArea.getChildCount()) {
            getLayoutInflater().inflate(R.layout.item_tip_label, tipLabelArea);
            tipLabelView = tipLabelArea.getChildAt(position);
            tipLabelView.setTag(requestCode);
        }
        else {
            tipLabelView = tipLabelArea.getChildAt(position);
        }
        int color = 0;
        switch (type) {
            case TIP_LABEL_TYPE_TIP:
                tipLabelView.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.ripple_tip_label_tip, null));
                color = getResources().getColor(R.color.primary);
                break;
            case TIP_LABEL_TYPE_ADV:
                //暂不支持
                color = getResources().getColor(R.color.critical);
                break;
            case TIP_LABEL_TYPE_CRITICAL:
                tipLabelView.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.ripple_tip_label_critical, null));
                color = getResources().getColor(R.color.critical);
                break;
        }
        TextView textView = tipLabelView.findViewById(R.id.textView);
        textView.setText(tipText);
        textView.setTextColor(color);
        if (icon != null) {
            icon.setBounds(0, 0, icon.getIntrinsicWidth(), icon.getIntrinsicHeight());
            textView.setCompoundDrawables(icon, null, null, null);
            TextViewCompat.setCompoundDrawableTintList(textView, ColorStateList.valueOf(color));
        }
        if (clickable) {
            tipLabelView.setOnClickListener(onTipLabelClickListener);
        }
        ImageView closeButton = tipLabelView.findViewById(R.id.closeButton);
        if (cancelable) {
            closeButton.setVisibility(View.VISIBLE);
            closeButton.setOnClickListener(onTipLabelCancelListener);
        }
        else {
            closeButton.setVisibility(View.GONE);
        }
    }

    protected final void removeTipLabel(int requestCode) {
        for (int i = 0; i < tipLabelArea.getChildCount(); i++) {
            if (((int) tipLabelArea.getChildAt(i).getTag()) == requestCode) {
                tipLabelArea.removeViewAt(i);
                return;
            }
        }
    }

    protected final void removeAllTipLabels() {
        tipLabelArea.removeAllViews();
    }

    protected void onTipLabelAction(int requestCode, int action) {}

    public void post(Runnable runnable) {
        contentViewContainer.post(runnable);
    }

    public void removePost(Runnable runnable) {
        contentViewContainer.removeCallbacks(runnable);
    }
}