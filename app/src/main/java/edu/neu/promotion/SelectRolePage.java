package edu.neu.promotion;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.davidsoft.utils.JsonNode;

import edu.neu.promotion.components.AlertDialog;
import edu.neu.promotion.components.BaseActivity;
import edu.neu.promotion.components.PageManager;
import edu.neu.promotion.enties.AdminNode;
import edu.neu.promotion.enties.AdminRoleNode;
import edu.neu.promotion.enties.ServerResponseNode;

public class SelectRolePage extends TokenRunNetworkTaskPage {

    private static final int ACTIVITY_REQUEST_VIEW_ROLE = 1;
    private static final int ACTIVITY_REQUEST_ADD_ROLE = 2;

    private static final int TASK_LIST_USER_ROLES = 1;
    private static final int TASK_ACCESS_ROLE = 2;

    private final AdminNode adminInfo;
    private final AdminRoleNode lastUseRole;

    private AdminRoleNode[] roles;

    private AlertDialog loadingDialog;
    private DialogInterface.OnCancelListener onLoadingDialogCancelListener;
    private View.OnClickListener onRoleButtonClickListener;
    private PopupMenu.OnMenuItemClickListener onActionbarMenuItemClickListener;
    private DialogInterface.OnClickListener onLogoutConfirmListener;

    private TextView subtitleTextView;
    private View recentTitleView;
    private LinearLayout recentListView;
    private View availableTitleView;
    private LinearLayout availableListView;
    private View unavailableTitleView;
    private LinearLayout unavailableListView;
    private View newTitleView;
    private LinearLayout newListView;

    private View selectedRoleItemView;

    private AdminRoleNode selectedRole;

    public SelectRolePage(PageManager pageManager, Object... args) {
        super(pageManager, args);
        adminInfo = JsonNode.toObject(StorageManager.getJson(getContext(), StorageManager.USER_INFO), AdminNode.class);
        lastUseRole = JsonNode.toObject(StorageManager.getJson(getContext(), StorageManager.ROLE_INFO), AdminRoleNode.class);
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        setActionbarStyle(BaseActivity.ACTIONBAR_STYLE_NO_BACK);
        addActionbarButton(getDrawable(R.drawable.ic_actionbar_menu), null);

        onLoadingDialogCancelListener = dialog -> cancelTask(TASK_ACCESS_ROLE);
        onRoleButtonClickListener = this::onRoleButtonClick;
        onActionbarMenuItemClickListener = item -> {
            AlertDialog.Builder.getBottom(getContext())
                    .setTitle(R.string.confirm_logout_title)
                    .setMessage(R.string.confirm_logout_instruction)
                    .setButton(DialogInterface.BUTTON_POSITIVE, R.string.resume, true)
                    .setButton(DialogInterface.BUTTON_NEGATIVE, R.string.back, true)
                    .setOnDialogButtonClickListener(onLogoutConfirmListener)
                    .show();
            return true;
        };
        onLogoutConfirmListener = (dialog, which) -> {
            dialog.dismiss();
            if (which == DialogInterface.BUTTON_POSITIVE) {
                logout();
            }
        };

        runTask(ServerInterfaces.Role.getAdminRole(getToken()), TASK_LIST_USER_ROLES);
    }

