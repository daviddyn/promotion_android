package edu.neu.promotion.pages;

import android.widget.ImageView;
import android.widget.TextView;

import com.davidsoft.utils.JsonNode;

import edu.neu.promotion.CoupleNames;
import edu.neu.promotion.R;
import edu.neu.promotion.StorageManager;
import edu.neu.promotion.components.BaseActivity;
import edu.neu.promotion.components.Page;
import edu.neu.promotion.components.PageManager;
import edu.neu.promotion.enties.AdminNode;
import edu.neu.promotion.enties.AdminRoleNode;

public class SelfPage extends Page {

    private final AdminNode adminInfo;
    private final AdminRoleNode roleInfo;

    private TextView nameCardView;
    private TextView nameView;
    private TextView idView;
    private ImageView roleIconView;
    private TextView roleNameView;

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

        setContentView(R.layout.page_self);
        nameCardView = findViewById(R.id.nameCardView);
        nameCardView.setText(CoupleNames.getInstance(getResource()).getShortName(adminInfo.adminName));
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
    }
}
