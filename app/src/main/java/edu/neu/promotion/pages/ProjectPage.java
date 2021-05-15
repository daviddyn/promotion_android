package edu.neu.promotion.pages;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.util.TypedValue;
import android.view.Gravity;
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

import edu.neu.promotion.StorageManager;
import edu.neu.promotion.activities.EditProjectActivity;
import edu.neu.promotion.enties.AdminNode;
import edu.neu.promotion.enties.AdminRoleNode;
import edu.neu.promotion.fillers.ProjectEntityFiller;
import edu.neu.promotion.R;
import edu.neu.promotion.ServerInterfaces;
import edu.neu.promotion.ServerInvoker;
import edu.neu.promotion.components.PageManager;
import edu.neu.promotion.enties.ProjectNode;
import edu.neu.promotion.enties.ServerResponseListNode;
import edu.neu.promotion.enties.ServerResponseNode;
import edu.neu.promotion.views.EntityAdapter;
import edu.neu.promotion.views.LazyLoadListView;
import edu.neu.promotion.views.PageTabBarView;

public class ProjectPage extends TokenRunNetworkTaskPage {

    private static final int ITEMS_PER_LOAD = 50;

    private static final int TASK_GET_ALL_PROJECTS = 1;
    private static final int TASK_GET_IN_PROJECTS = 2;
    private static final int TASK_GET_CREATE_PROJECTS = 3;
    private static final int TASK_APPEND_ALL_PROJECTS = 4;
    private static final int TASK_APPEND_IN_PROJECTS = 5;
    private static final int TASK_APPEND_CREATE_PROJECTS = 6;

    private static final int ACTIVITY_REQUEST_CREATE_PROJECT = 1;

    private final ProjectEntityFiller.Tag projectEntityFillerTag;

    private ArrayList<ProjectNode> allProjects;
    private ArrayList<ProjectNode> inProjects;
    private ArrayList<ProjectNode> createProjects;

    private LazyLoadListView allListView;
    private EntityAdapter<ProjectNode> allListAdapter;
    private LazyLoadListView inListView;
    private EntityAdapter<ProjectNode> inListAdapter;
    private LazyLoadListView createListView;
    private EntityAdapter<ProjectNode> createListAdapter;
    private AnimationDrawable[] loadingAnimations;
    private ImageView[] loadingViews;
    private TextView[] emptyTips;
    private FrameLayout[] pages;
    private int[] mainViewLoadState;

    private CheckedTextView allProjectButton;
    private CheckedTextView inProjectButton;
    private CheckedTextView createProjectButton;
    private PageTabBarView pageTabBarView;
    private ViewPager viewPager;

    private int lastSelectedPage;

    public ProjectPage(PageManager pageManager, Object... args) {
        super(pageManager, args);
        projectEntityFillerTag = new ProjectEntityFiller.Tag();
        projectEntityFillerTag.currentIsGroupAdmin = "role_professor".equals(JsonNode.toObject(StorageManager.getJson(getContext(), StorageManager.ROLE_INFO), AdminRoleNode.class).roleId);
        projectEntityFillerTag.currentAdminId = JsonNode.toObject(StorageManager.getJson(getContext(), StorageManager.USER_INFO), AdminNode.class).adminId;
    }

