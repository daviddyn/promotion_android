package edu.neu.promotion;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import edu.neu.promotion.components.Page;
import edu.neu.promotion.components.PageActivity;

public class SearchSelectItemActivity extends PageActivity {

    public static final String REQUEST_EXTRA_TITLE = "title";
    public static final String REQUEST_EXTRA_ITEMS = "items";
    public static final String REQUEST_EXTRA_INDEX = "index";
    public static final String REQUEST_EXTRA_BELONGS = "belongs";
    public static final String RESULT_EXTRA_POSITION = "position";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        SearchSelectItemPage searchSelectItemPage;
        if (intent.hasExtra(REQUEST_EXTRA_INDEX) && intent.hasExtra(REQUEST_EXTRA_BELONGS)) {
            searchSelectItemPage = new SearchSelectItemPage(this,
                    intent.getStringExtra(REQUEST_EXTRA_TITLE),
                    intent.getSerializableExtra(REQUEST_EXTRA_ITEMS),
                    intent.getSerializableExtra(REQUEST_EXTRA_INDEX),
                    intent.getSerializableExtra(REQUEST_EXTRA_BELONGS)
            );
        }
        else {
            searchSelectItemPage = new SearchSelectItemPage(this,
                    intent.getStringExtra(REQUEST_EXTRA_TITLE),
                    intent.getSerializableExtra(REQUEST_EXTRA_ITEMS)
            );
        }
        setContentPage(searchSelectItemPage);
    }

    @Override
    public void onPageNotify(Page who, int notifyCode, Object... args) {
        super.onPageNotify(who, notifyCode, args);
        Intent intent = new Intent();
        intent.putExtra(RESULT_EXTRA_POSITION, (int)args[0]);
        setResult(RESULT_OK, intent);
        finish();
    }
}
