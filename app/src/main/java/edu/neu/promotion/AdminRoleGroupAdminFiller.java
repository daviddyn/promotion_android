package edu.neu.promotion;

import android.content.Context;
import android.view.View;

import edu.neu.promotion.enties.AdminRoleGroupNode;
import edu.neu.promotion.views.EntityFiller;

public class AdminRoleGroupAdminFiller implements EntityFiller<AdminRoleGroupNode> {

    private final AdminEntityFiller adminEntityFiller = new AdminEntityFiller();

    @Override
    public View generateView(Context context) {
        return adminEntityFiller.generateView(context);
    }

    @Override
    public void fill(AdminRoleGroupNode entity, Object tag) {
        adminEntityFiller.fill(entity.adminObj, tag);
    }

    @Override
    public View getClickableView() {
        return adminEntityFiller.getClickableView();
    }
}
