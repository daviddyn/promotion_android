package edu.neu.promotion;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

import edu.neu.promotion.enties.AdminNode;
import edu.neu.promotion.views.EntityFiller;

public class AdminEntityFiller implements EntityFiller<AdminNode> {

    private TextView nameCardView;
    private TextView nameView;
    private TextView departmentView;

    @Override
    public View generateView(Context context) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.item_admin, null);
        nameCardView = rootView.findViewById(R.id.nameCardView);
        nameView = rootView.findViewById(R.id.nameView);
        departmentView = rootView.findViewById(R.id.departmentView);
        return rootView;
    }

    @Override
    public void fill(AdminNode entity, Object tag) {
        Resources resources = nameCardView.getContext().getResources();
        nameCardView.setBackground(ResourcesCompat.getDrawable(resources, "女".equals(entity.adminSex) ? R.drawable.button_female_normal : R.drawable.button_primary_normal, null));
        nameCardView.setText(CoupleNames.getInstance(resources).getShortName(entity.adminName));
        if (entity.adminId.equals(tag)) {
            nameView.setText(entity.adminName + resources.getString(R.string.myself));
        }
        else {
            nameView.setText(entity.adminName);
        }
        boolean hasContent = false;
        departmentView.setText("");
        if (entity.adminCollegeObj != null) {
            hasContent = true;
            departmentView.append(entity.adminCollegeObj.dictionaryName);
        }
        if (entity.adminPosition != null && !entity.adminPosition.isEmpty()) {
            if (hasContent) {
                departmentView.append(" - ");
            }
            else {
                hasContent = true;
            }
            departmentView.append(entity.adminPosition);
        }
        if (entity.isStudent() && entity.adminDegree != null && !entity.adminDegree.isEmpty()) {
            if (hasContent) {
                departmentView.append(" - ");
            }
            else {
                hasContent = true;
            }
            departmentView.append(entity.adminDegree);
        }
        if (!hasContent) {
            departmentView.setText("- -");
        }
    }

    @Override
    public View getClickableView() {
        return null;
    }
}
