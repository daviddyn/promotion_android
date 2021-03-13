package edu.neu.promotion;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.annotation.Nullable;

import edu.neu.promotion.components.Page;
import edu.neu.promotion.components.PageActivity;
import edu.neu.promotion.components.RunNetworkTaskPage;
import edu.neu.promotion.pages.HomePage;
import edu.neu.promotion.pages.RichEditPage;
import edu.neu.promotion.pages.SelectRolePage;
import edu.neu.promotion.pages.SelfPage;
import edu.neu.promotion.pages.WebPage;
import jp.wasabeef.richeditor.RichEditor;

public class SelectRoleActivity extends PageActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentPage(new SelectRolePage(this));
    }

    @Override
    public void onPageNotify(Page who, int notifyCode, Object... args) {
        super.onPageNotify(who, notifyCode, args);
        if (notifyCode == RunNetworkTaskPage.RESULT_NEED_LOGIN) {
            setResult(RESULT_NEED_FINISH);
            finish();
            return;
        }
        setResult(RESULT_OK);
        finish();
    }
}