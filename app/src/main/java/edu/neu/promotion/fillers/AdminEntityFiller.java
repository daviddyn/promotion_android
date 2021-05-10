package edu.neu.promotion.fillers;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

import edu.neu.promotion.R;
import edu.neu.promotion.enties.AdminNode;
import edu.neu.promotion.utils.CoupleNames;
import edu.neu.promotion.views.EntityFiller;

public class AdminEntityFiller implements EntityFiller<AdminNode> {

    private TextView nameCardView;
    private TextView nameView;
    private TextView departmentView;
    private ImageView chooseIcon;

    @Override
    public View generateView(Context context) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.item_admin, null);
        nameCardView = rootView.findViewById(R.id.nameCardView);
        nameView = rootView.findViewById(R.id.nameView);
        departmentView = rootView.findViewById(R.id.departmentView);
        chooseIcon = rootView.findViewById(R.id.chooseIcon);
        return rootView;
    }

    @Override
    public void fill(AdminNode entity, Object tag) {
        Resources resources = nameCardView.getContext().getResources();
        nameCardView.setBackground(ResourcesCompat.getDrawable(resources, entity.isFemale() ? R.drawable.button_female_normal : R.drawable.button_primary_normal, null));
        nameCardView.setText(CoupleNames.getInstance(resources).getShortName(entity.adminName));
        if (entity.adminId.equals(tag)) {
            nameView.setText(resources.getString(R.string.user_name_myself, entity.adminName));
            chooseIcon.setVisibility(View.GONE);
        }
        else {
            nameView.setText(entity.adminName);
            chooseIcon.setVisibility(View.VISIBLE);
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
            if (entity.isStudent() && entity.adminDegree != null && !entity.adminDegree.isEmpty()) {
                departmentView.append(entity.adminDegree);
            }
            else {
                departmentView.append(entity.adminPosition);
            }
        }
        if (!hasContent) {
            departmentView.setText(R.string.user_unknown);
        }
    }

    @Override
    public View getClickableView() {
        return null;
    }
}
