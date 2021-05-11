package edu.neu.promotion.fillers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import edu.neu.promotion.R;
import edu.neu.promotion.enties.ProjectNode;
import edu.neu.promotion.utils.TimeDisplay;
import edu.neu.promotion.views.EntityFiller;

public class ProjectEntityFiller implements EntityFiller<ProjectNode> {

    public static final class Tag {
        public long currentTimeMills;
        public boolean currentIsGroupAdmin;
        public String currentAdminId;
    }

    private static final SimpleDateFormat dateParser = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);

    private ImageView iconView;
    private TextView nameView;
    private TextView stateView;
    private TextView leftSubTextView;
    private TextView rightSubTextView;
    private TextView actionView;

    @Override
    public View generateView(Context context) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.item_project, null);
        iconView = rootView.findViewById(R.id.iconView);
        nameView = rootView.findViewById(R.id.nameView);
        stateView = rootView.findViewById(R.id.stateView);
        leftSubTextView = rootView.findViewById(R.id.leftSubTextView);
        rightSubTextView = rootView.findViewById(R.id.rightSubTextView);
        actionView = rootView.findViewById(R.id.actionView);
        actionView.setTextColor(context.getResources().getColor(R.color.positive));
        actionView.setText(R.string.member_can_check);
        return rootView;
    }

    @Override
    public void fill(ProjectNode entity, Object tag) {
        Context context = nameView.getContext();
        nameView.setText(entity.projectName);
        switch (entity.projectState) {
            case "project_state_1":
                //草稿状态
                iconView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_project_draft));
                if (entity.projectAdmin.equals(((Tag) tag).currentAdminId)) {
                    actionView.setText(R.string.project_edit);
                    stateView.setText("");
                    rightSubTextView.setText("");
                }
                else {
                    actionView.setText("");
                    actionView.setVisibility(View.GONE);
                    stateView.setTextColor(context.getResources().getColor(R.color.function1));
                    stateView.setText(R.string.project_draft);
                    rightSubTextView.setText(entity.projectGroupObj.groupName);
                }
                break;
            case "project_state_6":
                //审批通过状态
                actionView.setText("");
                iconView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_project_available));
                rightSubTextView.setText(entity.projectGroupObj.groupName);
                break;
            case "project_state_7":
                //已结束状态
                actionView.setText("");
                iconView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_project_finished));
                stateView.setTextColor(context.getResources().getColor(R.color.critical));
                stateView.setText(R.string.project_finish);
                rightSubTextView.setText(entity.projectGroupObj.groupName);
                break;
            default:
                //审批中状态
                iconView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_project_examine));
                if (((Tag) tag).currentIsGroupAdmin && "project_state_2".equals(entity.projectState)) {
                    actionView.setText(R.string.member_can_check);
                    stateView.setText("");
                    rightSubTextView.setText("");
                }
                else {
                    actionView.setText("");
                    stateView.setTextColor(context.getResources().getColor(R.color.text_secondary));
                    stateView.setText(R.string.project_examine);
                    rightSubTextView.setText(entity.projectGroupObj.groupName);
                }
                break;
        }
        try {
            leftSubTextView.setText(context.getString(
                    R.string.project_creator_and_time,
                    entity.projectAdminObj.adminName,
                    TimeDisplay.showRelative(context, dateParser.parse(entity.projectMakeDateStr).getTime(), ((Tag) tag).currentTimeMills)
            ));
        } catch (ParseException ignored) {}
    }

    @Override
    public View getClickableView() {
        return null;
    }
}