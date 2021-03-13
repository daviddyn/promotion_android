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

public class PageSwitchingActivity extends BaseActivity implements PageManager {

    private boolean paused;
    private boolean constructing;

    private int currentPage;
    private ArrayList<Page> pages;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pages = new ArrayList<>();
        currentPage = -1;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (paused) {
            paused = false;
            if (!pages.isEmpty()) {
                pages.get(currentPage).onResume();
            }
        }
    }

    @Override
    protected void onPause() {
        paused = true;
        if (!pages.isEmpty()) {
            pages.get(currentPage).onPause();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        constructing = true;
        if (!pages.isEmpty()) {
            pages.get(currentPage).onSwitchedOff();
        }
        for (Page p : pages) {
            p.onDestroy();
        }
        constructing = false;
        super.onDestroy();
    }

    private void loadCurrentPage() {
        Page p = pages.get(currentPage);
        //标题
        String title = p.getTitle();
        setTitle(title == null ? "" : title);
        setActionbarStyle(p.getActionbarStyle());
        removeAllActionbarButtons();
        for (Page.ActionbarButtonInfo info : p.getActionbarIcons()) {
            addActionbarButton(info.icon, info.toolTip);
            if (info.badge) {
                setActionbarButtonBadge(getActionbarButtonCount() - 1, true);
            }
        }
        //提示条
        removeAllTipLabels();
        for (Page.TipLabelInfo info : p.getTipLabels()) {
            showTipLabel(info.requestCode, info.icon, info.tipText, info.type, info.clickable, info.cancelable);
        }
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
        int position = pages.indexOf(who);
        if (position != currentPage) {
            return;
        }
        setContentView(v);
    }

    @Override
    public void onShowTipLabel(Page who, Page.TipLabelInfo info) {
        int position = pages.indexOf(who);
        if (position != currentPage) {
            return;
        }
        showTipLabel(info.requestCode, info.icon, info.tipText, info.type, info.clickable, info.cancelable);
    }

    @Override
    public void onRemoveTipLabel(Page who, int requestCode) {
        int position = pages.indexOf(who);
        if (position != currentPage) {
            return;
        }
        removeTipLabel(requestCode);
    }

    @Override
    public void onActionbarStyleChanged(Page who, int actionbarType) {
        int position = pages.indexOf(who);
        if (position != currentPage) {
            return;
        }
        setActionbarStyle(actionbarType);
    }

    @Override
    public void onTitleChanged(Page who, String title) {
        int position = pages.indexOf(who);
        if (position != currentPage) {
            return;
        }
        setTitle(title);
    }

    @Override
    public void onActionbarButtonAdded(Page who, Drawable icon, String toolTip) {
        int position = pages.indexOf(who);
        if (position != currentPage) {
            return;
        }
        addActionbarButton(icon, toolTip);
    }

    @Override
    public void onActionbarButtonRemoved(Page who, int position) {
        int pp = pages.indexOf(who);
        if (pp != currentPage) {
            return;
        }
        removeActionBarButton(position);
    }

    @Override
    public void onActionbarButtonBadgeChanged(Page who, int position, boolean badge) {
        int pp = pages.indexOf(who);
        if (pp != currentPage) {
            return;
        }
        setActionbarButtonBadge(position, badge);
    }

    @Override
    public void onRequestPermissions(Page who, int requestCode, String[] permissions) {
        int position = pages.indexOf(who);
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
        } else {
            who.onRequestPermissionResult(requestCode, permissions, new int[permissions.length]);
        }
    }

    @Override
    public void onStartActivity(Intent intent) {
        startActivity(intent);
    }

    @Override
    public void onStartActivityForResult(Page who, Intent intent, int requestCode) {
        int position = pages.indexOf(who);
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
    public void onPageNotify(Page who, int notifyCode, Object... args) {
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        int position = requestCode >> 8;
        requestCode = requestCode & 0xFF;
        String[] lastRequestedPermissions = pages.get(position).getLastRequestingPermissions();
        int[] result = new int[lastRequestedPermissions.length];
        for (int i = 0; i < result.length; i++) {
            int search;
            for (search = 0; search < permissions.length && !permissions[search].equals(lastRequestedPermissions[search]); search++)
                ;
            if (search < permissions.length) {
                result[i] = grantResults[search];
            } else {
                result[i] = PackageManager.PERMISSION_GRANTED;
            }
        }
        pages.get(position).onRequestPermissionResult(requestCode, lastRequestedPermissions, result);
    }

    @Override
    protected void onOverrideActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onOverrideActivityResult(requestCode, resultCode, data);
        int position = requestCode >> 8;
        requestCode = requestCode & 0xFF;
        pages.get(position).onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        if (pages.isEmpty()) {
            super.onBackPressed();
            return;
        }
        if (pages.get(currentPage).onGoBack()) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onActionbarButtonClicked(int position, View viewForAnchor) {
        pages.get(currentPage).onActionbarButtonClick(position, viewForAnchor);
    }

    @Override
    protected void onTipLabelAction(int requestCode, int action) {
        pages.get(currentPage).onTipLabelAction(requestCode, action);
    }

    private void hideSoftKeyboard() {
        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
    }

    protected void addContentPage(Page page) {
        pages.add(page);
        constructing = true;
        page.onCreate();
        if (paused) {
            page.onPause();
        }
        constructing = false;
    }

    protected void switchToPage(int position) {
        hideSoftKeyboard();
        if (position < 0 || position >= pages.size() || position == currentPage) {
            return;
        }
        constructing = true;
        if (currentPage >= 0 && currentPage < pages.size()) {
            pages.get(currentPage).onSwitchedOff();
        }
        onPageSwitched(currentPage, position);
        currentPage = position;
        Page p = pages.get(position);
        p.onSwitchedOn();
        loadCurrentPage();
        setContentView(p.getContentView());
        constructing = false;
    }

    protected void onPageSwitched(int oldPosition, int position) {}
}
