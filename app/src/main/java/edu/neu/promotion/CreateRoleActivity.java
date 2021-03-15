package edu.neu.promotion;

import android.os.Bundle;

import androidx.annotation.Nullable;

import edu.neu.promotion.components.Page;
import edu.neu.promotion.components.PageNavigatingActivity;
import edu.neu.promotion.components.RunNetworkTaskPage;

public class CreateRoleActivity extends PageNavigatingActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        navigateForward(new CreateRolePage(this));
    }

    @Override
    public void onPageNotify(Page who, int notifyCode, Object... args) {
        super.onPageNotify(who, notifyCode, args);
        if (notifyCode == RunNetworkTaskPage.RESULT_NEED_LOGIN) {
            setResult(RESULT_NEED_FINISH);
            finish();
            return;
        }
        if (who instanceof CreateRolePage) {
            switch (notifyCode) {
                case CreateRolePage.RESULT_CREATED:
                    onRoleCreated();
                    break;
                case CreateRolePage.RESULT_STEP_NEXT:
                    navigateForward(new CreateGroupPage(this, args));
                    break;
            }
        }
        else if (who instanceof CreateGroupPage) {
            onRoleCreated();
        }
    }

    private void onRoleCreated() {
        setResult(RESULT_OK);
        finish();
    }
}
