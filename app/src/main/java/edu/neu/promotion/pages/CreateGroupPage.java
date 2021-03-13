package edu.neu.promotion.pages;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.davidsoft.utils.JsonNode;

import java.util.ArrayList;

import edu.neu.promotion.R;
import edu.neu.promotion.SearchSelectItemActivity;
import edu.neu.promotion.ServerInterfaces;
import edu.neu.promotion.ServerInvoker;
import edu.neu.promotion.components.AlertDialog;
import edu.neu.promotion.components.BaseActivity;
import edu.neu.promotion.components.PageManager;
import edu.neu.promotion.enties.GroupNode;
import edu.neu.promotion.enties.RoleNode;
import edu.neu.promotion.enties.ServerResponseNode;

public class CreateGroupPage extends TokenRunNetworkTaskPage {

    private static final int TASK_GET_GROUPS = 1;
    private static final int TASK_APPLY_ROLE = 2;

    private static final int ACTIVITY_REQUEST_SELECT_GROUP = 1;

    private final RoleNode selectedRole;

    private GroupNode[] groups;
    private ArrayList<String> groupDisplays;

    private AlertDialog loadingDialog;
    private DialogInterface.OnCancelListener onLoadingDialogCancelListener;
    private TextView groupEdit;
    private TextView nextButton;

    private GroupNode selectedGroup;

    public CreateGroupPage(PageManager pageManager, Object... args) {
        super(pageManager, args);
        selectedRole = (RoleNode) args[0];
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        setActionbarStyle(BaseActivity.ACTIONBAR_STYLE_NO_TITLE);

        onLoadingDialogCancelListener = dialog -> cancelTask(TASK_APPLY_ROLE);
        runTask(ServerInterfaces.Group.getAllGroup(getToken()), TASK_GET_GROUPS);
    }

    private void loadMainViews() {
        View.OnClickListener onClickListener = v -> {
            if (v == groupEdit) {
                Intent intent = new Intent(getContext(), SearchSelectItemActivity.class);
                intent.putExtra(SearchSelectItemActivity.REQUEST_EXTRA_TITLE, getString(R.string.create_group_title));
                intent.putExtra(SearchSelectItemActivity.REQUEST_EXTRA_ITEMS, groupDisplays);
                startActivityForResult(intent, ACTIVITY_REQUEST_SELECT_GROUP);
            }
            else if (v == nextButton) {
                runTask(ServerInterfaces.Role.applyRole(getToken(), selectedRole.roleId, selectedGroup.groupId), TASK_APPLY_ROLE);
            }
        };

        setContentView(R.layout.page_create_group);
        ((TextView) findViewById(R.id.subtitleTextView)).setText(getString(R.string.create_group_subtitle, selectedRole.roleName));
        groupEdit = findViewById(R.id.groupEdit);
        groupEdit.setOnClickListener(onClickListener);
        nextButton = findViewById(R.id.nextButton);
        nextButton.setOnClickListener(onClickListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            selectedGroup = groups[data.getIntExtra(SearchSelectItemActivity.RESULT_EXTRA_POSITION, 0)];
            groupEdit.setText(selectedGroup.groupName);
            nextButton.setEnabled(true);
        }
    }

    @Override
    protected void onTaskBegin(int requestCode) {
        super.onTaskBegin(requestCode);
        switch (requestCode) {
            case TASK_GET_GROUPS:
                toLoadingState();
                break;
            case TASK_APPLY_ROLE:
                loadingDialog = AlertDialog.Builder.getCenter(getContext()).setLoading().setOnCancelListener(onLoadingDialogCancelListener).show();
                break;
        }
    }

    @Override
    protected void onTaskFinish(int requestCode) {
        super.onTaskFinish(requestCode);
        switch (requestCode) {
            case TASK_GET_GROUPS:
                break;
            case TASK_APPLY_ROLE:
                loadingDialog.cancel();
                break;
        }
    }

    @Override
    protected void onTaskResult(int requestCode, Object result) {
        super.onTaskResult(requestCode, result);
        ServerResponseNode response = ServerInterfaces.analyseCommonContent((ServerInvoker.InvokeResult) result);
        switch (requestCode) {
            case TASK_GET_GROUPS:
                if (response.code == ServerInterfaces.RESULT_CODE_SUCCESS) {
                    groups = JsonNode.toObject(response.object, GroupNode[].class);
                    groupDisplays = new ArrayList<>(groups.length);
                    for (GroupNode node : groups) {
                        groupDisplays.add(node.groupName);
                    }
                    loadMainViews();
                }
                toNormalState();
                break;
            case TASK_APPLY_ROLE:
                if (response.code == ServerInterfaces.RESULT_CODE_SUCCESS) {
                    Toast.makeText(getContext(), R.string.create_role_success, Toast.LENGTH_SHORT).show();
                    notifyParent(0);
                }
                break;
        }

    }

    @Override
    protected void onTaskRetryFailed(int requestCode) {
        super.onTaskRetryFailed(requestCode);
        switch (requestCode) {
            case TASK_GET_GROUPS:
                toErrorState();
                break;
            case TASK_APPLY_ROLE:
                break;
        }
    }

    @Override
    protected void onErrorStateRetry() {
        super.onErrorStateRetry();
        retryTask(TASK_GET_GROUPS);
    }
}
