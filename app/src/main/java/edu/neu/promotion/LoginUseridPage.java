package edu.neu.promotion;

import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.TextView;
import android.widget.Toast;

import edu.neu.promotion.R;
import edu.neu.promotion.ServerInterfaces;
import edu.neu.promotion.ServerInvoker;
import edu.neu.promotion.components.AlertDialog;
import edu.neu.promotion.components.BaseActivity;
import edu.neu.promotion.components.PageManager;
import edu.neu.promotion.components.RunTaskPage;
import edu.neu.promotion.enties.ServerResponseNode;
import edu.neu.promotion.views.ClearableEditText;

public class LoginUseridPage extends RunTaskPage {

    public static final int RESULT_LOGIN = 0;
    public static final int RESULT_REGISTER = 1;

    private static final int TASK_TRY_LOGIN = 1;

    private DialogInterface.OnClickListener registerConfirmDialogListener;
    private DialogInterface.OnCancelListener loadingDialogCancelListener;

    private AlertDialog loadingDialog;

    private ClearableEditText useridEdit;
    private TextView nextButton;

    private String inputtedUserId;

    public LoginUseridPage(PageManager pageManager, Object... args) {
        super(pageManager, args);
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        setActionbarStyle(BaseActivity.ACTIONBAR_STYLE_NO_BACK);

        registerConfirmDialogListener = (dialog, which) -> {
            dialog.dismiss();
            if (which == DialogInterface.BUTTON_POSITIVE) {
                notifyParent(RESULT_REGISTER, inputtedUserId);
            }
        };
        loadingDialogCancelListener = dialog -> cancelTask(TASK_TRY_LOGIN);

        setContentView(R.layout.page_login_userid);
        useridEdit = findViewById(R.id.useridEdit);
        useridEdit.setTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                nextButton.setEnabled(s.length() > 0);
            }
        });
        nextButton = findViewById(R.id.nextButton);
        nextButton.setOnClickListener(v -> onTryLogin());
    }

    @Override
    protected void onTaskBegin(int requestCode) {
        loadingDialog = AlertDialog.Builder.getCenter(getContext())
                .setLoading()
                .setOnCancelListener(loadingDialogCancelListener)
                .show();
    }

    @Override
    protected void onTaskFailed(int requestCode, int currentRetryTimes) {
        if (currentRetryTimes == 0) {
            Toast.makeText(getContext(), R.string.toast_network_error, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onTaskFinish(int requestCode) {
        loadingDialog.cancel();
    }

    @Override
    protected void onTaskResult(int requestCode, Object result) {
        ServerResponseNode response;
        switch (requestCode) {
            case TASK_TRY_LOGIN:
                response = ServerInterfaces.analyseCommonContent((ServerInvoker.InvokeResult) result);
                switch (response.code) {
                    case ServerInterfaces.RESULT_CODE_USER_NOT_EXISTS:
                        AlertDialog.Builder.getBottom(getContext())
                                .setTitle(R.string.register_confirm_title)
                                .setButton(DialogInterface.BUTTON_POSITIVE, R.string.resume, true)
                                .setButton(DialogInterface.BUTTON_NEGATIVE, R.string.back, true)
                                .setOnDialogButtonClickListener(registerConfirmDialogListener)
                                .setMessage(getString(R.string.register_confirm_content, inputtedUserId))
                                .show();
                        break;
                    case ServerInterfaces.RESULT_CODE_WRONG_PASSWORD:
                        notifyParent(RESULT_LOGIN, inputtedUserId);
                        break;
                    default:
                        Toast.makeText(getContext(), response.data, Toast.LENGTH_SHORT).show();
                        break;
                }
                break;
        }
    }

    private void onTryLogin() {
        inputtedUserId = useridEdit.getEditText().getText().toString();
        runTask(ServerInterfaces.adminLogin(inputtedUserId, "d41d8cd98f00b204e9800998ecf8427e"), TASK_TRY_LOGIN);
    }
}