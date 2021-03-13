package edu.neu.promotion.components;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class PageActivity extends BaseActivity implements PageManager {

    private boolean constructing;

    private Page contentPage;

    private boolean paused;

    @Override
    protected void onResume() {
        super.onResume();
        paused = false;
        if (contentPage != null && contentPage.isPaused()) {
            contentPage.onResume();
        }
    }

    @Override
    protected void onPause() {
        paused = true;
        if (contentPage != null && !contentPage.isPaused()) {
            contentPage.onPause();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (contentPage != null) {
            constructing = true;
            contentPage.onSwitchedOff();
            contentPage.onDestroy();
            constructing = false;
        }
        super.onDestroy();
    }

    protected void setContentPage(Page contentPage) {
        constructing = true;
        if (this.contentPage != null) {
            this.contentPage.onSwitchedOff();
            this.contentPage.onDestroy();
        }
        this.contentPage = contentPage;
        if (contentPage == null) {
            constructing = false;
            return;
        }
        contentPage.onCreate();
        //标题
        String title = contentPage.getTitle();
        setTitle(title == null ? "" : title);
        setActionbarStyle(contentPage.getActionbarStyle());
        for (Page.ActionbarButtonInfo info : contentPage.getActionbarIcons()) {
            addActionbarButton(info.icon, info.toolTip);
            if (info.badge) {
                setActionbarButtonBadge(getActionbarButtonCount() - 1, true);
            }
        }
        //提示条
        removeAllTipLabels();
        for (Page.TipLabelInfo info : contentPage.getTipLabels()) {
            showTipLabel(info.requestCode, info.icon, info.tipText, info.type, info.clickable, info.cancelable);
        }
        //内容
        setContentView(contentPage.getContentView());
        contentPage.onSwitchedOn();
        if (paused) {
            contentPage.onPause();
        }
        constructing = false;
    }

    @Override
    public boolean isConstructing() {
        return constructing;
    }

    @Override
    public Context onGetContext() {
        return this;
    }

    @Override
    public void onContentViewChanged(Page who, View v) {
        setContentView(v);
    }

    @Override
    public void onShowTipLabel(Page who, Page.TipLabelInfo info) {
        if (who != contentPage) {
            return;
        }
        showTipLabel(info.requestCode, info.icon, info.tipText, info.type, info.clickable, info.cancelable);
    }

    @Override
    public void onRemoveTipLabel(Page who, int requestCode) {
        if (who != contentPage) {
            return;
        }
        removeTipLabel(requestCode);
    }

    @Override
    public void onActionbarStyleChanged(Page who, int actionbarType) {
        if (who != contentPage) {
            return;
        }
        setActionbarStyle(actionbarType);
    }

    @Override
    public void onTitleChanged(Page who, String title) {
        if (who != contentPage) {
            return;
        }
        setTitle(title);
    }

    @Override
    public void onActionbarButtonAdded(Page who, Drawable icon, String toolTip) {
        if (who != contentPage) {
            return;
        }
        addActionbarButton(icon, toolTip);
    }

    @Override
    public void onActionbarButtonRemoved(Page who, int position) {
        if (who != contentPage) {
            return;
        }
        removeActionBarButton(position);
    }

    @Override
    public void onActionbarButtonBadgeChanged(Page who, int position, boolean badge) {
        if (who != contentPage) {
            return;
        }
        setActionbarButtonBadge(position, badge);
    }

    @Override
    public void onRequestPermissions(Page who, int requestCode, String[] permissions) {
        if (who != contentPage) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ArrayList<String> permissionsToBeGrant = new ArrayList<>(permissions.length);
            for (String permission : permissions) {
                if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                    permissionsToBeGrant.add(permission);
                }
            }
            if (permissionsToBeGrant.isEmpty()) {
                who.onRequestPermissionResult(requestCode, permissions, new int[permissions.length]);
                return;
            }
            if (permissionsToBeGrant.size() < permissions.length) {
                permissions = new String[permissionsToBeGrant.size()];
                permissionsToBeGrant.toArray(permissions);
            }
            requestPermissions(permissions, requestCode);
        }
        else {
            who.onRequestPermissionResult(requestCode, permissions, new int[permissions.length]);
        }
    }

    @Override
    public void onStartActivity(Intent intent) {
        startActivity(intent);
    }

    @Override
    public void onStartActivityForResult(Page who, Intent intent, int requestCode) {
        startActivityForResult(intent, requestCode);
    }

    @Override
    public void onPost(Runnable runnable) {
        post(runnable);
    }

    @Override
    public void onRemovePost(Runnable runnable) {
        removePost(runnable);
    }

    @Override
    public void onPageNotify(Page who, int notifyCode, Object... args) {}

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        String[] lastRequestedPermissions = contentPage.getLastRequestingPermissions();
        int[] result = new int[lastRequestedPermissions.length];
        for (int i = 0; i < result.length; i++) {
            int search;
            for (search = 0; search < permissions.length && !permissions[search].equals(lastRequestedPermissions[search]); search++);
            if (search < permissions.length) {
                result[i] = grantResults[search];
            }
            else {
                result[i] = PackageManager.PERMISSION_GRANTED;
            }
        }
        contentPage.onRequestPermissionResult(requestCode, lastRequestedPermissions, result);
    }

    @Override
    protected void onOverrideActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onOverrideActivityResult(requestCode, resultCode, data);
        contentPage.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        if (contentPage != null && contentPage.onGoBack()) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onActionbarButtonClicked(int position, View viewForAnchor) {
        contentPage.onActionbarButtonClick(position, viewForAnchor);
    }

    @Override
    protected void onTipLabelAction(int requestCode, int action) {
        contentPage.onTipLabelAction(requestCode, action);
    }
}
