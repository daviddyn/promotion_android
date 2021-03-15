package edu.neu.promotion;

import edu.neu.promotion.R;
import edu.neu.promotion.components.BaseActivity;
import edu.neu.promotion.components.Page;
import edu.neu.promotion.components.PageManager;

public class NewsPage extends Page {

    public NewsPage(PageManager pageManager, Object... args) {
        super(pageManager, args);
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        setTitle(R.string.home_news);
        setActionbarStyle(BaseActivity.ACTIONBAR_STYLE_NO_BACK);
    }
}
