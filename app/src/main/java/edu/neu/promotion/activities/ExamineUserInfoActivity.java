package edu.neu.promotion.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import edu.neu.promotion.components.Page;
import edu.neu.promotion.components.PageActivity;
import edu.neu.promotion.components.RunNetworkTaskPage;
import edu.neu.promotion.pages.ExamineUserInfoPage;

public class ExamineUserInfoActivity extends PageActivity {

    public static final int RESULT_ACCEPT = RESULT_OK;
    public static final int RESULT_DENIED = RESULT_FIRST_USER + 10;
    public static final String RESULT_EXTRA_NEW_CHECK_STATE = "checkState";

    public static final String REQUEST_EXTRA_ADMIN_ROLE_GROUP_INFO = "admin";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        setContentPage(new ExamineUserInfoPage(
                this,
                intent.getSerializableExtra(REQUEST_EXTRA_ADMIN_ROLE_GROUP_INFO)
        ));
    }

    @Override
    public void onPageNotify(Page who, int notifyCode, Object... args) {
        super.onPageNotify(who, notifyCode, args);
        Intent data;
        switch (notifyCode) {
            case RunNetworkTaskPage.RESULT_NEED_LOGIN:
                setResult(RESULT_NEED_FINISH);
                finish();
                break;
            case ExamineUserInfoPage.RESULT_ACCEPT:
                data = new Intent();
                data.putExtra(RESULT_EXTRA_NEW_CHECK_STATE, ((String) args[0]));
                setResult(RESULT_ACCEPT, data);
                break;
            case ExamineUserInfoPage.RESULT_DENIED:
                setResult(RESULT_DENIED);
                finish();
                break;
        }
    }
}
