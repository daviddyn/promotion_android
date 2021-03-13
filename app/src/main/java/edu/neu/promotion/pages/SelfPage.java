package edu.neu.promotion.pages;

import edu.neu.promotion.R;
import edu.neu.promotion.components.BaseActivity;
import edu.neu.promotion.components.Page;
import edu.neu.promotion.components.PageManager;

public class SelfPage extends Page {

    public SelfPage(PageManager pageManager, Object... args) {
        super(pageManager, args);
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        setActionbarStyle(BaseActivity.ACTIONBAR_STYLE_NO_BACK);
        setTitle("我");
        setContentView(R.layout.page_self);

        addActionbarButton(getDrawable(R.drawable.ic_actionbar_notification), R.string.system_notification);
        setActionbarButtonBadge(0, true);
    }
}
