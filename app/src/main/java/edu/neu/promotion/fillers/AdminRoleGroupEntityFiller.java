package edu.neu.promotion.fillers;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

import edu.neu.promotion.R;
import edu.neu.promotion.enties.AdminRoleGroupNode;
import edu.neu.promotion.utils.CoupleNames;
import edu.neu.promotion.views.EntityFiller;

public class AdminRoleGroupEntityFiller implements EntityFiller<AdminRoleGroupNode> {

    private TextView nameCardView;
    private TextView nameView;
    private TextView leftSubTextView;
    private ImageView chooseIcon;
    private TextView stateTextView;

    @Override
    public View generateView(Context context) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.item_member, null);
        nameCardView = rootView.findViewById(R.id.nameCardView);
        nameView = rootView.findViewById(R.id.nameView);
        leftSubTextView = rootView.findViewById(R.id.leftSubTextView);
        chooseIcon = rootView.findViewById(R.id.chooseIcon);
        stateTextView = rootView.findViewById(R.id.actionView);
        stateTextView.setTextColor(context.getResources().getColor(R.color.positive));
        stateTextView.setText(R.string.member_can_check);
        return rootView;
    }

    @Override
    public void fill(AdminRoleGroupNode entity, Object tag) {
        Resources resources = nameCardView.getContext().getResources();
        nameCardView.setText(CoupleNames.getInstance(resources).getShortName(entity.adminObj.adminName));
        if (entity.adminId.equals(tag)) {
            nameView.setText(resources.getString(R.string.user_name_myself, entity.adminObj.adminName));
            chooseIcon.setVisibility(View.GONE);
        }
        else {
            nameView.setText(entity.adminObj.adminName);
            chooseIcon.setVisibility(View.VISIBLE);
        }
        if ("admin_check_state_5".equals(entity.checkState)) {
            nameCardView.setBackground(ResourcesCompat.getDrawable(resources, entity.adminObj.isFemale() ? R.drawable.button_female_normal : R.drawable.button_primary_normal, null));
            boolean hasContent = false;
            leftSubTextView.setText("");
            if (entity.adminObj.adminCollegeObj != null) {
                hasContent = true;
                leftSubTextView.append(entity.adminObj.adminCollegeObj.dictionaryName);
            }
            if (entity.adminObj.adminPosition != null && !entity.adminObj.adminPosition.isEmpty()) {
                if (hasContent) {
                    leftSubTextView.append(" - ");
                }
                else {
                    hasContent = true;
                }
                if (entity.adminObj.isStudent() && entity.adminObj.adminDegree != null && !entity.adminObj.adminDegree.isEmpty()) {
                    leftSubTextView.append(entity.adminObj.adminDegree);
                }
                else {
                    leftSubTextView.append(entity.adminObj.adminPosition);
                }
            }
            if (!hasContent) {
                leftSubTextView.setText(R.string.user_unknown);
            }
            stateTextView.setVisibility(View.GONE);
        }
        else {
            nameCardView.setBackground(ResourcesCompat.getDrawable(resources, R.drawable.button_gray_normal, null));
            leftSubTextView.setText(entity.checkStateObj.dictionaryName);
            stateTextView.setVisibility(entity.canCheck ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public View getClickableView() {
        return null;
    }
}
