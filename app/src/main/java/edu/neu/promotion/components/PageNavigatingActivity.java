package edu.neu.promotion.components;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

import edu.neu.promotion.views.StepView;

public class PageNavigatingActivity extends BaseActivity implements PageManager {

    private boolean paused;
    private boolean constructing;

    private ArrayList<Page> pageStack;

    private StepView stepView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageStack = new ArrayList<>();
        stepView = new StepView(this);
        setContentView(stepView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (paused) {
            paused = false;
            if (!pageStack.isEmpty()) {
                pageStack.get(pageStack.size() - 1).onResume();
            }
        }
    }

    @Override
    protected void onPause() {
        paused = true;
        if (!pageStack.isEmpty()) {
            pageStack.get(pageStack.size() - 1).onPause();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        constructing = true;
        if (!pageStack.isEmpty()) {
            pageStack.get(pageStack.size() - 1).onSwitchedOff();
        }
        for (Page p : pageStack) {
            p.onDestroy();
        }
        constructing = false;
        super.onDestroy();
    }

    private void loadCurrentPage() {
        Page currentPage = pageStack.get(pageStack.size() - 1);
        //标题
        String title = currentPage.getTitle();
        setTitle(title == null ? "" : title);
        setActionbarStyle(currentPage.getActionbarStyle());
        removeAllActionbarButtons();
        for (Page.ActionbarButtonInfo info : currentPage.getActionbarIcons()) {
            addActionbarButton(info.icon, info.toolTip);
            if (info.badge) {
                setActionbarButtonBadge(getActionbarButtonCount() - 1, true);
            }
        }
        //提示条
        removeAllTipLabels();
        for (Page.TipLabelInfo info : currentPage.getTipLabels()) {
            showTipLabel(info.requestCode, info.icon, info.tipText, info.type, info.clickable, info.cancelable);
        }
    }

    private Page currentPage() {
        return pageStack.isEmpty() ? null : pageStack.get(pageStack.size() - 1);
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
        if (who != currentPage()) {
            return;
        }
        stepView.removeViewAt(pageStack.size() - 1);
        stepView.addView(v);
    }

    @Override
    public void onShowTipLabel(Page who, Page.TipLabelInfo info) {
        if (who != currentPage()) {
            return;
        }
        showTipLabel(info.requestCode, info.icon, info.tipText, info.type, info.clickable, info.cancelable);
    }

    @Override
    public void onRemoveTipLabel(Page who, int requestCode) {
        if (who != currentPage()) {
            return;
        }
        removeTipLabel(requestCode);
    }

    @Override
    public void onActionbarStyleChanged(Page who, int actionbarType) {
        if (who != currentPage()) {
            return;
        }
        setActionbarStyle(actionbarType);
    }

    @Override
    public void onTitleChanged(Page who, String title) {
        if (who != currentPage()) {
            return;
        }
        setTitle(title);
    }

    @Override
    public void onActionbarButtonAdded(Page who, Drawable icon, String toolTip) {
        if (who != currentPage()) {
            return;
        }
        addActionbarButton(icon, toolTip);
    }

    @Override
    public void onActionbarButtonRemoved(Page who, int position) {
        if (who != currentPage()) {
            return;
        }
        removeActionBarButton(position);
    }

    @Override
    public void onActionbarButtonBadgeChanged(Page who, int position, boolean badge) {
        if (who != currentPage()) {
            return;
        }
        setActionbarButtonBadge(position, badge);
    }

    @Override
    public void onRequestPermissions(Page who, int requestCode, String[] permissions) {
        int position = pageStack.indexOf(who);
        if (position == -1) {
            return;
        }
        requestCode = requestCode | (position << 8);
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
        int position = pageStack.indexOf(who);
        if (position == -1) {
            return;
        }
        requestCode = requestCode | (position << 8);
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
        int position = requestCode >> 8;
        requestCode = requestCode & 0xFF;
        String[] lastRequestedPermissions = pageStack.get(position).getLastRequestingPermissions();
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
        pageStack.get(position).onRequestPermissionResult(requestCode, lastRequestedPermissions, result);
    }

    @Override
    protected void onOverrideActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onOverrideActivityResult(requestCode, resultCode, data);
        int position = requestCode >> 8;
        requestCode = requestCode & 0xFF;
        pageStack.get(position).onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        if (pageStack.isEmpty()) {
            super.onBackPressed();
            return;
        }
        if (pageStack.get(pageStack.size() - 1).onGoBack()) {
            return;
        }
        if (pageStack.size() == 1) {
            super.onBackPressed();
        }
        else {
            navigateBack();
        }
    }

    @Override
    protected void onActionbarButtonClicked(int position, View viewForAnchor) {
        currentPage().onActionbarButtonClick(position, viewForAnchor);
    }

    @Override
    protected void onTipLabelAction(int requestCode, int action) {
        currentPage().onTipLabelAction(requestCode, action);
    }

    protected void navigateForward(Page page) {
        hideSoftKeyboard();
        constructing = true;
        if (!pageStack.isEmpty()) {
            pageStack.get(pageStack.size() - 1).onSwitchedOff();
        }
        pageStack.add(page);
        page.onCreate();
        page.onSwitchedOn();
        if (paused) {
            page.onPause();
        }
        loadCurrentPage();
        constructing = false;
        View contentView = page.getContentView();
        if (contentView == null) {
            contentView = new View(this);
        }
        stepView.stepForward(contentView);
    }

    protected void navigateBackTo(int position) {
        hideSoftKeyboard();
        if (position < 0 || position >= pageStack.size() - 1) {
            return;
        }
        constructing = true;
        for (int i = pageStack.size() - 1; i > position; i--) {
            Page p = pageStack.remove(i);
            p.onSwitchedOff();
            p.onDestroy();
        }
        Page p = pageStack.get(position);
        p.onSwitchedOn();
        loadCurrentPage();
        constructing = false;
        stepView.removeViewAt(position);
        stepView.addView(p.getContentView(), position);
        stepView.stepBackwardTo(position);
    }

    protected void navigateBack() {
        navigateBackTo(pageStack.size() - 2);
    }

    private void hideSoftKeyboard() {
        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
    }
}