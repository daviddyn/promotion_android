package edu.neu.promotion.activities;

import android.os.Bundle;

import androidx.annotation.Nullable;

import edu.neu.promotion.components.PageActivity;
import edu.neu.promotion.pages.EditProjectPage;

public class EditProjectActivity extends PageActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentPage(new EditProjectPage(this));
    }
}
