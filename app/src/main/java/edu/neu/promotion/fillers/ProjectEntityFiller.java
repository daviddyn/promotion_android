package edu.neu.promotion.fillers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import edu.neu.promotion.R;
import edu.neu.promotion.enties.ProjectNode;
import edu.neu.promotion.views.EntityFiller;

public class ProjectEntityFiller implements EntityFiller<ProjectNode> {

    private TextView nameView;
    private TextView leftSubTextView;
    private TextView rightSubTextView;
    private TextView stateTextView;

    @Override
    public View generateView(Context context) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.item_member, null);
        nameView = rootView.findViewById(R.id.nameView);
        leftSubTextView = rootView.findViewById(R.id.leftSubTextView);
        rightSubTextView = rootView.findViewById(R.id.rightSubTextView);
        stateTextView = rootView.findViewById(R.id.stateTextView);
        return rootView;
    }

    @Override
    public void fill(ProjectNode entity, Object tag) {
        nameView.setText(entity.projectName);
        leftSubTextView.setText(entity.projectGroupObj.groupName);
        rightSubTextView.setText(entity.projectGroupObj.groupName);
    }

    @Override
    public View getClickableView() {
        return null;
    }
}