    private void loadMainViews() {
        setContentView(R.layout.page_select_role);
        //显示姓名
        ((TextView) findViewById(R.id.titleTextView)).setText(getString(R.string.select_role_title, adminInfo.adminName));
        //其他
        subtitleTextView = findViewById(R.id.subtitleTextView);
        recentTitleView = findViewById(R.id.recentTitleView);
        recentListView = findViewById(R.id.recentListView);
        availableTitleView = findViewById(R.id.availableTitleView);
        availableListView = findViewById(R.id.availableListView);
        unavailableTitleView = findViewById(R.id.unavailableTitleView);
        unavailableListView = findViewById(R.id.unavailableListView);
        newTitleView = findViewById(R.id.newTitleView);
        newListView = findViewById(R.id.newListView);
        if (roles.length == 0) {
            subtitleTextView.setText(R.string.select_role_subtitle_no_role);
            recentTitleView.setVisibility(View.GONE);
            recentListView.setVisibility(View.GONE);
            availableTitleView.setVisibility(View.GONE);
            availableListView.setVisibility(View.GONE);
            unavailableTitleView.setVisibility(View.GONE);
            unavailableListView.setVisibility(View.GONE);
            newTitleView.setVisibility(View.GONE);
            newListView.setVisibility(View.GONE);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            int pagePadding = (int) getDimension(R.dimen.page_padding);
            layoutParams.setMargins(pagePadding, (int) getDimension(R.dimen.navigate_content_margin), pagePadding, 0);
            View roleItemView = getLayoutInflater().inflate(R.layout.item_role, null);
            setItemCreate(roleItemView);
            ((LinearLayout) findViewById(R.id.mainLinearLayout)).addView(roleItemView, layoutParams);
        }
        else {
            for (int i = 0; i < roles.length; i++) {
                View roleItemView;
                if ("admin_check_state_5".equals(roles[i].checkState)) {
                    if (lastUseRole != null && roles[i].adminRoleGroupId.equals(lastUseRole.adminRoleGroupId)) {
                        getLayoutInflater().inflate(R.layout.item_role, recentListView);
                        roleItemView = recentListView.getChildAt(recentListView.getChildCount() - 1);
                    }
                    else {
                        getLayoutInflater().inflate(R.layout.item_role, availableListView);
                        roleItemView = availableListView.getChildAt(availableListView.getChildCount() - 1);
                    }
                }
                else {
                    getLayoutInflater().inflate(R.layout.item_role, unavailableListView);
                    roleItemView = unavailableListView.getChildAt(unavailableListView.getChildCount() - 1);
                }
                setItemRole(roleItemView, i);
            }
            if (recentListView.getChildCount() == 0) {
                recentTitleView.setVisibility(View.GONE);
                recentListView.setVisibility(View.GONE);
            }
            if (availableListView.getChildCount() == 0) {
                availableTitleView.setVisibility(View.GONE);
                availableListView.setVisibility(View.GONE);
            }
            if (unavailableListView.getChildCount() == 0) {
                unavailableTitleView.setVisibility(View.GONE);
                unavailableListView.setVisibility(View.GONE);
            }
            if (recentListView.getChildCount() == 0 && availableListView.getChildCount() == 0) {
                subtitleTextView.setText(R.string.select_role_subtitle_no_available);
            }
            else {
                subtitleTextView.setText(R.string.select_role_subtitle_normal);
            }
            getLayoutInflater().inflate(R.layout.item_role, newListView);
            setItemCreate(newListView.getChildAt(0));
        }
    }

    private void setItemCreate(View roleItemView) {
        roleItemView.setOnClickListener(v -> onCreateRoleButtonClick());
        ((ImageView) roleItemView.findViewById(R.id.roleIconView)).setImageDrawable(getDrawable(R.drawable.ic_role_add));
        TextView groupNameText = roleItemView.findViewById(R.id.groupNameText);
        groupNameText.setText(R.string.select_role_apply);
        groupNameText.setTextColor(getColor(R.color.primary));
        roleItemView.findViewById(R.id.roleNameText).setVisibility(View.GONE);
        roleItemView.findViewById(R.id.stateTextView).setVisibility(View.GONE);
    }

