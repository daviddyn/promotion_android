package edu.neu.promotion.components;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.View;

public interface PageManager {

    boolean isConstructing();

    Context onGetContext();

    void onContentViewChanged(Page who, View v);

    void onShowTipLabel(Page who, Page.TipLabelInfo tipLabelInfo);

    void onRemoveTipLabel(Page who, int requestCode);

    void onActionbarStyleChanged(Page who, int actionbarType);

    void onTitleChanged(Page who, String title);

    void onActionbarButtonAdded(Page who, Drawable icon, String toolTip);

    void onActionbarButtonRemoved(Page who, int position);

    void onActionbarButtonBadgeChanged(Page who, int position, boolean badge);

    void onRequestPermissions(Page who, int requestCode, String[] permissions);

    void onStartActivity(Intent intent);

    void onStartActivityForResult(Page who, Intent intent, int requestCode);

    void onPost(Runnable runnable);

    void onRemovePost(Runnable runnable);

    void onPageNotify(Page who, int notifyCode, Object... args);
}
