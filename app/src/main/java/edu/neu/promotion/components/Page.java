package edu.neu.promotion.components;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.core.content.res.ResourcesCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import edu.neu.promotion.R;

public class Page {

    static final class TipLabelInfo {
        public final int requestCode;
        public final Drawable icon;
        public final String tipText;
        public final int type;
        public final boolean clickable;
        public final boolean cancelable;

        public TipLabelInfo(int requestCode, Drawable icon, String tipText, int type, boolean clickable, boolean cancelable) {
            this.requestCode = requestCode;
            this.icon = icon;
            this.tipText = tipText;
            this.type = type;
            this.clickable = clickable;
            this.cancelable = cancelable;
        }
    }

    static final class ActionbarButtonInfo {
        public final Drawable icon;
        public final String toolTip;
        public boolean badge;

        ActionbarButtonInfo(Drawable icon, String toolTip) {
            this.icon = icon;
            this.toolTip = toolTip;
        }
    }

    private final Object[] args;
    private final PageManager pageManager;
    private final Context context;
    private final LayoutInflater layoutInflater;

    private boolean paused;

    private View contentView;
    private final ArrayList<TipLabelInfo> tipLabels;
    private int actionbarType;
    private String title;
    private final ArrayList<ActionbarButtonInfo> actionbarIcons;

    private String[] lastRequestingPermissions;

    public Page(PageManager pageManager, Object... args) {
        this.args = args;
        this.pageManager = pageManager;
        this.context = pageManager.onGetContext();
        this.layoutInflater = LayoutInflater.from(context);
        actionbarIcons = new ArrayList<>();
        tipLabels = new ArrayList<>();
    }

    protected final <T> T getArgument(int position) {
        return (T) args[position];
    }

    protected final boolean isPaused() {
        return paused;
    }

    protected final void post(Runnable runnable) {
        pageManager.onPost(runnable);
    }

    protected final void removePost(Runnable runnable) {
        pageManager.onRemovePost(runnable);
    }

    //资源管理

    protected final Context getContext() {
        return context;
    }

    protected final Resources getResource() {
        return context.getResources();
    }

    protected final float applyDimensions(int unit, float value) {
        return TypedValue.applyDimension(unit, value, context.getResources().getDisplayMetrics());
    }

    protected final int getColor(int colorResId) {
        return context.getResources().getColor(colorResId);
    }

    protected final String getString(int stringResId, Object... params) {
        return context.getResources().getString(stringResId, params);
    }

    protected final Drawable getDrawable(int drawableResId) {
        return ResourcesCompat.getDrawable(context.getResources(), drawableResId, null);
    }

    protected final LayoutInflater getLayoutInflater() {
        return layoutInflater;
    }

    protected final float getDimension(int dimenResId) {
        return context.getResources().getDimension(dimenResId);
    }

    //界面管理

    protected final View getContentView() {
        return contentView;
    }

    protected void setContentView(View v) {
        if (v != contentView) {
            contentView = v;
            if (!pageManager.isConstructing()) {
                pageManager.onContentViewChanged(this, contentView);
            }
        }
    }

    protected final void setContentView(int layoutResId) {
        setContentView(layoutInflater.inflate(layoutResId, null));
    }

    protected <T extends View> T findViewById(int viewId) {
        if (contentView == null) {
            return null;
        }
        return contentView.findViewById(viewId);
    }

    protected final List<TipLabelInfo> getTipLabels() {
        return tipLabels;
    }

    protected final void showTipLabel(int requestCode, Drawable icon, String tipText, int type, boolean clickable, boolean cancelable) {
        TipLabelInfo tipLabelInfo = new TipLabelInfo(requestCode, icon, tipText, type, clickable, cancelable);
        int i;
        for (i = 0; i < tipLabels.size() && tipLabels.get(i).requestCode != requestCode; i++);
        if (i == tipLabels.size()) {
            tipLabels.add(tipLabelInfo);
        }
        else {
            tipLabels.set(i, tipLabelInfo);
        }
        if (!pageManager.isConstructing()) {
            pageManager.onShowTipLabel(this, tipLabelInfo);
        }
    }

