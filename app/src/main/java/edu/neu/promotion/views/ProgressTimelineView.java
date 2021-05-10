package edu.neu.promotion.views;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.widget.ImageViewCompat;

import edu.neu.promotion.R;

public class ProgressTimelineView extends LinearLayout {

    public static final int TYPE_PRIMARY = 0;
    public static final int TYPE_IN_PROGRESS = 1;
    public static final int TYPE_CRITICAL = 2;
    public static final int TYPE_POSITIVE = 3;

    public ProgressTimelineView(Context context) {
        super(context);
        construct();
    }

    public ProgressTimelineView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        construct();
    }

    public ProgressTimelineView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        construct();
    }

    private LayoutInflater layoutInflater;

    private void construct() {
        setOrientation(VERTICAL);
        layoutInflater = LayoutInflater.from(getContext());
    }

    public void appendTimelineItem(int type, String description, String subText1, String subText2) {
        int childCount = getChildCount();
        layoutInflater.inflate(R.layout.item_examine_progress, this);
        fillExamineProgressItem(getChildAt(childCount), type, description, subText1, subText2);
    }

    public void finishAppendTimeLineItem() {
        int childCount = getChildCount();
        if (childCount == 0) {
            return;
        }
        getChildAt(childCount - 1).findViewById(R.id.additionalArea).setVisibility(View.GONE);
    }

    private void fillExamineProgressItem(View rootView, int type, String description, String subText1, String subText2) {
        TextView examineResultView = rootView.findViewById(R.id.examineResultView);
        int color;
        Resources resources = getResources();
        switch (type) {
            case TYPE_PRIMARY:
                ((ImageView) rootView.findViewById(R.id.dotView)).setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.ic_progress_primary, null));
                color = resources.getColor(R.color.primary);
                break;
            case TYPE_IN_PROGRESS:
                ((ImageView) rootView.findViewById(R.id.dotView)).setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.ic_progress_in_progress, null));
                color = resources.getColor(R.color.gray);
                break;
            case TYPE_CRITICAL:
                ((ImageView) rootView.findViewById(R.id.dotView)).setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.ic_progress_critical, null));
                color = resources.getColor(R.color.critical);
                break;
            default:
                ((ImageView) rootView.findViewById(R.id.dotView)).setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.ic_progress_positive, null));
                color = resources.getColor(R.color.positive);
                break;
        }
        examineResultView.setTextColor(color);
        examineResultView.setText(description);
        ImageViewCompat.setImageTintList(rootView.findViewById(R.id.lineView), ColorStateList.valueOf(color));
        if (subText1 != null) {
            ((TextView) rootView.findViewById(R.id.examineOperatorView)).setText(subText1);
            if (subText2 != null) {
                ((TextView) rootView.findViewById(R.id.examineDateView)).setText(subText2);
            }
        }
    }
}
