package edu.neu.promotion.pages;

import android.content.DialogInterface;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.davidsoft.utils.JsonNode;

import java.util.Arrays;

import edu.neu.promotion.R;
import edu.neu.promotion.ServerInterfaces;
import edu.neu.promotion.ServerInvoker;
import edu.neu.promotion.components.AlertDialog;
import edu.neu.promotion.components.PageManager;
import edu.neu.promotion.enties.AdminRoleNode;
import edu.neu.promotion.enties.ServerResponseNode;
import edu.neu.promotion.enties.TimeListNode;
import edu.neu.promotion.views.ProgressTimelineView;

public class ViewRoleExamineStatePage extends TokenRunNetworkTaskPage {

    private static final int TASK_GET_TIME_LINE = 1;
    private static final int TASK_CANCEL_APPLY = 2;

    private final AdminRoleNode roleInfo;

    private TimeListNode[] timeList;

    private AlertDialog loadingDialog;
    private DialogInterface.OnCancelListener onLoadingDialogCancelListener;
    private DialogInterface.OnClickListener onCancelConfirmListener;

    public ViewRoleExamineStatePage(PageManager pageManager, Object... args) {
        super(pageManager, args);
        roleInfo = (AdminRoleNode) args[0];
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        setTitle(R.string.view_role_examine_state_title);

        onLoadingDialogCancelListener = dialog -> cancelTask(TASK_CANCEL_APPLY);
        onCancelConfirmListener = (dialog, which) -> {
            dialog.dismiss();
            if (which == DialogInterface.BUTTON_POSITIVE) {
                runTask(ServerInterfaces.Role.cancelRole(getToken(), roleInfo.adminRoleGroupId), TASK_CANCEL_APPLY);
            }
        };

        runTask(ServerInterfaces.Role.getAdminRoleTimeList(getToken(), roleInfo.adminRoleGroupId), TASK_GET_TIME_LINE);
    }

    private void loadMainViews() {
        setContentView(R.layout.page_view_role_examine_state);
        TimeListNode primaryNode = timeList[timeList.length - 1];
        if (primaryNode.time != null && !primaryNode.time.isEmpty()) {
            ((TextView) findViewById(R.id.examineStateView)).setText(getString(R.string.view_role_examine_state_at, primaryNode.time));
        }
        ImageView roleIconView = findViewById(R.id.roleIconView);
        TextView groupNameText = findViewById(R.id.groupNameText);
        TextView roleNameText = findViewById(R.id.roleNameText);
        switch (roleInfo.roleId) {
            case "role_monitor":
                roleIconView.setImageDrawable(getDrawable(R.drawable.ic_role_admin));
                break;
            case "role_professor":
                roleIconView.setImageDrawable(getDrawable(R.drawable.ic_role_professor));
                break;
            default:
                roleIconView.setImageDrawable(getDrawable(R.drawable.ic_role_member));
                break;
        }
        if (roleInfo.groupObj == null) {
            groupNameText.setText(getString(R.string.view_role_examine_state_role, roleInfo.roleObj.roleName));
            roleNameText.setVisibility(View.INVISIBLE);
        }
        else {
            groupNameText.setText(getString(R.string.view_role_examine_state_group, roleInfo.groupObj.groupName));
            roleNameText.setText(getString(R.string.view_role_examine_state_role, roleInfo.roleObj.roleName));
        }
        TextView cancelButton = findViewById(R.id.cancelButton);
        cancelButton.setText("admin_check_state_0".equals(roleInfo.checkState) ? R.string.view_role_examine_state_delete : R.string.view_role_examine_state_cancel);
        cancelButton.setOnClickListener(v -> {
            AlertDialog.Builder alertDialogBuilder = AlertDialog.Builder.getBottom(getContext());
            if ("admin_check_state_0".equals(roleInfo.checkState)) {
                alertDialogBuilder.setTitle(R.string.view_role_examine_state_delete_confirm_title)
                        .setMessage(R.string.view_role_examine_state_delete_confirm_message);
            }
            else {
                alertDialogBuilder.setTitle(R.string.view_role_examine_state_cancel_confirm_title)
                        .setMessage(R.string.view_role_examine_state_cancel_confirm_message);
            }
            alertDialogBuilder.setButton(DialogInterface.BUTTON_POSITIVE, R.string.resume, true)
                    .setButton(DialogInterface.BUTTON_NEGATIVE, R.string.back, true)
                    .setOnDialogButtonClickListener(onCancelConfirmListener)
                    .show();
        });
        //时间线
        ProgressTimelineView progressTimelineView = findViewById(R.id.examineProgressView);
        for (TimeListNode node : timeList) {
            switch (node.type) {
                case TimeListNode.TYPE_PRIMARY:
                    progressTimelineView.appendTimelineItem(ProgressTimelineView.TYPE_PRIMARY, node.text, node.time, node.admin);
                    break;
                case TimeListNode.TYPE_SUCCESS:
                    progressTimelineView.appendTimelineItem(ProgressTimelineView.TYPE_POSITIVE, node.text, node.time, node.admin);
                    break;
                case TimeListNode.TYPE_WARNING:
                    progressTimelineView.appendTimelineItem(ProgressTimelineView.TYPE_IN_PROGRESS, node.text, null, null);
                    break;
                case TimeListNode.TYPE_DANGER:
                    progressTimelineView.appendTimelineItem(ProgressTimelineView.TYPE_CRITICAL, node.text, node.time, node.admin);
                    break;
            }
        }
        progressTimelineView.finishAppendTimeLineItem();
    }

