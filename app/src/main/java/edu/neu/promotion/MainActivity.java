package edu.neu.promotion;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;

import edu.neu.promotion.components.Page;
import edu.neu.promotion.components.PageSwitchingActivity;
import edu.neu.promotion.components.RunNetworkTaskPage;
import edu.neu.promotion.pages.HomePage;
import edu.neu.promotion.pages.NewsPage;
import edu.neu.promotion.pages.SelfPage;

public class MainActivity extends PageSwitchingActivity {

    private FrameLayout content;
    private ImageView[] buttonImages;
    private TextView[] buttonTexts;

    private View newsButton;
    private View homeButton;
    private View selfButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View.OnClickListener onClickListener = v -> {
            if (v == newsButton) {
                switchToPage(0);
            }
            else if (v == homeButton) {
                switchToPage(1);
            }
            else if (v == selfButton) {
                switchToPage(2);
            }
        };

        super.setContentView(R.layout.activity_main);
        content = super.findViewById(R.id.content);
        newsButton = super.findViewById(R.id.newsButton);
        newsButton.setOnClickListener(onClickListener);
        homeButton = super.findViewById(R.id.homeButton);
        homeButton.setOnClickListener(onClickListener);
        selfButton = super.findViewById(R.id.selfButton);
        selfButton.setOnClickListener(onClickListener);
        buttonImages = new ImageView[] {
                super.findViewById(R.id.newsButtonImage),
                super.findViewById(R.id.homeButtonImage),
                super.findViewById(R.id.selfButtonImage)
        };
        buttonTexts = new TextView[] {
                super.findViewById(R.id.newsButtonText),
                super.findViewById(R.id.homeButtonText),
                super.findViewById(R.id.selfButtonText)
        };

        addContentPage(new NewsPage(this));
        addContentPage(new HomePage(this));
        addContentPage(new SelfPage(this));

        switchToPage(1);

        addActionbarButton(ContextCompat.getDrawable(this, R.drawable.ic_actionbar_notification), R.string.system_notification);
        setActionbarButtonBadge(0, true);
    }

    @Override
    public void onPageNotify(Page who, int notifyCode, Object... args) {
        super.onPageNotify(who, notifyCode, args);
        if (notifyCode == RunNetworkTaskPage.RESULT_NEED_LOGIN) {
            setResult(RESULT_NEED_FINISH);
            finish();
            return;
        }
    }

    @Override
    public void setContentView(View view) {
        content.removeAllViews();
        if (view != null) {
            content.addView(view, -1, -1);
        }
    }

    @Override
    public <T extends View> T findViewById(int id) {
        return content.findViewById(id);
    }

    @Override
    protected void onPageSwitched(int oldPosition, int position) {
        super.onPageSwitched(oldPosition, position);
        if (oldPosition >= 0) {
            ImageViewCompat.setImageTintList(buttonImages[oldPosition], ColorStateList.valueOf(getResources().getColor(R.color.text_primary)));
            buttonTexts[oldPosition].setTextColor(ColorStateList.valueOf(getResources().getColor(R.color.text_primary)));
        }
        ImageViewCompat.setImageTintList(buttonImages[position], ColorStateList.valueOf(getResources().getColor(R.color.primary)));
        buttonTexts[position].setTextColor(ColorStateList.valueOf(getResources().getColor(R.color.primary)));
    }

    @Override
    public void removeAllActionbarButtons() {}
}
