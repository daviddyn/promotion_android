package edu.neu.promotion.pages;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.davidsoft.utils.JsonNode;

import edu.neu.promotion.activities.MemberActivity;
import edu.neu.promotion.activities.ProjectActivity;
import edu.neu.promotion.R;
import edu.neu.promotion.ServerInterfaces;
import edu.neu.promotion.ServerInvoker;
import edu.neu.promotion.components.BaseActivity;
import edu.neu.promotion.components.PageManager;
import edu.neu.promotion.enties.AdminPowerNode;
import edu.neu.promotion.enties.ServerResponseNode;
import edu.neu.promotion.views.FixedGridLayout;

public class HomePage extends TokenRunNetworkTaskPage {

    private static final int TASK_LIST_USER_POWERS = 1;

    private FixedGridLayout functionGridView;

    public HomePage(PageManager pageManager, Object... args) {
        super(pageManager, args);
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        setActionbarStyle(BaseActivity.ACTIONBAR_STYLE_NO_BACK);
        setTitle(R.string.home_home_page);

        setContentView(R.layout.page_home);
        functionGridView = findViewById(R.id.functionGridView);

        runTask(ServerInterfaces.Power.getPowerList(getToken()), TASK_LIST_USER_POWERS);
    }

    private void loadFunctions(AdminPowerNode[] powers) {
        View.OnClickListener onClickListener = v -> {
            switch (((String) v.getTag())) {
                case "p_02_01":
                    startActivity(new Intent(getContext(), ProjectActivity.class));
                    break;
                case "p_01_04":
                    startActivity(new Intent(getContext(), MemberActivity.class));
                    break;
            }
        };
        View buttonItem;
        ImageView buttonImage;
        for (AdminPowerNode n : powers) {
            switch (n.powerId) {
                case "p_02_01":
                    getLayoutInflater().inflate(R.layout.item_home_page_function, functionGridView);
                    buttonItem = functionGridView.getChildAt(functionGridView.getChildCount() - 1);
                    buttonItem.setTag(n.powerId);
                    buttonItem.setOnClickListener(onClickListener);

                    buttonImage = buttonItem.findViewById(R.id.functionIcon);
                    buttonImage.setBackground(getDrawable(R.drawable.button_function1_press));
                    buttonImage.setImageResource(R.drawable.ic_function_project);
                    ((TextView) buttonItem.findViewById(R.id.functionName)).setText(R.string.home_project);
                    break;
                case "p_01_04":
                    getLayoutInflater().inflate(R.layout.item_home_page_function, functionGridView);
                    buttonItem = functionGridView.getChildAt(functionGridView.getChildCount() - 1);
                    buttonItem.setTag(n.powerId);
                    buttonItem.setOnClickListener(onClickListener);

                    buttonImage = buttonItem.findViewById(R.id.functionIcon);
                    buttonImage.setBackground(getDrawable(R.drawable.button_function2_press));
                    buttonImage.setImageResource(R.drawable.ic_function_member);
                    ((TextView) buttonItem.findViewById(R.id.functionName)).setText(R.string.home_my_member);
                    break;
            }
        }
    }

    @Override
    protected void onTaskBegin(int requestCode) {
        super.onTaskBegin(requestCode);
        switch (requestCode) {
            case TASK_LIST_USER_POWERS:
                toLoadingState();
                break;
        }
    }

    @Override
    protected void onTaskResult(int requestCode, Object result) {
        super.onTaskResult(requestCode, result);
        ServerResponseNode response = ServerInterfaces.analyseCommonContent((ServerInvoker.InvokeResult) result);
        switch (requestCode) {
            case TASK_LIST_USER_POWERS:
                if (response.code == ServerInterfaces.RESULT_CODE_SUCCESS) {
                    loadFunctions(JsonNode.toObject(response.object, AdminPowerNode[].class));
                }
                toNormalState();
                break;
        }
    }

    @Override
    protected void onTaskRetryFailed(int requestCode) {
        super.onTaskRetryFailed(requestCode);
        switch (requestCode) {
            case TASK_LIST_USER_POWERS:
                toErrorState();
                break;
        }
    }

    @Override
    protected void onErrorStateRetry() {
        super.onErrorStateRetry();
        retryTask(TASK_LIST_USER_POWERS);
    }
}