    private void processTimeListResult(TimeListNode[] timeList) {
        //timeList[0]代表最新状态
        if (TimeListNode.TYPE_DANGER.equals(timeList[0].type)) {
            if (timeList.length > 2) {
                this.timeList = Arrays.copyOfRange(timeList, 1, timeList.length);
                this.timeList[0].type = TimeListNode.TYPE_DANGER;
            }
            else {
                this.timeList = timeList;
            }
        }
        else {
            this.timeList = timeList;
        }
    }

    @Override
    protected void onTaskBegin(int requestCode) {
        super.onTaskBegin(requestCode);
        switch (requestCode) {
            case TASK_GET_TIME_LINE:
                toLoadingState();
                break;
            case TASK_CANCEL_APPLY:
                loadingDialog = AlertDialog.Builder.getCenter(getContext()).setLoading().setOnCancelListener(onLoadingDialogCancelListener).show();
                break;
        }
    }

    @Override
    protected void onTaskFinish(int requestCode) {
        super.onTaskFinish(requestCode);
        switch (requestCode) {
            case TASK_GET_TIME_LINE:
                break;
            case TASK_CANCEL_APPLY:
                loadingDialog.cancel();
                break;
        }
    }

    @Override
    protected void onTaskRetryFailed(int requestCode) {
        super.onTaskRetryFailed(requestCode);
        switch (requestCode) {
            case TASK_GET_TIME_LINE:
                toErrorState();
                break;
            case TASK_CANCEL_APPLY:
                break;
        }
    }

    @Override
    protected void onTaskResult(int requestCode, Object result) {
        super.onTaskResult(requestCode, result);
        ServerResponseNode responseNode = ServerInterfaces.analyseCommonContent((ServerInvoker.InvokeResult) result);
        if (responseNode.code != ServerInterfaces.RESULT_CODE_SUCCESS) {
            return;
        }
        switch (requestCode) {
            case TASK_GET_TIME_LINE:
                processTimeListResult(JsonNode.toObject(responseNode.object, TimeListNode[].class));
                loadMainViews();
                toNormalState();
                break;
            case TASK_CANCEL_APPLY:
                Toast.makeText(getContext(), R.string.view_role_examine_state_cancel_success, Toast.LENGTH_SHORT).show();
                notifyParent(0);
                break;
        }
    }

    @Override
    protected void onErrorStateRetry() {
        super.onErrorStateRetry();
        retryTask(TASK_GET_TIME_LINE);
    }
}
