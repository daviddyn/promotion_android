package edu.neu.promotion;

import android.graphics.drawable.AnimationDrawable;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.davidsoft.utils.JsonNode;

import java.util.ArrayList;
import java.util.Arrays;

import edu.neu.promotion.components.PageManager;
import edu.neu.promotion.enties.AdminNode;
import edu.neu.promotion.enties.AdminRoleGroupNode;
import edu.neu.promotion.enties.ServerResponseListNode;
import edu.neu.promotion.enties.ServerResponseNode;
import edu.neu.promotion.views.EntityAdapter;
import edu.neu.promotion.views.LazyLoadListView;
import edu.neu.promotion.views.PageTabBarView;

public class MemberPage extends TokenRunNetworkTaskPage {

    private static final int ITEMS_PER_LOAD = 50;

    private static final int TASK_GET_FORMAL_MEMBERS = 1;
    private static final int TASK_GET_EXAMINE_MEMBERS = 2;
    private static final int TASK_APPEND_FORMAL_MEMBERS = 3;
    private static final int TASK_APPEND_EXAMINE_MEMBERS = 4;

    private final AdminNode adminInfo;

    private ArrayList<AdminRoleGroupNode> formalMembers;
    private ArrayList<AdminRoleGroupNode> examineMembers;

    private LazyLoadListView formalListView;
    private EntityAdapter<AdminRoleGroupNode> formalListAdapter;
    private LazyLoadListView examineListView;
    private EntityAdapter<AdminRoleGroupNode> examineListAdapter;
    private AnimationDrawable[] loadingAnimations;
    private ImageView[] loadingViews;
    private TextView emptyTips[];
    private FrameLayout[] pages;
    private int[] mainViewLoadState;

    private CheckedTextView formalButton;
    private CheckedTextView examiningButton;
    private PageTabBarView pageTabBarView;
    private ViewPager viewPager;

    public MemberPage(PageManager pageManager, Object... args) {
        super(pageManager, args);
        adminInfo = JsonNode.toObject(StorageManager.getJson(getContext(), StorageManager.USER_INFO), AdminNode.class);
    }

    @Override
    protected void onCreate() {
        super.onCreate();

        formalMembers = new ArrayList<>();
        examineMembers = new ArrayList<>();

        formalListView = new LazyLoadListView(getContext());
        formalListView.setOnLoadListener(() -> runTask(
                ServerInterfaces.Role.getAdminRoleGroupBySearch(getToken(), "", "", "admin_check_state_5", formalMembers.size() / ITEMS_PER_LOAD, ITEMS_PER_LOAD),
                Integer.MAX_VALUE,
                TASK_APPEND_FORMAL_MEMBERS
        ));
        formalListAdapter = new EntityAdapter<>(formalMembers, AdminRoleGroupAdminFiller.class, adminInfo.adminId);
        formalListView.setAdapter(formalListAdapter);
        formalListView.setVisibility(View.GONE);
        examineListView = new LazyLoadListView(getContext());
        examineListView.setOnLoadListener(() -> runTask(
                ServerInterfaces.Role.getNoCheckAdminRoleGroupBySearch(getToken(), formalMembers.size() / ITEMS_PER_LOAD, ITEMS_PER_LOAD),
                Integer.MAX_VALUE,
                TASK_APPEND_EXAMINE_MEMBERS
        ));
        examineListAdapter = new EntityAdapter<>(examineMembers, AdminRoleGroupExamineFiller.class);
        examineListView.setAdapter(examineListAdapter);
        examineListView.setVisibility(View.GONE);

        loadingAnimations = new AnimationDrawable[2];
        loadingViews = new ImageView[2];
        emptyTips = new TextView[2];
        pages = new FrameLayout[2];
        for (int i = 0; i < loadingAnimations.length; i++) {
            loadingAnimations[i] = (AnimationDrawable) getDrawable(R.drawable.bg_loading).mutate();
            loadingViews[i] = new ImageView(getContext());
            loadingViews[i].setScaleType(ImageView.ScaleType.CENTER);
            loadingViews[i].setImageDrawable(loadingAnimations[i]);
            emptyTips[i] = new TextView(getContext());
            emptyTips[i].setTextColor(getColor(R.color.text_tertiary));
            emptyTips[i].setTextSize(TypedValue.COMPLEX_UNIT_PX, getDimension(R.dimen.list_tip_text_size));
            emptyTips[i].setPadding(0, (int) getDimension(R.dimen.list_tip_padding_top), 0, 0);
            emptyTips[i].setText(R.string.list_empty);
            emptyTips[i].setVisibility(View.GONE);
            pages[i] = new FrameLayout(getContext());
            pages[i].addView(loadingViews[i], -1, -1);
            pages[i].addView(emptyTips[i], -1, -1);
        }
        pages[0].addView(formalListView);
        pages[1].addView(examineListView);
        mainViewLoadState = new int[2];

        View.OnClickListener onClickListener = v -> {
            if (v == formalButton) {
                viewPager.setCurrentItem(0);
            }
            else if (v == examiningButton) {
                viewPager.setCurrentItem(1);
            }
        };

        setContentView(R.layout.page_member);
        formalButton = findViewById(R.id.formalButton);
        formalButton.setOnClickListener(onClickListener);
        examiningButton = findViewById(R.id.examiningButton);
        examiningButton.setOnClickListener(onClickListener);
        pageTabBarView = findViewById(R.id.pageTabBarView);
        viewPager = findViewById(R.id.viewPager);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                pageTabBarView.setPagePosition(2, position, positionOffset);
            }

