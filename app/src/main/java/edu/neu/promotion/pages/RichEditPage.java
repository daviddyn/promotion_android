package edu.neu.promotion.pages;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import edu.neu.promotion.R;
import edu.neu.promotion.components.Page;
import edu.neu.promotion.components.PageManager;
import edu.neu.promotion.views.PageTabBarView;
import jp.wasabeef.richeditor.RichEditor;

public class RichEditPage extends Page {

    public static final int NOTIFY_DISABLE_SOFT_KEYBOARD = 1;
    public static final int NOTIFY_ENABLE_SOFT_KEYBOARD = 2;

    //禁用软键盘 getWindow().addFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
    //启用软键盘 getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);

    private RelativeLayout rootView;
    private RichEditor richEdit;
    private View toolContainer;
    private RelativeLayout.LayoutParams toolContainerLayoutParams;
    private TextView textButton;
    private TextView formatButton;
    private TextView insertButton;
    private ImageView closeButton;
    private PageTabBarView pageTabBarView;
    private ViewPager viewPager;

    private static final int TOOL_BAR_STATE_FOLDED = 0;
    private static final int TOOL_BAR_STATE_FOLDING = 1;
    private static final int TOOL_BAR_STATE_EXPANDING = 2;
    private static final int TOOL_BAR_STATE_EXPANDED = 3;

    private int toolBarState;
    private int foldedMarginValue;
    private ValueAnimator valueAnimator;

    public RichEditPage(PageManager pageManager, Object... args) {
        super(pageManager, args);
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        View.OnClickListener onClickListener = v -> {
            if (v == textButton) {
                viewPager.setCurrentItem(0, toolBarState != TOOL_BAR_STATE_FOLDED);
                expandToolBar();
            }
            else if (v == formatButton) {
                viewPager.setCurrentItem(1, toolBarState != TOOL_BAR_STATE_FOLDED);
                expandToolBar();
            }
            else if (v == insertButton) {
                viewPager.setCurrentItem(2, toolBarState != TOOL_BAR_STATE_FOLDED);
                expandToolBar();
            }
            else if (v == closeButton) {
                switch (toolBarState) {
                    case TOOL_BAR_STATE_FOLDED:
                        expandToolBar();
                        break;
                    case TOOL_BAR_STATE_EXPANDED:
                        foldToolBar();
                        break;
                }
            }
        };

        setContentView(R.layout.page_rich_edit);
        rootView = findViewById(R.id.rootView);
        richEdit = findViewById(R.id.richEdit);
        int dip4 = (int) applyDimensions(TypedValue.COMPLEX_UNIT_DIP, 4);
        richEdit.setPadding(dip4, dip4, dip4, dip4);
        toolContainer = findViewById(R.id.toolContainer);
        toolContainerLayoutParams = (RelativeLayout.LayoutParams) toolContainer.getLayoutParams();
        textButton = findViewById(R.id.textButton);
        textButton.setOnClickListener(onClickListener);
        formatButton = findViewById(R.id.formatButton);
        formatButton.setOnClickListener(onClickListener);
        insertButton = findViewById(R.id.insertButton);
        insertButton.setOnClickListener(onClickListener);
        closeButton = findViewById(R.id.closeButton);
        closeButton.setOnClickListener(onClickListener);
        pageTabBarView = findViewById(R.id.pageTabBarView);
        viewPager = findViewById(R.id.viewPager);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                pageTabBarView.setPagePosition(3, position, positionOffset);
            }

            @Override
            public void onPageSelected(int position) {
                onToolPageSelected(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        viewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return 3;
            }

            @Override
            public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
                return view == object;
            }

            @NonNull
            @Override
            public Object instantiateItem(@NonNull ViewGroup container, int position) {
                switch (position) {
                    case 0:
                        return ((ViewGroup) getLayoutInflater().inflate(R.layout.frag_text, container)).getChildAt(container.getChildCount() - 1);
                    case 1:
                        return ((ViewGroup) getLayoutInflater().inflate(R.layout.frag_format, container)).getChildAt(container.getChildCount() - 1);
                    case 2:
                        return ((ViewGroup) getLayoutInflater().inflate(R.layout.frag_insert, container)).getChildAt(container.getChildCount() - 1);
                    default:
                        return super.instantiateItem(container, position);
                }
            }

            @Override
            public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
                container.removeView(((View) object));
            }
        });
        foldedMarginValue = toolContainerLayoutParams.bottomMargin;

        valueAnimator = new ValueAnimator();
        valueAnimator.setDuration(getResource().getInteger(android.R.integer.config_shortAnimTime));
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.addUpdateListener(animation -> {
            int top = ((int) animation.getAnimatedValue());
            toolContainer.layout(0, top, toolContainer.getMeasuredWidth(), top + toolContainer.getMeasuredHeight());
        });
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                switch (toolBarState) {
                    case TOOL_BAR_STATE_EXPANDING:
                        toolBarState = TOOL_BAR_STATE_EXPANDED;
                        toolContainerLayoutParams.setMargins(0, 0, 0, 0);
                        toolContainer.requestLayout();
                        break;
                    case TOOL_BAR_STATE_FOLDING:
                        toolBarState = TOOL_BAR_STATE_FOLDED;
                        toolContainerLayoutParams.setMargins(0, 0, 0, foldedMarginValue);
                        toolContainer.requestLayout();
                        break;
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    private void onToolPageSelected(int position) {
        textButton.setEnabled(position != 0);
        textButton.setTextColor(getColor(position == 0 ? R.color.primary : R.color.text_primary));
        formatButton.setEnabled(position != 1);
        formatButton.setTextColor(getColor(position == 1 ? R.color.primary : R.color.text_primary));
        insertButton.setEnabled(position != 2);
        insertButton.setTextColor(getColor(position == 2 ? R.color.primary : R.color.text_primary));
    }

    private void expandToolBar() {
        if (toolBarState != TOOL_BAR_STATE_FOLDED) {
            return;
        }
        toolBarState = TOOL_BAR_STATE_EXPANDING;
        //收起软键盘
        hideSoftKeyboard();
        //禁用软键盘
        notifyParent(NOTIFY_DISABLE_SOFT_KEYBOARD);
        //文字高亮
        onToolPageSelected(viewPager.getCurrentItem());
        //开启动画
        valueAnimator.setIntValues(rootView.getMeasuredHeight() - closeButton.getMeasuredHeight() - 1, rootView.getMeasuredHeight() - toolContainer.getMeasuredHeight());
        valueAnimator.start();
    }

    private void foldToolBar() {
        if (toolBarState != TOOL_BAR_STATE_EXPANDED) {
            return;
        }
        toolBarState = TOOL_BAR_STATE_FOLDING;
        //启用软键盘
        notifyParent(NOTIFY_ENABLE_SOFT_KEYBOARD);
        //没有文字高亮
        onToolPageSelected(-1);
        //收起动画
        valueAnimator.setIntValues(rootView.getMeasuredHeight() - toolContainer.getMeasuredHeight(), rootView.getMeasuredHeight() - closeButton.getMeasuredHeight() - 1);
        valueAnimator.start();
    }
}
