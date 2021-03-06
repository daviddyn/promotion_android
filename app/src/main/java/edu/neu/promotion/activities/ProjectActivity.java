package edu.neu.promotion.activities;

import android.os.Bundle;

import androidx.annotation.Nullable;

import edu.neu.promotion.components.Page;
import edu.neu.promotion.components.PageActivity;
import edu.neu.promotion.components.RunNetworkTaskPage;
import edu.neu.promotion.pages.ProjectPage;

public class ProjectActivity extends PageActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentPage(new ProjectPage(this));
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