            @Override
            public void onPageSelected(int position) {
                MemberPage.this.onPageSelected(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        viewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return pages.length;
            }

            @Override
            public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
                return view == object;
            }

            @NonNull
            @Override
            public Object instantiateItem(@NonNull ViewGroup container, int position) {
                container.addView(pages[position]);
                return pages[position];
            }

            @Override
            public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
                container.removeView((View) object);
            }
        });

        onPageSelected(0);

        setTitle(R.string.member_title);
        addActionbarButton(getDrawable(R.drawable.ic_actionbar_search), R.string.search);
    }

    private void onPageSelected(int position) {
        switch (position) {
            case 0:
                formalButton.setEnabled(false);
                formalButton.setChecked(true);
                examiningButton.setEnabled(true);
                examiningButton.setChecked(false);
                if (mainViewLoadState[0] == 0) {
                    mainViewLoadState[0] = 1;
                    loadingAnimations[0].start();
                    runTask(
                            ServerInterfaces.Role.getAdminRoleGroupBySearch(getToken(), "", "", "admin_check_state_5", formalMembers.size() / ITEMS_PER_LOAD, ITEMS_PER_LOAD),
                            Integer.MAX_VALUE,
                            TASK_GET_FORMAL_MEMBERS
                    );
                }
                break;
            case 1:
                formalButton.setEnabled(true);
                formalButton.setChecked(false);
                examiningButton.setEnabled(false);
                examiningButton.setChecked(true);
                if (mainViewLoadState[1] == 0) {
                    mainViewLoadState[1] = 1;
                    loadingAnimations[1].start();
                    runTask(
                            ServerInterfaces.Role.getNoCheckAdminRoleGroupBySearch(getToken(), formalMembers.size() / ITEMS_PER_LOAD, ITEMS_PER_LOAD),
                            Integer.MAX_VALUE,
                            TASK_GET_EXAMINE_MEMBERS
                    );
                }
                break;
        }
    }

    @Override
    protected void onTaskResult(int requestCode, Object result) {
        super.onTaskResult(requestCode, result);
        ServerResponseNode response = ServerInterfaces.analyseCommonContent((ServerInvoker.InvokeResult) result);
        if (response.code != 200) {
            return;
        }
        ServerResponseListNode responseListNode = JsonNode.toObject(response.object, ServerResponseListNode.class);
        switch (requestCode) {
            case TASK_GET_FORMAL_MEMBERS:
                formalMembers.addAll(Arrays.asList(JsonNode.toObject(responseListNode.items, AdminRoleGroupNode[].class)));
                formalListView.notifyLoadResult(responseListNode.isMore != 0);
                loadingAnimations[0].stop();
                loadingViews[0].setVisibility(View.GONE);
                if (formalMembers.isEmpty()) {
                    emptyTips[0].setVisibility(View.VISIBLE);
                }
                else {
                    formalListView.setVisibility(View.VISIBLE);
                    formalListAdapter.notifyDataSetChanged();
                }
                break;
            case TASK_APPEND_FORMAL_MEMBERS:
                formalMembers.addAll(Arrays.asList(JsonNode.toObject(responseListNode.items, AdminRoleGroupNode[].class)));
                formalListView.notifyLoadResult(responseListNode.isMore != 0);
                formalListAdapter.notifyDataSetChanged();
                break;

            case TASK_GET_EXAMINE_MEMBERS:
                examineMembers.addAll(Arrays.asList(JsonNode.toObject(responseListNode.items, AdminRoleGroupNode[].class)));
                examineListView.notifyLoadResult(responseListNode.isMore != 0);
                loadingAnimations[1].stop();
                loadingViews[1].setVisibility(View.GONE);
                if (examineMembers.isEmpty()) {
                    emptyTips[1].setVisibility(View.VISIBLE);
                }
                else {
                    examineListView.setVisibility(View.VISIBLE);
                    examineListAdapter.notifyDataSetChanged();
                }
                break;
            case TASK_APPEND_EXAMINE_MEMBERS:
                examineMembers.addAll(Arrays.asList(JsonNode.toObject(responseListNode.items, AdminRoleGroupNode[].class)));
                examineListView.notifyLoadResult(responseListNode.isMore != 0);
                examineListAdapter.notifyDataSetChanged();
                break;
        }
    }
}