    @Override
    protected void onCreate() {
        super.onCreate();

        allProjects = new ArrayList<>();
        inProjects = new ArrayList<>();
        createProjects = new ArrayList<>();

        allListView = new LazyLoadListView(getContext());
        allListView.setDividerHeight(1);
        allListView.setOnLoadListener(() -> runTask(
                ServerInterfaces.Project.getProjectByPreset(getToken(), false, false, ServerInterfaces.Project.STATE_FILTER_NOT_DRAFT, allProjects.size() / ITEMS_PER_LOAD, ITEMS_PER_LOAD),
                Integer.MAX_VALUE,
                TASK_APPEND_ALL_PROJECTS
        ));
        allListAdapter = new EntityAdapter<>(allProjects, ProjectEntityFiller.class, projectEntityFillerTag);
        allListView.setAdapter(allListAdapter);
        allListView.setVisibility(View.GONE);

        inListView = new LazyLoadListView(getContext());
        inListView.setDividerHeight(1);
        inListView.setOnLoadListener(() -> runTask(
                ServerInterfaces.Project.getProjectByPreset(getToken(), false, true, ServerInterfaces.Project.STATE_FILTER_NOT_DRAFT, inProjects.size() / ITEMS_PER_LOAD, ITEMS_PER_LOAD),
                Integer.MAX_VALUE,
                TASK_APPEND_IN_PROJECTS
        ));
        inListAdapter = new EntityAdapter<>(inProjects, ProjectEntityFiller.class, projectEntityFillerTag);
        inListView.setAdapter(inListAdapter);
        inListView.setVisibility(View.GONE);

        createListView = new LazyLoadListView(getContext());
        createListView.setDividerHeight(1);
        createListView.setOnLoadListener(() -> runTask(
                ServerInterfaces.Project.getProjectByPreset(getToken(), true, false, ServerInterfaces.Project.STATE_FILTER_ALL, createProjects.size() / ITEMS_PER_LOAD, ITEMS_PER_LOAD),
                Integer.MAX_VALUE,
                TASK_APPEND_CREATE_PROJECTS
        ));
        createListAdapter = new EntityAdapter<>(createProjects, ProjectEntityFiller.class, projectEntityFillerTag);
        createListView.setAdapter(createListAdapter);
        createListView.setVisibility(View.GONE);

        loadingAnimations = new AnimationDrawable[3];
        loadingViews = new ImageView[3];
        emptyTips = new TextView[3];
        pages = new FrameLayout[3];
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
            emptyTips[i].setGravity(Gravity.CENTER_HORIZONTAL);
            pages[i] = new FrameLayout(getContext());
            pages[i].addView(loadingViews[i], -1, -1);
            pages[i].addView(emptyTips[i], -1, -1);
        }
        pages[0].addView(allListView);
        pages[1].addView(inListView);
        pages[2].addView(createListView);
        mainViewLoadState = new int[3];

        View.OnClickListener onClickListener = v -> {
            if (v == allProjectButton) {
                viewPager.setCurrentItem(0);
            }
            else if (v == inProjectButton) {
                viewPager.setCurrentItem(1);
            }
            else if (v == createProjectButton) {
                viewPager.setCurrentItem(2);
            }
        };

        setContentView(R.layout.page_project);
        allProjectButton = findViewById(R.id.allProjectButton);
        allProjectButton.setOnClickListener(onClickListener);
        inProjectButton = findViewById(R.id.inProjectButton);
        inProjectButton.setOnClickListener(onClickListener);
        createProjectButton = findViewById(R.id.createProjectButton);
        createProjectButton.setOnClickListener(onClickListener);

