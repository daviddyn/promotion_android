package edu.neu.promotion.pages;

import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.widget.ImageViewCompat;

import com.davidsoft.utils.JsonNode;

import edu.neu.promotion.R;
import edu.neu.promotion.ServerInterfaces;
import edu.neu.promotion.ServerInvoker;
import edu.neu.promotion.components.AlertDialog;
import edu.neu.promotion.components.BaseActivity;
import edu.neu.promotion.components.PageManager;
import edu.neu.promotion.enties.RoleNode;
import edu.neu.promotion.enties.ServerResponseNode;

public class CreateRolePage extends TokenRunNetworkTaskPage {

    public static final int RESULT_STEP_NEXT = 0;
    public static final int RESULT_CREATED = 1;

    private static final int TASK_LIST_ROLE = 1;
    private static final int TASK_APPLY_ROLE = 2;

    private RoleNode[] roles;
    private int selectedItem;

    private AlertDialog loadingDialog;
    private DialogInterface.OnCancelListener onLoadingDialogCancelListener;
    private View.OnClickListener onRoleButtonClickListener;
    private LinearLayout availableListView;
    private TextView nextButton;

    public CreateRolePage(PageManager pageManager, Object... args) {
        super(pageManager, args);
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        setActionbarStyle(BaseActivity.ACTIONBAR_STYLE_NO_TITLE);

        selectedItem = -1;

        onLoadingDialogCancelListener = dialog -> cancelTask(TASK_APPLY_ROLE);
        onRoleButtonClickListener = this::onRoleButtonClick;

        runTask(ServerInterfaces.Role.getApplicableRole(getToken()), TASK_LIST_ROLE);
    }

    private void loadMainViews() {
        setContentView(R.layout.page_create_role);
        nextButton = findViewById(R.id.nextButton);
        nextButton.setOnClickListener(v -> {
            if ("0".equals(roles[selectedItem].showGroup)) {
                runTask(ServerInterfaces.Role.applyRole(getToken(), roles[selectedItem].roleId, ""), TASK_APPLY_ROLE);
            }
            else {
                notifyParent(RESULT_STEP_NEXT, roles[selectedItem]);
            }
        });
        availableListView = findViewById(R.id.availableListView);
        for (int i = 0; i < roles.length; i++) {
            getLayoutInflater().inflate(R.layout.item_role, availableListView);
            View roleItemView = availableListView.getChildAt(i);
            setItemRole(roleItemView, i);
            setItemUnselected(roleItemView);
        }
    }

    private void setItemRole(View roleItemView, int position) {
        RoleNode role = roles[position];
        roleItemView.setTag(position);
        roleItemView.setOnClickListener(onRoleButtonClickListener);
        ImageView roleIconView = roleItemView.findViewById(R.id.roleIconView);
        roleItemView.findViewById(R.id.roleNameText).setVisibility(View.GONE);
        roleItemView.findViewById(R.id.stateTextView).setVisibility(View.GONE);
        switch (role.roleId) {
            case "role_monitor":
                roleIconView.setImageDrawable(getDrawable(R.drawable.ic_role_admin));
                break;
            case "role_professor":
                roleIconView.setImageDrawable(getDrawable(R.drawable.ic_role_professor));
                break;
            default:
                roleIconView.setImageDrawable(getDrawable(R.drawable.ic_role_member));
                break;
        }
        ((TextView) roleItemView.findViewById(R.id.groupNameText)).setText(role.roleName);
    }

    private void setItemSelected(View roleItemView) {
        roleItemView.setBackground(getDrawable(R.drawable.card_selected));
        ImageViewCompat.setImageTintList(roleItemView.findViewById(R.id.roleIconView), ColorStateList.valueOf(getColor(R.color.primary_lighter)));
        ((TextView) roleItemView.findViewById(R.id.groupNameText)).setTextColor(getColor(R.color.primary));
    }

    private void setItemUnselected(View roleItemView) {
        roleItemView.setBackground(getDrawable(R.drawable.card));
        ImageViewCompat.setImageTintList(roleItemView.findViewById(R.id.roleIconView), ColorStateList.valueOf(getColor(R.color.text_hint)));
        ((TextView) roleItemView.findViewById(R.id.groupNameText)).setTextColor(getColor(R.color.text_tertiary));
    }

    @Override
    protected void onTaskBegin(int requestCode) {
        super.onTaskBegin(requestCode);
        switch (requestCode) {
            case TASK_LIST_ROLE:
                toLoadingState();
                break;
            case TASK_APPLY_ROLE:
                loadingDialog = AlertDialog.Builder.getCenter(getContext()).setLoading().setOnCancelListener(onLoadingDialogCancelListener).show();
                break;
        }
    }

    @Override
    protected void onTaskFinish(int requestCode) {
        super.onTaskFinish(requestCode);
        switch (requestCode) {
            case TASK_LIST_ROLE:
                break;
            case TASK_APPLY_ROLE:
                loadingDialog.cancel();
                break;
        }
    }

    @Override
    protected void onTaskResult(int requestCode, Object result) {
        super.onTaskResult(requestCode, result);
        ServerResponseNode response = ServerInterfaces.analyseCommonContent((ServerInvoker.InvokeResult) result);
        switch (requestCode) {
            case TASK_LIST_ROLE:
                if (response.code == ServerInterfaces.RESULT_CODE_SUCCESS) {
                    roles = JsonNode.toObject(response.object, RoleNode[].class);
                    loadMainViews();
                }
                toNormalState();
                break;
            case TASK_APPLY_ROLE:
                if (response.code == ServerInterfaces.RESULT_CODE_SUCCESS) {
                    Toast.makeText(getContext(), R.string.create_role_success, Toast.LENGTH_SHORT).show();
                    notifyParent(RESULT_CREATED);
                }
                break;
        }
    }

    @Override
    protected void onTaskRetryFailed(int requestCode) {
        super.onTaskRetryFailed(requestCode);
        switch (requestCode) {
            case TASK_LIST_ROLE:
                toErrorState();
                break;
            case TASK_APPLY_ROLE:
                break;
        }
    }

    @Override
    protected void onErrorStateRetry() {
        super.onErrorStateRetry();
        retryTask(TASK_LIST_ROLE);
    }

    private void onRoleButtonClick(View view) {
        int newSelectedItem = (int) view.getTag();
        if  (newSelectedItem == selectedItem) {
            return;
        }
        if (selectedItem >= 0 && selectedItem < roles.length) {
            setItemUnselected(availableListView.getChildAt(selectedItem));
        }
        selectedItem = newSelectedItem;
        setItemSelected(availableListView.getChildAt(newSelectedItem));
        nextButton.setEnabled(true);
        if ("0".equals(roles[selectedItem].showGroup)) {
            nextButton.setText(R.string.create_group_ok);
        }
        else {
            nextButton.setText(R.string.next);
        }
    }
}
