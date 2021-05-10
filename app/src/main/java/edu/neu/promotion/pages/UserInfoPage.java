package edu.neu.promotion.pages;

import android.content.Intent;
import android.net.Uri;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

import edu.neu.promotion.R;
import edu.neu.promotion.components.Page;
import edu.neu.promotion.components.PageManager;
import edu.neu.promotion.enties.AdminRoleGroupNode;
import edu.neu.promotion.utils.CoupleNames;

public class UserInfoPage extends Page {

    private final AdminRoleGroupNode adminInfo;

    public UserInfoPage(PageManager pageManager, Object... args) {
        super(pageManager, args);
        adminInfo = (AdminRoleGroupNode) args[0];
    }

    @Override
    protected void onCreate() {
        super.onCreate();

        setTitle(R.string.user_info_title);

        setContentView(R.layout.page_user_info);
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
                roleNameView.setText(adminInfo.groupObj.groupName + " - " + adminInfo.roleObj.roleName);
                break;
            case "role_professor":
                roleIconView.setImageDrawable(getDrawable(R.drawable.ic_role_professor));
                roleNameView.setText(adminInfo.roleObj.roleName);
                break;
            default:
                roleIconView.setImageDrawable(getDrawable(R.drawable.ic_role_member));
                roleNameView.setText(adminInfo.groupObj.groupName + " - " + adminInfo.roleObj.roleName);
                break;
        }
        ((TextView) findViewById(R.id.accountView)).setText(adminInfo.adminObj.adminAccount);
        textView = findViewById(R.id.phoneView);
        TextView callButton = findViewById(R.id.callButton);
        if (adminInfo.adminObj.adminPhone == null || adminInfo.adminObj.adminPhone.isEmpty()) {
            textView.setText(R.string.user_unspecified);
            callButton.setEnabled(false);
        }
        else {
            textView.setText(adminInfo.adminObj.adminPhone);
            callButton.setEnabled(true);
        }
        callButton.setOnClickListener(v -> {
            startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + adminInfo.adminObj.adminPhone)));
        });
    }
}