    protected final void removeTipLabel(int requestCode) {
        int i;
        for (i = 0; i < tipLabels.size() && tipLabels.get(i).requestCode != requestCode; i++);
        if (i < tipLabels.size()) {
            tipLabels.remove(i);
            if (!pageManager.isConstructing()) {
                pageManager.onRemoveTipLabel(this, requestCode);
            }
        }
    }

    //标题栏管理

    protected final int getActionbarStyle() {
        return actionbarType;
    }

    protected final void setActionbarStyle(int actionbarType) {
        if (this.actionbarType != actionbarType) {
            this.actionbarType = actionbarType;
            if (!pageManager.isConstructing()) {
                pageManager.onActionbarStyleChanged(this, actionbarType);
            }
        }
    }

    protected final String getTitle() {
        return title;
    }

    protected final void setTitle(String title) {
        if (!Objects.equals(title, this.title)) {
            this.title = title;
            if (!pageManager.isConstructing()) {
                pageManager.onTitleChanged(this, title);
            }
        }
    }

    protected final void setTitle(int stringResId) {
        setTitle(getString(stringResId));
    }

    protected final List<ActionbarButtonInfo> getActionbarIcons() {
        return actionbarIcons;
    }

    protected final void addActionbarButton(Drawable icon, String toolTip) {
        actionbarIcons.add(new ActionbarButtonInfo(icon, toolTip));
        if (!pageManager.isConstructing()) {
            pageManager.onActionbarButtonAdded(this, icon, toolTip);
        }
    }

    protected final void addActionbarButton(Drawable icon, int toolTipResId) {
        addActionbarButton(icon, getString(toolTipResId));
    }

    protected final void setActionbarButtonBadge(int position, boolean badge) {
        ActionbarButtonInfo info = actionbarIcons.get(position);
        if (info.badge != badge) {
            info.badge = badge;
            if (!pageManager.isConstructing()) {
                pageManager.onActionbarButtonBadgeChanged(this, position, badge);
            }
        }
    }

    protected final void removeActionbarButton(int position) {
        actionbarIcons.remove(position);
        if (!pageManager.isConstructing()) {
            pageManager.onActionbarButtonRemoved(this, position);
        }
    }

    //组件跳转

    protected final void startActivity(Intent intent) {
        pageManager.onStartActivity(intent);
    }

    protected final void startActivityForResult(Intent intent, int requestCode) {
        pageManager.onStartActivityForResult(this, intent, requestCode);
    }

    protected final void notifyParent(int notifyCode, Object... args) {
        pageManager.onPageNotify(this, notifyCode, args);
    }

    protected final void hideSoftKeyboard() {
        if (context instanceof Activity) {
            ((InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(((Activity) context).getWindow().getDecorView().getWindowToken(), 0);
        }
    }

    //权限管理

    protected final String[] getLastRequestingPermissions() {
        return lastRequestingPermissions;
    }

    protected final void requestPermissions(int requestCode, String[] permissions) {
        lastRequestingPermissions = permissions;
        pageManager.onRequestPermissions(this, requestCode, permissions);
    }

    public static boolean isPermissionsAllGranted(int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    //等重写

    protected void onCreate() {}

    protected void onDestroy() {}

    protected void onPause() {
        paused = true;
    }

    protected void onResume() {
        paused = false;
    }

    protected void onSwitchedOn() {}

    protected void onSwitchedOff() {}

    protected void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults) {}

    protected void onTipLabelAction(int requestCode, int action) {}

    protected boolean onGoBack() {
        return false;
    }

    protected void onActionbarButtonClick(int position, View viewForAnchor) {}

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {}
}