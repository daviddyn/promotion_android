package edu.neu.promotion.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import edu.neu.promotion.components.Page;
import edu.neu.promotion.components.PageActivity;
import edu.neu.promotion.components.RunNetworkTaskPage;
import edu.neu.promotion.pages.MemberInfoPage;

public class MemberInfoActivity extends PageActivity {

    public static final String REQUEST_EXTRA_ADMIN_ROLE_GROUP_INFO = "admin";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        setContentPage(new MemberInfoPage(
                this,
                intent.getSerializableExtra(REQUEST_EXTRA_ADMIN_ROLE_GROUP_INFO)
        ));
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
