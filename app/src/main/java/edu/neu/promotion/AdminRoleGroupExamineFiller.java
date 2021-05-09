package edu.neu.promotion;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

import edu.neu.promotion.enties.AdminNode;
import edu.neu.promotion.enties.AdminRoleGroupNode;
import edu.neu.promotion.views.EntityFiller;

public class AdminRoleGroupExamineFiller implements EntityFiller<AdminRoleGroupNode> {

    private TextView nameCardView;
    private TextView nameView;
    private TextView departmentView;
    private TextView stateTextView;

    @Override
    public View generateView(Context context) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.item_admin, null);
        nameCardView = rootView.findViewById(R.id.nameCardView);
        nameView = rootView.findViewById(R.id.nameView);
        departmentView = rootView.findViewById(R.id.departmentView);
        stateTextView = rootView.findViewById(R.id.stateTextView);
        stateTextView.setTextColor(context.getResources().getColor(R.color.positive));
        stateTextView.setText(R.string.member_can_check);
        return rootView;
    }

    @Override
    public void fill(AdminRoleGroupNode entity, Object tag) {
        Resources resources = nameCardView.getContext().getResources();
        nameCardView.setBackground(ResourcesCompat.getDrawable(resources, R.drawable.button_gray_normal, null));
        nameCardView.setText(CoupleNames.getInstance(resources).getShortName(entity.adminObj.adminName));
        nameView.setText(entity.adminObj.adminName);
        boolean hasContent = false;
        departmentView.setText("");
        if (entity.adminObj.adminCollegeObj != null) {
            hasContent = true;
            departmentView.append(entity.adminObj.adminCollegeObj.dictionaryName);
        }
        if (entity.adminObj.adminPosition != null && !entity.adminObj.adminPosition.isEmpty()) {
            if (hasContent) {
                departmentView.append(" - ");
            }
            else {
                hasContent = true;
            }
            departmentView.append(entity.adminObj.adminPosition);
        }
        if (entity.adminObj.isStudent() && entity.adminObj.adminDegree != null && !entity.adminObj.adminDegree.isEmpty()) {
            if (hasContent) {
                departmentView.append(" - ");
            }
            else {
                hasContent = true;
            }
            departmentView.append(entity.adminObj.adminDegree);
        }
        if (!hasContent) {
            departmentView.setText("- -");
        }
        stateTextView.setVisibility(entity.canCheck ? View.VISIBLE : View.GONE);
    }

    @Override
    public View getClickableView() {
        return null;
    }
}
