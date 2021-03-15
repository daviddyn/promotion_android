package edu.neu.promotion;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;

import androidx.core.content.ContextCompat;

import edu.neu.promotion.components.BaseActivity;
import edu.neu.promotion.components.Page;
import edu.neu.promotion.components.PageManager;

public class HomePage extends Page {

    private View projectButtonArea;
    private ImageView projectButton;
    private View memberButtonArea;
    private ImageView memberButton;
    private View staticButtonArea;
    private ImageView staticButton;

    public HomePage(PageManager pageManager, Object... args) {
        super(pageManager, args);
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        setActionbarStyle(BaseActivity.ACTIONBAR_STYLE_NO_BACK);
        setTitle(R.string.home_home_page);

        addActionbarButton(ContextCompat.getDrawable(getContext(), R.drawable.ic_actionbar_notification), R.string.system_notification);
        setActionbarButtonBadge(0, true);
        addActionbarButton(ContextCompat.getDrawable(getContext(), R.drawable.ic_actionbar_scan), R.string.scan);

        View.OnClickListener onClickListener = v -> {
            if (v == projectButton) {
                startActivity(new Intent(getContext(), ProjectActivity.class));
            }
            else if (v == memberButton) {
                startActivity(new Intent(getContext(), MemberActivity.class));
            }
            else if (v == staticButtonArea) {

            }
        };

        setContentView(R.layout.page_home);
        projectButtonArea = findViewById(R.id.projectButtonArea);
        projectButton = findViewById(R.id.projectButton);
        projectButton.setOnClickListener(onClickListener);
        memberButtonArea = findViewById(R.id.memberButtonArea);
        memberButton = findViewById(R.id.memberButton);
        memberButton.setOnClickListener(onClickListener);
        staticButtonArea = findViewById(R.id.staticButtonArea);
        staticButton = findViewById(R.id.staticButton);
        staticButton.setOnClickListener(onClickListener);
    }


}
