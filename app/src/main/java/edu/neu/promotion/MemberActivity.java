package edu.neu.promotion;

import android.os.Bundle;

import androidx.annotation.Nullable;

import edu.neu.promotion.components.Page;
import edu.neu.promotion.components.PageActivity;
import edu.neu.promotion.components.RunNetworkTaskPage;

public class MemberActivity extends PageActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentPage(new MemberPage(this));
    }

    @Override
    public void onPageNotify(Page who, int notifyCode, Object... args) {
        super.onPageNotify(who, notifyCode, args);
        if (notifyCode == RunNetworkTaskPage.RESULT_NEED_LOGIN) {
            setResult(RESULT_NEED_FINISH);
            finish();
        }
    }
}