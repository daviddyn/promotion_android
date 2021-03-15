package edu.neu.promotion;

import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.TextView;
import android.widget.Toast;

import com.davidsoft.utils.MessageDigest5;

import java.nio.charset.StandardCharsets;

import edu.neu.promotion.CacheManager;
import edu.neu.promotion.R;
import edu.neu.promotion.ServerInterfaces;
import edu.neu.promotion.ServerInvoker;
import edu.neu.promotion.StorageManager;
import edu.neu.promotion.components.AlertDialog;
import edu.neu.promotion.components.BaseActivity;
import edu.neu.promotion.components.PageManager;
import edu.neu.promotion.components.RunNetworkTaskPage;
import edu.neu.promotion.components.RunTaskPage;
import edu.neu.promotion.enties.ServerResponseNode;
import edu.neu.promotion.views.PasswordEditText;

public class LoginPasswordPage extends RunNetworkTaskPage {

    private static final int TASK_LOGIN = 1;

    private final String userId;

    private AlertDialog loadingDialog;
    private DialogInterface.OnCancelListener loadingDialogCancelListener;

    private PasswordEditText passwordEdit;
    private TextView nextButton;

    public LoginPasswordPage(PageManager pageManager, Object... args) {
        super(pageManager, args);
        userId = (String) args[0];
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        setActionbarStyle(BaseActivity.ACTIONBAR_STYLE_NO_TITLE);

        loadingDialogCancelListener = dialog -> {
            cancelTask(TASK_LOGIN);
        };

        setContentView(R.layout.page_login_password);
        ((TextView) findViewById(R.id.subtitleTextView)).setText(getString(R.string.login_password_subtitle, userId));
        passwordEdit = findViewById(R.id.passwordEdit);
        passwordEdit.setTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                nextButton.setEnabled(s.length() > 0);
            }
        });
        nextButton = findViewById(R.id.nextButton);
        nextButton.setOnClickListener(v -> login());
    }

    private void login() {
        runTask(ServerInterfaces.adminLogin(
                userId, MessageDigest5.encode(passwordEdit.getEditText().getText().toString().getBytes(StandardCharsets.UTF_8), false)
        ), TASK_LOGIN);
    }

    @Override
    protected void onTaskBegin(int requestCode) {
        super.onTaskBegin(requestCode);
        loadingDialog = AlertDialog.Builder.getCenter(getContext()).setOnCancelListener(loadingDialogCancelListener).setLoading().show();
    }

    @Override
    protected void onTaskFinish(int requestCode) {
        super.onTaskFinish(requestCode);
        loadingDialog.cancel();
    }

    @Override
    protected void onTaskResult(int requestCode, Object result) {
        super.onTaskResult(requestCode, result);
        ServerResponseNode response = ServerInterfaces.analyseCommonContent((ServerInvoker.InvokeResult) result);
        if (response.code == ServerInterfaces.RESULT_CODE_SUCCESS) {
            notifyParent(0, response.object.getField("admin"), response.object.getField("token").getValue());
        }
    }
}
