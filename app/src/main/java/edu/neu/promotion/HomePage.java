package edu.neu.promotion;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.davidsoft.utils.JsonNode;

import java.util.ArrayList;

import edu.neu.promotion.components.AlertDialog;
import edu.neu.promotion.components.BaseActivity;
import edu.neu.promotion.components.Page;
import edu.neu.promotion.components.PageManager;
import edu.neu.promotion.enties.GroupNode;
import edu.neu.promotion.enties.PowerNode;
import edu.neu.promotion.enties.ServerResponseNode;

public class HomePage extends TokenRunNetworkTaskPage {

    private static final int TASK_GET_POWERS = 1;
    private static final int TASK_GET_NOTIFICATIONS = 2;

    private boolean firstSwitched;
    private View projectButtonArea;
    private ImageView projectButton;
    private View memberButtonArea;
    private ImageView memberButton;
    private View staticButtonArea;
    private ImageView staticButton;

    public HomePage(PageManager pageManager, Object... args) {
        super(pageManager, args);
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        firstSwitched = true;
        setActionbarStyle(BaseActivity.ACTIONBAR_STYLE_NO_BACK);
        setTitle(R.string.home_home_page);

        addActionbarButton(ContextCompat.getDrawable(getContext(), R.drawable.ic_actionbar_notification), R.string.system_notification);
        setActionbarButtonBadge(0, true);
        addActionbarButton(ContextCompat.getDrawable(getContext(), R.drawable.ic_actionbar_scan), R.string.scan);
    }

    @Override
    protected void onSwitchedOn() {
        super.onSwitchedOn();
        if (firstSwitched) {
            runTask(ServerInterfaces.Group.getAllGroup(getToken()), TASK_GET_POWERS);
            firstSwitched = false;
        }
    }

    private static boolean containsPower(PowerNode[] powers, String power) {
        if (powers == null) {
            return false;
        }
        for (PowerNode n : powers) {
            if (power.equals(n.powerId)) {
                return true;
            }
        }
        return false;
    }

    private void loadMainViews(PowerNode[] powers) {
        View.OnClickListener onClickListener = v -> {
            if (v == projectButton) {
                startActivity(new Intent(getContext(), ProjectActivity.class));
            }
            else if (v == memberButton) {
                startActivity(new Intent(getContext(), MemberActivity.class));
            }
            else if (v == staticButtonArea) {

            }
        };

        setContentView(R.layout.page_home);
        projectButtonArea = findViewById(R.id.projectButtonArea);
        projectButton = findViewById(R.id.projectButton);
        if (containsPower(powers, PowerNode.POWER_MANAGE_PROJECT)) {
            projectButton.setOnClickListener(onClickListener);
        }
        else {
            projectButtonArea.setVisibility(View.GONE);
        }
        memberButtonArea = findViewById(R.id.memberButtonArea);
        memberButton = findViewById(R.id.memberButton);
        if (containsPower(powers, PowerNode.POWER_MANAGE_MEMBER)) {
            memberButton.setOnClickListener(onClickListener);
        }
        else {
            memberButtonArea.setVisibility(View.GONE);
        }
        staticButtonArea = findViewById(R.id.staticButtonArea);
        staticButton = findViewById(R.id.staticButton);
        staticButtonArea.setVisibility(View.GONE);
        //staticButton.setOnClickListener(onClickListener);
    }

    @Override
    protected void onTaskBegin(int requestCode) {
        super.onTaskBegin(requestCode);
        switch (requestCode) {
            case TASK_GET_POWERS:
                toLoadingState();
                break;
            case TASK_GET_NOTIFICATIONS:
                break;
        }
    }

    @Override
    protected void onTaskResult(int requestCode, Object result) {
        super.onTaskResult(requestCode, result);
        ServerResponseNode response = ServerInterfaces.analyseCommonContent((ServerInvoker.InvokeResult) result);
        switch (requestCode) {
            case TASK_GET_POWERS:
                if (response.code == ServerInterfaces.RESULT_CODE_SUCCESS) {
                    loadMainViews(JsonNode.toObject(response.object, PowerNode[].class));
                }
                toNormalState();
                break;
            case TASK_GET_NOTIFICATIONS:
                break;
        }

    }

    @Override
    protected void onTaskRetryFailed(int requestCode) {
        super.onTaskRetryFailed(requestCode);
        switch (requestCode) {
            case TASK_GET_POWERS:
                toErrorState();
                break;
            case TASK_GET_NOTIFICATIONS:
                break;
        }
    }

    @Override
    protected void onErrorStateRetry() {
        super.onErrorStateRetry();
        retryTask(TASK_GET_POWERS);
    }
}
