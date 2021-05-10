package edu.neu.promotion.pages;

import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

import com.davidsoft.utils.JsonNode;

import edu.neu.promotion.activities.ChangePasswordActivity;
import edu.neu.promotion.utils.CoupleNames;
import edu.neu.promotion.R;
import edu.neu.promotion.ServerInterfaces;
import edu.neu.promotion.ServerInvoker;
import edu.neu.promotion.StorageManager;
import edu.neu.promotion.components.AlertDialog;
import edu.neu.promotion.components.BaseActivity;
import edu.neu.promotion.components.PageManager;
import edu.neu.promotion.enties.AdminNode;
import edu.neu.promotion.enties.AdminRoleNode;
import edu.neu.promotion.enties.ServerResponseNode;

public class SelfPage extends TokenRunNetworkTaskPage {

    private static final int TASK_UPDATE_USER_INFO = 1;

    private final AdminNode adminInfo;
    private final AdminRoleNode roleInfo;

    private DialogInterface.OnClickListener onLogoutConfirmListener;
    private DialogInterface.OnClickListener onSelectRoleConfirmListener;

    private TextView nameCardView;
    private TextView nameView;
    private TextView idView;
    private ImageView roleIconView;
    private TextView roleNameView;
    private View selectRoleButton;
    private View changePasswordButton;
    private View settingsButton;
    private View logoutButton;

    public SelfPage(PageManager pageManager, Object... args) {
        super(pageManager, args);
        adminInfo = JsonNode.toObject(StorageManager.getJson(getContext(), StorageManager.USER_INFO), AdminNode.class);
        roleInfo = JsonNode.toObject(StorageManager.getJson(getContext(), StorageManager.ROLE_INFO), AdminRoleNode.class);
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        setActionbarStyle(BaseActivity.ACTIONBAR_STYLE_NO_BACK);
        setTitle(R.string.home_myself);

        View.OnClickListener onClickListener = v -> {
            if (v == nameCardView) {

            }
            else if (v == selectRoleButton) {
                AlertDialog.Builder.getBottom(getContext())
                        .setTitle(R.string.self_select_role)
                        .setMessage(R.string.confirm_select_role_instruction)
                        .setButton(DialogInterface.BUTTON_POSITIVE, R.string.resume, true)
                        .setButton(DialogInterface.BUTTON_NEGATIVE, R.string.back, true)
                        .setOnDialogButtonClickListener(onSelectRoleConfirmListener)
                        .show();
            }
            else if (v == changePasswordButton) {
                startActivity(new Intent(getContext(), ChangePasswordActivity.class));
            }
            else if (v == settingsButton) {

            }
            else if (v == logoutButton) {
                AlertDialog.Builder.getBottom(getContext())
                        .setTitle(R.string.confirm_logout_title)
                        .setMessage(R.string.confirm_logout_instruction)
                        .setButton(DialogInterface.BUTTON_POSITIVE, R.string.resume, true)
                        .setButton(DialogInterface.BUTTON_NEGATIVE, R.string.back, true)
                        .setOnDialogButtonClickListener(onLogoutConfirmListener)
                        .show();
            }
        };
        onLogoutConfirmListener = (dialog, which) -> {
            dialog.dismiss();
            if (which == DialogInterface.BUTTON_POSITIVE) {
                logout();
            }
        };
        onSelectRoleConfirmListener = (dialog, which) -> {
            dialog.dismiss();
            if (which == DialogInterface.BUTTON_POSITIVE) {
                selectRole();
            }
        };

        setContentView(R.layout.page_self);
        nameCardView = findViewById(R.id.nameCardView);
        nameCardView.setOnClickListener(onClickListener);
        nameView = findViewById(R.id.nameView);
        idView = findViewById(R.id.rightSubTextView);
        roleIconView = findViewById(R.id.roleIconView);
        roleNameView = findViewById(R.id.roleNameView);
        selectRoleButton = findViewById(R.id.selectRoleButton);
        selectRoleButton.setOnClickListener(onClickListener);
        changePasswordButton = findViewById(R.id.changePasswordButton);
        changePasswordButton.setOnClickListener(onClickListener);
        settingsButton = findViewById(R.id.settingsButton);
        settingsButton.setOnClickListener(onClickListener);
        logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(onClickListener);

        updateViews();
    }

    private void updateViews() {
        nameCardView.setText(CoupleNames.getInstance(getResource()).getShortName(adminInfo.adminName));
        nameCardView.setBackground(ResourcesCompat.getDrawable(getResource(), adminInfo.isFemale() ? R.drawable.button_female_normal : R.drawable.button_primary_normal, null));
        nameView.setText(adminInfo.adminName);
        idView.setText(adminInfo.adminAccount);
        switch (roleInfo.roleId) {
            case "role_monitor":
                roleIconView.setImageDrawable(getDrawable(R.drawable.ic_role_admin));
                roleNameView.setText(roleInfo.groupObj.groupName + " - " + roleInfo.roleObj.roleName);
                break;
            case "role_professor":
                roleIconView.setImageDrawable(getDrawable(R.drawable.ic_role_professor));
                roleNameView.setText(roleInfo.roleObj.roleName);
                break;
            default:
                roleIconView.setImageDrawable(getDrawable(R.drawable.ic_role_member));
                roleNameView.setText(roleInfo.groupObj.groupName + " - " + roleInfo.roleObj.roleName);
                break;
        }

    }

    @Override
    protected void onSwitchedOn() {
        super.onResume();
        runTask(ServerInterfaces.getMyselfMessage(getToken()), Integer.MAX_VALUE, TASK_UPDATE_USER_INFO);
    }

    private void logout() {
        cancelAllTasks();
        StorageManager.clear(getContext(), StorageManager.TOKEN);
        StorageManager.clear(getContext(), StorageManager.TOKEN_TYPE);
        notifyParent(RESULT_NEED_LOGIN);
    }

    private void selectRole() {
        cancelAllTasks();
        StorageManager.setValue(getContext(), StorageManager.TOKEN_TYPE, "role");
        notifyParent(0);
    }

    @Override
    protected void onTaskResult(int requestCode, Object result) {
        super.onTaskResult(requestCode, result);
        ServerResponseNode response = ServerInterfaces.analyseCommonContent((ServerInvoker.InvokeResult) result);
        if (response.code == ServerInterfaces.RESULT_CODE_SUCCESS) {
            StorageManager.setJson(getContext(), StorageManager.USER_INFO, response.object);
            updateViews();
        }
    }
}
