package edu.neu.promotion.pages;

import edu.neu.promotion.R;
import edu.neu.promotion.components.BaseActivity;
import edu.neu.promotion.components.Page;
import edu.neu.promotion.components.PageManager;

public class HomePage extends Page {

    public HomePage(PageManager pageManager, Object... args) {
        super(pageManager, args);
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        setActionbarStyle(BaseActivity.ACTIONBAR_STYLE_NO_BACK);
        setTitle("首页");
        setContentView(R.layout.page_home);
    }
}
