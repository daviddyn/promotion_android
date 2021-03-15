package edu.neu.promotion;

import android.os.Bundle;

import androidx.annotation.Nullable;

import edu.neu.promotion.components.PageActivity;

public class MemberActivity extends PageActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentPage(new MemberPage(this));
    }
}