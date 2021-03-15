package edu.neu.promotion;

import android.content.DialogInterface;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.davidsoft.utils.JsonNode;

import edu.neu.promotion.components.AlertDialog;
import edu.neu.promotion.components.BaseActivity;
import edu.neu.promotion.components.PageManager;
import edu.neu.promotion.enties.AdminNode;
import edu.neu.promotion.enties.AdminRoleNode;

public class SelfPage extends TokenRunNetworkTaskPage {

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
        nameCardView.setText(CoupleNames.getInstance(getResource()).getShortName(adminInfo.adminName));
        nameCardView.setOnClickListener(onClickListener);
        nameView = findViewById(R.id.nameView);
        nameView.setText(adminInfo.adminName);
        idView = findViewById(R.id.idView);
        idView.setText(adminInfo.adminAccount);
        roleIconView = findViewById(R.id.roleIconView);
        roleNameView = findViewById(R.id.roleNameView);
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
        selectRoleButton = findViewById(R.id.selectRoleButton);
        selectRoleButton.setOnClickListener(onClickListener);
        changePasswordButton = findViewById(R.id.changePasswordButton);
        changePasswordButton.setOnClickListener(onClickListener);
        settingsButton = findViewById(R.id.settingsButton);
        settingsButton.setOnClickListener(onClickListener);
        logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(onClickListener);
    }

    private void logout() {
        StorageManager.clear(getContext(), StorageManager.TOKEN);
        StorageManager.clear(getContext(), StorageManager.TOKEN_TYPE);
        notifyParent(RESULT_NEED_LOGIN);
    }

    private void selectRole() {
        StorageManager.setValue(getContext(), StorageManager.TOKEN_TYPE, "role");
        notifyParent(0);
    }
}
