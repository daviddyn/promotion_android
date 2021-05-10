package edu.neu.promotion.fillers;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import edu.neu.promotion.enties.ProjectNode;
import edu.neu.promotion.views.EntityFiller;

public class ProjectEntityFiller implements EntityFiller<ProjectNode> {

    private TextView debug;

    @Override
    public View generateView(Context context) {
        debug = new TextView(context);
        return debug;
    }

    @Override
    public void fill(ProjectNode entity, Object tag) {
        debug.setText(entity.projectName);
    }

    @Override
    public View getClickableView() {
        return null;
    }
}
