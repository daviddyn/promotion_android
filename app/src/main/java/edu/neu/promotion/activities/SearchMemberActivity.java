package edu.neu.promotion.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import edu.neu.promotion.components.Page;
import edu.neu.promotion.components.PageActivity;
import edu.neu.promotion.pages.SearchMemberPage;

public class SearchMemberActivity extends PageActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentPage(new SearchMemberPage(this));
    }

    @Override
    public void onPageNotify(Page who, int notifyCode, Object... args) {
        super.onPageNotify(who, notifyCode, args);
        if (notifyCode == SearchMemberPage.RESULT_MEMBER_MODIFIED) {
            setResult(RESULT_OK, (Intent) args[0]);
        }
    }
}