    private void setItemRole(View roleItemView, int position) {
        AdminRoleNode role = roles[position];
        roleItemView.setTag(position);
        roleItemView.setOnClickListener(onRoleButtonClickListener);

        ImageView roleIconView = roleItemView.findViewById(R.id.roleIconView);
        TextView groupNameText = roleItemView.findViewById(R.id.groupNameText);
        TextView roleNameText = roleItemView.findViewById(R.id.roleNameText);
        switch (role.roleId) {
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
        if (role.groupObj == null) {
            groupNameText.setText(role.roleObj.roleName);
            roleNameText.setVisibility(View.GONE);
        }
        else {
            groupNameText.setText(role.groupObj.groupName);
            roleNameText.setText(role.roleObj.roleName);
        }
        switch (role.checkState) {
            case "admin_check_state_0":
                ((TextView) roleItemView.findViewById(R.id.stateTextView)).setText(R.string.select_role_denied);
                roleIconView.setImageTintList(ColorStateList.valueOf(getColor(R.color.critical)));
                break;
            case "admin_check_state_5":
                roleItemView.findViewById(R.id.stateTextView).setVisibility(View.GONE);
                roleIconView.setImageTintList(ColorStateList.valueOf(getColor(R.color.primary)));
                break;
            default:
                ((TextView) roleItemView.findViewById(R.id.stateTextView)).setText(R.string.select_role_unavailable);
                roleIconView.setImageTintList(ColorStateList.valueOf(getColor(R.color.text_tertiary)));
                break;
        }
    }

    @Override
    protected void onTaskBegin(int requestCode) {
        super.onTaskBegin(requestCode);
        switch (requestCode) {
            case TASK_LIST_USER_ROLES:
                toLoadingState();
                break;
            case TASK_ACCESS_ROLE:
                loadingDialog = AlertDialog.Builder.getCenter(getContext()).setLoading().setOnCancelListener(onLoadingDialogCancelListener).show();
                break;
        }
    }

    @Override
    protected void onTaskFinish(int requestCode) {
        super.onTaskFinish(requestCode);
        switch (requestCode) {
            case TASK_LIST_USER_ROLES:
                break;
            case TASK_ACCESS_ROLE:
                loadingDialog.cancel();
                break;
        }
    }

    @Override
    protected void onTaskResult(int requestCode, Object result) {
        super.onTaskResult(requestCode, result);
        ServerResponseNode response = ServerInterfaces.analyseCommonContent((ServerInvoker.InvokeResult) result);
        switch (requestCode) {
            case TASK_LIST_USER_ROLES:
                if (response.code == ServerInterfaces.RESULT_CODE_SUCCESS) {
                    roles = JsonNode.toObject(response.object, AdminRoleNode[].class);
                    loadMainViews();
                }
                toNormalState();
                break;
            case TASK_ACCESS_ROLE:
                StorageManager.setValue(getContext(), StorageManager.TOKEN, response.object.getField("token").getValue());
                StorageManager.setValue(getContext(), StorageManager.TOKEN_TYPE, "normal");
                StorageManager.setJson(getContext(), StorageManager.ROLE_INFO, JsonNode.valueOf(selectedRole));
                notifyParent(0);
                break;
        }

    }

    @Override
    protected void onTaskRetryFailed(int requestCode) {
        super.onTaskRetryFailed(requestCode);
        switch (requestCode) {
            case TASK_LIST_USER_ROLES:
                toErrorState();
                break;
            case TASK_ACCESS_ROLE:
                break;
        }
    }

    @Override
    protected void onErrorStateRetry() {
        super.onErrorStateRetry();
        retryTask(TASK_LIST_USER_ROLES);
    }

    @Override
    protected void onActionbarButtonClick(int position, View viewForAnchor) {
        super.onActionbarButtonClick(position, viewForAnchor);
        PopupMenu actionbarMenu = new PopupMenu(getContext(), viewForAnchor);
        actionbarMenu.getMenuInflater().inflate(R.menu.page_select_role, actionbarMenu.getMenu());
        actionbarMenu.setOnMenuItemClickListener(onActionbarMenuItemClickListener);
        actionbarMenu.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ACTIVITY_REQUEST_VIEW_ROLE:
                if (resultCode == Activity.RESULT_OK) {
                    unavailableListView.removeView(selectedRoleItemView);
                    if (unavailableListView.getChildCount() == 0) {
                        unavailableListView.setVisibility(View.GONE);
                        unavailableTitleView.setVisibility(View.GONE);
                    }
                }
                break;
            case ACTIVITY_REQUEST_ADD_ROLE:
                if (resultCode == Activity.RESULT_OK) {
                    setContentView(null);
                    runTask(ServerInterfaces.Role.getAdminRole(getToken()), TASK_LIST_USER_ROLES);
                }
                break;
        }
    }

    private void onRoleButtonClick(View v) {
        int position = (int) v.getTag();
        if ("admin_check_state_5".equals(roles[position].checkState)) {
            selectedRole = roles[position];
            runTask(ServerInterfaces.Role.accessRole(getToken(), selectedRole.adminRoleGroupId), TASK_ACCESS_ROLE);
        }
        else {
            selectedRoleItemView = v;
            Intent intent = new Intent(getContext(), ViewRoleExamineStateActivity.class);
            intent.putExtra("roleInfo", roles[position]);
            startActivityForResult(intent, ACTIVITY_REQUEST_VIEW_ROLE);
        }
    }

    private void onCreateRoleButtonClick() {
        startActivityForResult(new Intent(getContext(), CreateRoleActivity.class), ACTIVITY_REQUEST_ADD_ROLE);
    }

    private void logout() {
        cancelTask(TASK_LIST_USER_ROLES);
        StorageManager.clear(getContext(), StorageManager.TOKEN);
        StorageManager.clear(getContext(), StorageManager.TOKEN_TYPE);
        notifyParent(RESULT_NEED_LOGIN);
    }
}
