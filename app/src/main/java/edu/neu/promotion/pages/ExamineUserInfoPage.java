package edu.neu.promotion.pages;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.res.ResourcesCompat;

import com.davidsoft.utils.JsonNode;

import java.util.Arrays;

import edu.neu.promotion.R;
import edu.neu.promotion.ServerInterfaces;
import edu.neu.promotion.ServerInvoker;
import edu.neu.promotion.components.AlertDialog;
import edu.neu.promotion.components.PageManager;
import edu.neu.promotion.enties.AdminRoleGroupNode;
import edu.neu.promotion.enties.ServerResponseNode;
import edu.neu.promotion.enties.TimeListNode;
import edu.neu.promotion.utils.CoupleNames;
import edu.neu.promotion.views.ProgressTimelineView;

public class ExamineUserInfoPage extends TokenRunNetworkTaskPage {

    public static final int RESULT_ACCEPT = 1;
    public static final int RESULT_DENIED = 2;

    private static final int TASK_GET_TIMELINE = 1;
    private static final int TASK_REFRESH_TIMELINE = 2;
    private static final int TASK_ACCEPT = 3;
    private static final int TASK_DENIED = 4;

    private final AdminRoleGroupNode adminInfo;

    private TimeListNode[] timeList;

    private AlertDialog loadingDialog;
    private DialogInterface.OnCancelListener onLoadingDialogCancelListener;
    private DialogInterface.OnClickListener onAcceptDialogListener;
    private DialogInterface.OnClickListener onDeniedDialogListener;

    private ProgressTimelineView examineProgressView;
    private TextView acceptButton;
    private TextView deniedButton;
    private TextView callButton;

    private String applyRoleGroupString;

    private boolean currentIsAccept;

    public ExamineUserInfoPage(PageManager pageManager, Object... args) {
        super(pageManager, args);
        adminInfo = (AdminRoleGroupNode) args[0];
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        setTitle(R.string.examine_title);

        onLoadingDialogCancelListener = dialog -> cancelTask(currentIsAccept ? TASK_ACCEPT : TASK_DENIED);
        onAcceptDialogListener = (dialog, which) -> {
            dialog.dismiss();
            if (which == DialogInterface.BUTTON_POSITIVE) {
                currentIsAccept = true;
                runTask(ServerInterfaces.Role.checkRole(getToken(), adminInfo.adminRoleGroupId), TASK_ACCEPT);
            }
        };
        onDeniedDialogListener = (dialog, which) -> {
            dialog.dismiss();
            if (which == DialogInterface.BUTTON_POSITIVE) {
                currentIsAccept = false;
                runTask(ServerInterfaces.Role.uncheckRole(getToken(), adminInfo.adminRoleGroupId), TASK_DENIED);
            }
        };

        runTask(ServerInterfaces.Role.getAdminRoleTimeList(getToken(), adminInfo.adminRoleGroupId), TASK_GET_TIMELINE);
    }

