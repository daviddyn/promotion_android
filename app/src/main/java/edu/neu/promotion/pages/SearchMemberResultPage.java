package edu.neu.promotion.pages;

import android.content.Intent;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.davidsoft.utils.JsonNode;

import java.util.ArrayList;
import java.util.Arrays;

import edu.neu.promotion.R;
import edu.neu.promotion.ServerInterfaces;
import edu.neu.promotion.ServerInvoker;
import edu.neu.promotion.StorageManager;
import edu.neu.promotion.activities.ExamineUserInfoActivity;
import edu.neu.promotion.activities.MemberInfoActivity;
import edu.neu.promotion.components.PageManager;
import edu.neu.promotion.enties.AdminNode;
import edu.neu.promotion.enties.AdminRoleGroupNode;
import edu.neu.promotion.enties.ServerResponseListNode;
import edu.neu.promotion.enties.ServerResponseNode;
import edu.neu.promotion.fillers.AdminRoleGroupEntityFiller;
import edu.neu.promotion.views.EntityAdapter;
import edu.neu.promotion.views.LazyLoadListView;

public class SearchMemberResultPage extends TokenRunNetworkTaskPage {

    public static final int RESULT_ACCEPT = 1;
    public static final int RESULT_DENIED = 2;

    private static final int ITEMS_PER_LOAD = 50;

    private static final int TASK_GET_SEARCH_RESULT = 1;
    private static final int TASK_APPEND_SEARCH_RESULT = 2;

    private static final int ACTIVITY_REQUEST_VIEW_EXAMINE = 1;

    private final AdminNode searchAdminInfo;
    private final String searchCheckState;
    private final AdminNode myAdminInfo;

    private ArrayList<AdminRoleGroupNode> resultMembers;

    private LazyLoadListView listView;
    private EntityAdapter<AdminRoleGroupNode> listAdapter;
    private TextView emptyTip;

    private int operatingPosition;

    public SearchMemberResultPage(PageManager pageManager, Object... args) {
        super(pageManager, args);
        searchAdminInfo = (AdminNode) args[0];
        searchCheckState = (String) args[1];
        myAdminInfo = JsonNode.toObject(StorageManager.getJson(getContext(), StorageManager.USER_INFO), AdminNode.class);
    }

    @Override
    protected void onCreate() {
        super.onCreate();

        setTitle(R.string.member_search_result_title);

        resultMembers = new ArrayList<>();

        listView = new LazyLoadListView(getContext());
        listView.setDividerHeight(1);
        listView.setOnLoadListener(() -> runTask(
                ServerInterfaces.Role.getAdminRoleGroupBySearch(getToken(), searchAdminInfo, searchCheckState, resultMembers.size() / ITEMS_PER_LOAD, ITEMS_PER_LOAD),
                Integer.MAX_VALUE,
                TASK_APPEND_SEARCH_RESULT
        ));
        listView.setOnItemClickListener((parent, view, position, id) -> {
            AdminRoleGroupNode node = resultMembers.get(position);
            if (myAdminInfo.adminId.equals(node.adminId)) {
                return;
            }
            if ("admin_check_state_5".equals(node.checkState)) {
                Intent intent = new Intent(getContext(), MemberInfoActivity.class);
                intent.putExtra(MemberInfoActivity.REQUEST_EXTRA_ADMIN_ROLE_GROUP_INFO, node);
                startActivity(intent);
            }
            else {
                Intent intent = new Intent(getContext(), ExamineUserInfoActivity.class);
                intent.putExtra(ExamineUserInfoActivity.REQUEST_EXTRA_ADMIN_ROLE_GROUP_INFO, node);
                operatingPosition = position;
                startActivityForResult(intent, ACTIVITY_REQUEST_VIEW_EXAMINE);
            }
        });
        listAdapter = new EntityAdapter<>(resultMembers, AdminRoleGroupEntityFiller.class, myAdminInfo.adminId);
        listView.setAdapter(listAdapter);
        emptyTip = new TextView(getContext());
        emptyTip.setTextColor(getColor(R.color.text_tertiary));
        emptyTip.setTextSize(TypedValue.COMPLEX_UNIT_PX, getDimension(R.dimen.list_tip_text_size));
        emptyTip.setPadding(0, (int) getDimension(R.dimen.list_tip_padding_top), 0, 0);
        emptyTip.setText(R.string.list_empty);
        emptyTip.setGravity(Gravity.CENTER_HORIZONTAL);

        runTask(ServerInterfaces.Role.getAdminRoleGroupBySearch(getToken(), searchAdminInfo, searchCheckState, resultMembers.size() / ITEMS_PER_LOAD, ITEMS_PER_LOAD), TASK_GET_SEARCH_RESULT);
    }

    @Override
    protected void onTaskBegin(int requestCode) {
        super.onTaskBegin(requestCode);
        switch (requestCode) {
            case TASK_GET_SEARCH_RESULT:
                toLoadingState();
                break;
        }
    }

    @Override
    protected void onTaskRetryFailed(int requestCode) {
        super.onTaskRetryFailed(requestCode);
        switch (requestCode) {
            case TASK_GET_SEARCH_RESULT:
                toErrorState();
                break;
        }
    }

    @Override
    protected void onErrorStateRetry() {
        super.onErrorStateRetry();
        retryTask(TASK_GET_SEARCH_RESULT);
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
            case TASK_GET_SEARCH_RESULT:
                resultMembers.addAll(Arrays.asList(JsonNode.toObject(responseListNode.items, AdminRoleGroupNode[].class)));
                listView.notifyLoadResult(responseListNode.isMore != 0);
                if (resultMembers.isEmpty()) {
                    setContentView(emptyTip);
                }
                else {
                    setContentView(listView);
                    listAdapter.notifyDataSetChanged();
                }
                toNormalState();
                break;
            case TASK_APPEND_SEARCH_RESULT:
                resultMembers.addAll(Arrays.asList(JsonNode.toObject(responseListNode.items, AdminRoleGroupNode[].class)));
                listView.notifyLoadResult(responseListNode.isMore != 0);
                listAdapter.notifyDataSetChanged();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ACTIVITY_REQUEST_VIEW_EXAMINE:
                switch (resultCode) {
                    case ExamineUserInfoActivity.RESULT_ACCEPT:
                        AdminRoleGroupNode node = resultMembers.get(operatingPosition);
                        node.checkStateObj.dictionaryName = data.getStringExtra(ExamineUserInfoActivity.RESULT_EXTRA_NEW_CHECK_STATE);
                        node.canCheck = false;
                        listAdapter.notifyDataSetChanged();
                        notifyParent(RESULT_ACCEPT, node.adminId, node.checkStateObj.dictionaryName);
                        break;
                    case ExamineUserInfoActivity.RESULT_DENIED:
                        notifyParent(RESULT_DENIED, resultMembers.remove(operatingPosition).adminId);
                        listAdapter.notifyDataSetChanged();
                        break;
                }
        }
    }
}
