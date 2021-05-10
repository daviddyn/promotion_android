package edu.neu.promotion.activities;

import android.os.Bundle;

import androidx.annotation.Nullable;

import edu.neu.promotion.components.PageActivity;
import edu.neu.promotion.pages.WebPage;

public class WebActivity extends PageActivity {

    public static final String REQUEST_EXTRA_URL = "url";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentPage(new WebPage(this, getIntent().getStringExtra(REQUEST_EXTRA_URL)));
    }
}
