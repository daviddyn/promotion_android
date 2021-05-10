package edu.neu.promotion.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import edu.neu.promotion.components.PageActivity;
import edu.neu.promotion.pages.UserInfoPage;

public class UserInfoActivity extends PageActivity {

    public static final String REQUEST_EXTRA_ADMIN_ROLE_GROUP_INFO = "admin";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        setContentPage(new UserInfoPage(
                this,
                intent.getSerializableExtra(REQUEST_EXTRA_ADMIN_ROLE_GROUP_INFO)
        ));
    }
}
