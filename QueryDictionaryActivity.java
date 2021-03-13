package edu.neu.promotion;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import edu.neu.promotion.components.Page;
import edu.neu.promotion.components.PageActivity;
import edu.neu.promotion.enties.DictionaryItemNode;
import edu.neu.promotion.pages.QueryDictionaryPage;

public class QueryDictionaryActivity extends PageActivity {

    public static final String REQUEST_EXTRA_TITLE = "title";
    public static final String REQUEST_EXTRA_DICTIONARY_TYPE = "dictionaryType";
    public static final String RESULT_EXTRA_DICTIONARY_ITEM = "dictionaryItem";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        setContentPage(new QueryDictionaryPage(this, intent.getStringExtra(REQUEST_EXTRA_TITLE), intent.getStringExtra(REQUEST_EXTRA_DICTIONARY_TYPE)));
    }

    @Override
    public void onPageNotify(Page who, int notifyCode, Object... args) {
        super.onPageNotify(who, notifyCode, args);
        if (notifyCode == QueryDictionaryPage.RESULT_OK) {
            Intent intent = new Intent();
            intent.putExtra(RESULT_EXTRA_DICTIONARY_ITEM, (DictionaryItemNode)args[0]);
            setResult(RESULT_OK, intent);
        }
        finish();
    }
}
