package edu.neu.promotion;

import android.os.Bundle;

import androidx.annotation.Nullable;

import edu.neu.promotion.components.PageActivity;

public class WebActivity extends PageActivity {

    public static final String REQUEST_EXTRA_URL = "url";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentPage(new WebPage(this, getIntent().getStringExtra(REQUEST_EXTRA_URL)));
    }
}