        pageTabBarView = findViewById(R.id.pageTabBarView);
        viewPager = findViewById(R.id.viewPager);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                pageTabBarView.setPagePosition(3, position, positionOffset);
            }

            @Override
            public void onPageSelected(int position) {
                ProjectPage.this.onPageSelected(position);
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

        lastSelectedPage = -1;
        onPageSelected(0);

        setTitle(R.string.project_title);
        addActionbarButton(getDrawable(R.drawable.ic_actionbar_search), R.string.search);
        addActionbarButton(getDrawable(R.drawable.ic_actionbar_add), R.string.project_create);
    }

    private void onPageSelected(int position) {
        switch (lastSelectedPage) {
            case 0:
                allProjectButton.setEnabled(true);
                allProjectButton.setChecked(false);
                break;
            case 1:
                inProjectButton.setEnabled(true);
                inProjectButton.setChecked(false);
                break;
            case 2:
                createProjectButton.setEnabled(true);
                createProjectButton.setChecked(false);
                break;
        }
        lastSelectedPage = position;
        switch (position) {
            case 0:
                allProjectButton.setEnabled(false);
                allProjectButton.setChecked(true);
                break;
            case 1:
                inProjectButton.setEnabled(false);
                inProjectButton.setChecked(true);
                break;
            case 2:
                createProjectButton.setEnabled(false);
                createProjectButton.setChecked(true);
                break;
        }
        if (mainViewLoadState[position] == 0) {
            mainViewLoadState[position] = 1;
            loadingAnimations[position].start();
            switch (position) {
                case 0:
                    runTask(
                            ServerInterfaces.Project.getProjectByPreset(getToken(), false, false, ServerInterfaces.Project.STATE_FILTER_NOT_DRAFT, allProjects.size() / ITEMS_PER_LOAD, ITEMS_PER_LOAD),
                            Integer.MAX_VALUE,
                            TASK_GET_ALL_PROJECTS
                    );
                    break;
                case 1:
                    runTask(
                            ServerInterfaces.Project.getProjectByPreset(getToken(), false, true, ServerInterfaces.Project.STATE_FILTER_NOT_DRAFT, inProjects.size() / ITEMS_PER_LOAD, ITEMS_PER_LOAD),
                            Integer.MAX_VALUE,
                            TASK_GET_IN_PROJECTS
                    );
                    break;
                case 2:
                    runTask(
                            ServerInterfaces.Project.getProjectByPreset(getToken(), true, false, ServerInterfaces.Project.STATE_FILTER_ALL, createProjects.size() / ITEMS_PER_LOAD, ITEMS_PER_LOAD),
                            Integer.MAX_VALUE,
                            TASK_GET_CREATE_PROJECTS
                    );
                    break;
            }
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
            case TASK_GET_ALL_PROJECTS:
                allProjects.addAll(Arrays.asList(JsonNode.toObject(responseListNode.items, ProjectNode[].class)));
                allListView.notifyLoadResult(responseListNode.isMore != 0);
                loadingAnimations[0].stop();
                loadingViews[0].setVisibility(View.GONE);
                if (allProjects.isEmpty()) {
                    emptyTips[0].setVisibility(View.VISIBLE);
                }
                else {
                    allListView.setVisibility(View.VISIBLE);
                    projectEntityFillerTag.currentTimeMills = System.currentTimeMillis();
                    allListAdapter.notifyDataSetChanged();
                }
                break;
            case TASK_GET_IN_PROJECTS:
                inProjects.addAll(Arrays.asList(JsonNode.toObject(responseListNode.items, ProjectNode[].class)));
                inListView.notifyLoadResult(responseListNode.isMore != 0);
                loadingAnimations[1].stop();
                loadingViews[1].setVisibility(View.GONE);
                if (inProjects.isEmpty()) {
                    emptyTips[1].setVisibility(View.VISIBLE);
                }
                else {
                    inListView.setVisibility(View.VISIBLE);
                    projectEntityFillerTag.currentTimeMills = System.currentTimeMillis();
                    inListAdapter.notifyDataSetChanged();
                }
                break;
            case TASK_GET_CREATE_PROJECTS:
                createProjects.addAll(Arrays.asList(JsonNode.toObject(responseListNode.items, ProjectNode[].class)));
                createListView.notifyLoadResult(responseListNode.isMore != 0);
                loadingAnimations[2].stop();
                loadingViews[2].setVisibility(View.GONE);
                if (createProjects.isEmpty()) {
                    emptyTips[2].setVisibility(View.VISIBLE);
                }
                else {
                    createListView.setVisibility(View.VISIBLE);
                    projectEntityFillerTag.currentTimeMills = System.currentTimeMillis();
                    createListAdapter.notifyDataSetChanged();
                }
                break;
                
            case TASK_APPEND_ALL_PROJECTS:
                allProjects.addAll(Arrays.asList(JsonNode.toObject(responseListNode.items, ProjectNode[].class)));
                allListView.notifyLoadResult(responseListNode.isMore != 0);
                allListAdapter.notifyDataSetChanged();
                break;
            case TASK_APPEND_IN_PROJECTS:
                inProjects.addAll(Arrays.asList(JsonNode.toObject(responseListNode.items, ProjectNode[].class)));
                inListView.notifyLoadResult(responseListNode.isMore != 0);
                inListAdapter.notifyDataSetChanged();
                break;
            case TASK_APPEND_CREATE_PROJECTS:
                createProjects.addAll(Arrays.asList(JsonNode.toObject(responseListNode.items, ProjectNode[].class)));
                createListView.notifyLoadResult(responseListNode.isMore != 0);
                createListAdapter.notifyDataSetChanged();
                break;
        }
    }

    @Override
    protected void onActionbarButtonClick(int position, View viewForAnchor) {
        switch (position) {
            case 0:
                //TODO: 搜索
                break;
            case 1:
                startActivityForResult(new Intent(getContext(), EditProjectActivity.class), ACTIVITY_REQUEST_CREATE_PROJECT);
                break;
        }
    }
}
