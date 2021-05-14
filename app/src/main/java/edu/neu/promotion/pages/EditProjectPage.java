package edu.neu.promotion.pages;

import edu.neu.promotion.R;
import edu.neu.promotion.components.PageManager;

public class EditProjectPage extends TokenRunNetworkTaskPage {

    public EditProjectPage(PageManager pageManager, Object... args) {
        super(pageManager, args);
    }

    @Override
    protected void onCreate() {
        super.onCreate();

        setContentView(R.layout.page_edit_project);
    }
}
