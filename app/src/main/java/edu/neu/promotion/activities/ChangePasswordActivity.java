package edu.neu.promotion.activities;

import android.os.Bundle;

import androidx.annotation.Nullable;

import edu.neu.promotion.components.Page;
import edu.neu.promotion.components.PageNavigatingActivity;
import edu.neu.promotion.components.RunNetworkTaskPage;
import edu.neu.promotion.pages.NewPasswordPage;
import edu.neu.promotion.pages.OriginalPasswordPage;

public class ChangePasswordActivity extends PageNavigatingActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        navigateForward(new OriginalPasswordPage(this));
    }

    @Override
    public void onPageNotify(Page who, int notifyCode, Object... args) {
        super.onPageNotify(who, notifyCode, args);
        if (who instanceof OriginalPasswordPage) {
            navigateForward(new NewPasswordPage(this, args));
        }
        else {
            if (notifyCode == RunNetworkTaskPage.RESULT_NEED_LOGIN) {
                setResult(RESULT_NEED_FINISH);
                finish();
                return;
            }
            //TODO: 修改密码
        }
    }
}
