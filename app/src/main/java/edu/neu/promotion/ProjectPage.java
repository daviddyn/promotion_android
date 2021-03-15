package edu.neu.promotion;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import edu.neu.promotion.components.PageManager;
import edu.neu.promotion.views.LazyLoadListView;
import edu.neu.promotion.views.PageTabBarView;

public class ProjectPage extends TokenRunNetworkTaskPage {

    private LazyLoadListView lazyLoadListView;

    private CheckedTextView allProjectButton;
    private CheckedTextView inProjectButton;
    private CheckedTextView createProjectButton;
    private PageTabBarView pageTabBarView;
    private ViewPager viewPager;

    public ProjectPage(PageManager pageManager, Object... args) {
        super(pageManager, args);
    }

    @Override
    protected void onCreate() {
        super.onCreate();

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        };

        lazyLoadListView = new LazyLoadListView(getContext());
        lazyLoadListView.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return 100;
            }

            @Override
            public Object getItem(int position) {
                return null;
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = new TextView(getContext());
                }
                return convertView;
            }
        });

        setContentView(R.layout.page_project);
        allProjectButton = findViewById(R.id.allProjectButton);
        inProjectButton = findViewById(R.id.inProjectButton);
        createProjectButton = findViewById(R.id.createProjectButton);
        pageTabBarView = findViewById(R.id.pageTabBarView);
        viewPager = findViewById(R.id.viewPager);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                pageTabBarView.setPagePosition(3, position, positionOffset);
            }

            @Override
            public void onPageSelected(int position) {

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
                View v;
                switch (position) {
                    case 0:
                        container.addView(lazyLoadListView);
                        return lazyLoadListView;
                    case 1:
                    case 2:
                        v = new View(getContext());
                        container.addView(v);
                        return v;
                    default:
                        return super.instantiateItem(container, position);
                }
            }

            @Override
            public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
                container.removeView((View) object);
            }
        });

        addActionbarButton(getDrawable(R.drawable.ic_actionbar_search), "");
    }

    @Override
    protected void onActionbarButtonClick(int position, View viewForAnchor) {
        super.onActionbarButtonClick(position, viewForAnchor);
        lazyLoadListView.debug();
    }
}