    private void loadMainView() {
        View.OnClickListener onClickListener = v -> {
            if (v == acceptButton) {
                AlertDialog.Builder.getCenter(getContext())
                        .setTitle(R.string.examine_accept_title)
                        .setMessage(getString(R.string.examine_accept_confirm, adminInfo.adminObj.adminName, applyRoleGroupString))
                        .setButton(DialogInterface.BUTTON_POSITIVE, R.string.ok, true)
                        .setButton(DialogInterface.BUTTON_NEGATIVE, R.string.cancel, true)
                        .setOnDialogButtonClickListener(onAcceptDialogListener)
                        .show();
            }
            else if (v == deniedButton) {
                AlertDialog.Builder.getCenter(getContext())
                        .setTitle(R.string.examine_denied_title)
                        .setMessage(getString(R.string.examine_denied_confirm, adminInfo.adminObj.adminName))
                        .setButton(DialogInterface.BUTTON_POSITIVE, R.string.ok, true)
                        .setButton(DialogInterface.BUTTON_NEGATIVE, R.string.cancel, true)
                        .setOnDialogButtonClickListener(onDeniedDialogListener)
                        .show();
            }
            else if (v == callButton) {
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + adminInfo.adminObj.adminPhone)));
            }
        };
        setContentView(R.layout.page_examine_user_info);
        ((TextView) findViewById(R.id.nameView)).setText(adminInfo.adminObj.adminName);
        TextView textView = findViewById(R.id.nameCardView);
        textView.setText(CoupleNames.getInstance(getResource()).getShortName(adminInfo.adminObj.adminName));
        textView.setBackground(ResourcesCompat.getDrawable(getResource(), adminInfo.adminObj.isFemale() ? R.drawable.button_female_normal : R.drawable.button_primary_normal, null));
        textView = findViewById(R.id.collegeView);
        boolean hasContent = false;
        textView.setText("");
        if (adminInfo.adminObj.adminCollegeObj != null) {
            hasContent = true;
            textView.append(adminInfo.adminObj.adminCollegeObj.dictionaryName);
        }
        if (adminInfo.adminObj.adminPosition != null && !adminInfo.adminObj.adminPosition.isEmpty()) {
            if (hasContent) {
                textView.append(" - ");
            }
            else {
                hasContent = true;
            }
            if (adminInfo.adminObj.isStudent() && adminInfo.adminObj.adminDegree != null && !adminInfo.adminObj.adminDegree.isEmpty()) {
                textView.append(adminInfo.adminObj.adminDegree);
            }
            else {
                textView.append(adminInfo.adminObj.adminPosition);
            }
        }
        if (!hasContent) {
            textView.setText(R.string.user_unknown);
        }
        ImageView roleIconView = findViewById(R.id.roleIconView);
        TextView roleNameView = findViewById(R.id.roleNameView);
        switch (adminInfo.roleObj.roleId) {
            case "role_monitor":
                roleIconView.setImageDrawable(getDrawable(R.drawable.ic_role_admin));
                applyRoleGroupString = adminInfo.groupObj.groupName + " - " + adminInfo.roleObj.roleName;
                roleNameView.setText(getResource().getString(R.string.view_role_examine_state_role, applyRoleGroupString));
                break;
            case "role_professor":
                roleIconView.setImageDrawable(getDrawable(R.drawable.ic_role_professor));
                applyRoleGroupString = adminInfo.roleObj.roleName;
                roleNameView.setText(getResource().getString(R.string.view_role_examine_state_role, adminInfo.roleObj.roleName));
                break;
            default:
                roleIconView.setImageDrawable(getDrawable(R.drawable.ic_role_member));
                applyRoleGroupString = adminInfo.groupObj.groupName + " - " + adminInfo.roleObj.roleName;
                roleNameView.setText(getResource().getString(R.string.view_role_examine_state_role, applyRoleGroupString));
                break;
        }
        ((TextView) findViewById(R.id.accountView)).setText(adminInfo.adminObj.adminAccount);
        textView = findViewById(R.id.phoneView);
        callButton = findViewById(R.id.callButton);
        callButton.setOnClickListener(onClickListener);
        if (adminInfo.adminObj.adminPhone == null || adminInfo.adminObj.adminPhone.isEmpty()) {
            textView.setText(R.string.user_unspecified);
            callButton.setEnabled(false);
        }
        else {
            textView.setText(adminInfo.adminObj.adminPhone);
            callButton.setEnabled(true);
        }
        examineProgressView = findViewById(R.id.examineProgressView);
        acceptButton = findViewById(R.id.acceptButton);
        acceptButton.setOnClickListener(onClickListener);
        deniedButton = findViewById(R.id.deniedButton);
        deniedButton.setOnClickListener(onClickListener);

        refreshViews();
    }

    private void refreshViews() {
        if (timeList != null) {
            examineProgressView.removeAllViews();
            for (TimeListNode node : timeList) {
                switch (node.type) {
                    case TimeListNode.TYPE_PRIMARY:
                        examineProgressView.appendTimelineItem(ProgressTimelineView.TYPE_PRIMARY, node.text, node.time, node.admin);
                        break;
                    case TimeListNode.TYPE_SUCCESS:
                        examineProgressView.appendTimelineItem(ProgressTimelineView.TYPE_POSITIVE, node.text, node.time, node.admin);
                        break;
                    case TimeListNode.TYPE_WARNING:
                        examineProgressView.appendTimelineItem(ProgressTimelineView.TYPE_IN_PROGRESS, node.text, null, null);
                        break;
                    case TimeListNode.TYPE_DANGER:
                        examineProgressView.appendTimelineItem(ProgressTimelineView.TYPE_CRITICAL, node.text, node.time, node.admin);
                        break;
                }
            }
            examineProgressView.finishAppendTimeLineItem();
        }
        if (!adminInfo.canCheck) {
            acceptButton.setText(R.string.examine_already_accept);
            acceptButton.setEnabled(false);
            deniedButton.setEnabled(false);
        }
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
            case TASK_GET_TIMELINE:
                break;
            case TASK_ACCEPT:
            case TASK_DENIED:
                loadingDialog = AlertDialog.Builder.getCenter(getContext()).setLoading().setOnCancelListener(onLoadingDialogCancelListener).show();
                break;
        }
    }

    @Override
    protected void onTaskFinish(int requestCode) {
        super.onTaskFinish(requestCode);
        switch (requestCode) {
            case TASK_ACCEPT:
            case TASK_DENIED:
                loadingDialog.cancel();
                break;
        }
    }

    @Override
    protected void onTaskRetryFailed(int requestCode) {
        super.onTaskRetryFailed(requestCode);
        switch (requestCode) {
            case TASK_GET_TIMELINE:
                toErrorState();
                break;
        }
    }

    @Override
    protected void onErrorStateRetry() {
        super.onErrorStateRetry();
        retryTask(TASK_GET_TIMELINE);
    }

    @Override
    protected void onTaskResult(int requestCode, Object result) {
        super.onTaskResult(requestCode, result);
        ServerResponseNode responseNode = ServerInterfaces.analyseCommonContent((ServerInvoker.InvokeResult) result);
        if (responseNode.code != ServerInterfaces.RESULT_CODE_SUCCESS) {
            return;
        }
        switch (requestCode) {
            case TASK_GET_TIMELINE:
                processTimeListResult(JsonNode.toObject(responseNode.object, TimeListNode[].class));
                toNormalState();
                loadMainView();
                break;
            case TASK_REFRESH_TIMELINE:
                processTimeListResult(JsonNode.toObject(responseNode.object, TimeListNode[].class));
                refreshViews();
                break;
            case TASK_ACCEPT:
                Toast.makeText(getContext(), R.string.examine_accept_success, Toast.LENGTH_SHORT).show();
                notifyParent(RESULT_ACCEPT, "");
                timeList = null;
                adminInfo.canCheck = false;
                refreshViews();
                //TODO: 重新获取时间线
                runTask(ServerInterfaces.Role.getAdminRoleTimeList(getToken(), adminInfo.adminRoleGroupId), TASK_REFRESH_TIMELINE);
                break;
            case TASK_DENIED:
                Toast.makeText(getContext(), R.string.examine_denied_success, Toast.LENGTH_SHORT).show();
                notifyParent(RESULT_DENIED);
                break;
        }
    }
}
