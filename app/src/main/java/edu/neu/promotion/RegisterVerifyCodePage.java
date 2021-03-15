package edu.neu.promotion;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.davidsoft.utils.MessageDigest5;

import java.nio.charset.StandardCharsets;

import edu.neu.promotion.components.AlertDialog;
import edu.neu.promotion.components.BaseActivity;
import edu.neu.promotion.components.PageManager;
import edu.neu.promotion.components.RunNetworkTaskPage;
import edu.neu.promotion.enties.DictionaryItemNode;
import edu.neu.promotion.enties.ServerResponseNode;
import edu.neu.promotion.views.CaptchaInputView;

public class RegisterVerifyCodePage extends RunNetworkTaskPage {

    public static final int RESULT_REGISTER_SUCCESS = 0;
    public static final int RESULT_LOGIN_SUCCESS = 1;

    private static final int TASK_GET_IMAGE = 1;
    private static final int TASK_REGISTER = 2;
    private static final int TASK_LOGIN = 3;

    private Drawable reloadDrawable;
    private AnimationDrawable loadingDrawable;

    private AlertDialog loadingDialog;
    private DialogInterface.OnCancelListener loadingDialogCancelListener;

    private BitmapFactory.Options px2dipOptions;

    private ImageView verifyImageView;
    private CaptchaInputView verifyEdit;
    private TextView changeImageButton;

    private final String userId;
    private final String userPassword;
    private final String userName;
    private final boolean userSex;
    private final String userIdentify;
    private final String userPhone;
    private final DictionaryItemNode userDepartment;
    private final String userPosition;
    private final String userDegree;
    private String aesCode;

    public RegisterVerifyCodePage(PageManager pageManager, Object... args) {
        super(pageManager, args);
        userId = (String) args[0];
        userPassword = (String) args[1];
        userName = (String) args[2];
        userSex = (boolean) args[3];
        userIdentify = (String) args[4];
        userPhone = (String) args[5];
        userDepartment = (DictionaryItemNode) args[6];
        userPosition = (String) args[7];
        userDegree = (String) args[8];
    }

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate() {
        super.onCreate();
        setActionbarStyle(BaseActivity.ACTIONBAR_STYLE_NO_TITLE);

        View.OnClickListener onClickListener = v -> {
            if (v == changeImageButton) {
                loadImage();
            }
            else if (v == verifyImageView) {
                retryTask(TASK_GET_IMAGE);
            }
        };

        reloadDrawable = getDrawable(R.drawable.ic_reload);
        loadingDrawable = (AnimationDrawable) getDrawable(R.drawable.bg_loading);

        loadingDialogCancelListener = dialog -> {
            if (isRunning(TASK_REGISTER)) {
                cancelTask(TASK_REGISTER);
                loadImage();
            }
            else if (isRunning(TASK_LOGIN)) {
                cancelTask(TASK_LOGIN);
                returnRegisterSuccess();
            }
        };

        px2dipOptions = new BitmapFactory.Options();
        px2dipOptions.inDensity = DisplayMetrics.DENSITY_DEFAULT;
        px2dipOptions.inTargetDensity = getResource().getDisplayMetrics().densityDpi;

        setContentView(R.layout.page_register_verify_code);
        verifyImageView = findViewById(R.id.verifyImageView);
        verifyImageView.setOnClickListener(onClickListener);
        verifyEdit = findViewById(R.id.verifyEdit);
        verifyEdit.setInputContent(4, false);
        verifyEdit.setOnInputFinishListener((who, content) -> register(content));
        changeImageButton = findViewById(R.id.changeImageButton);
        changeImageButton.setOnClickListener(onClickListener);

        loadImage();
    }

    @Override
    protected void onDestroy() {
        loadingDrawable.stop();
        super.onDestroy();
    }

    private void loadImage() {
        runTask(ServerInterfaces.verifyCode(), TASK_GET_IMAGE);
    }

    private void register(String verifyCode) {
        runTask(ServerInterfaces.adminSign(
                userId, MessageDigest5.encode(userPassword.getBytes(StandardCharsets.UTF_8), false),
                userName, userSex ? "女" : "男", userPhone, userDepartment == null ? "" : userDepartment.dictionaryId, userPosition, userDegree, userIdentify,
                verifyCode, aesCode
        ), TASK_REGISTER);
    }

    private void login() {
        runTask(ServerInterfaces.adminLogin(
                userId, MessageDigest5.encode(userPassword.getBytes(StandardCharsets.UTF_8), false)
        ), TASK_LOGIN);
    }

    @Override
    protected void onTaskBegin(int requestCode) {
        super.onTaskBegin(requestCode);
        switch (requestCode) {
            case TASK_GET_IMAGE:
                verifyEdit.setEnabled(false);
                changeImageButton.setEnabled(false);
                changeImageButton.setTextColor(getColor(R.color.split_bar));
                verifyImageView.setEnabled(false);
                verifyImageView.setImageDrawable(loadingDrawable);
                loadingDrawable.start();
                break;
            case TASK_REGISTER:
                loadingDialog = AlertDialog.Builder.getCenter(getContext()).setOnCancelListener(loadingDialogCancelListener).setLoading().show();
                break;
        }
    }

    @Override
    protected void onTaskFinish(int requestCode) {
        super.onTaskFinish(requestCode);
        switch (requestCode) {
            case TASK_GET_IMAGE:
                loadingDrawable.stop();
                break;
            case TASK_LOGIN:
                loadingDialog.cancel();
                break;
        }
    }

    @Override
    protected void onTaskFailed(int requestCode, int currentRetryTimes) {
        super.onTaskFailed(requestCode, currentRetryTimes);
        switch (requestCode) {
            case TASK_GET_IMAGE:
                verifyImageView.setImageDrawable(reloadDrawable);
                verifyImageView.setEnabled(true);
                break;
            case TASK_REGISTER:
                loadingDialog.cancel();
                loadImage();
                break;
            case TASK_LOGIN:
                returnRegisterSuccess();
                break;
        }
    }

    @Override
    protected void onTaskResult(int requestCode, Object result) {
        ServerInvoker.InvokeResult invokeResult = (ServerInvoker.InvokeResult) result;
        byte[] imageData;
        ServerResponseNode response;
        switch (requestCode) {
            case TASK_GET_IMAGE:
                aesCode = invokeResult.getResponseHeader("VerifyCode");
                imageData = invokeResult.getContent();
                verifyEdit.setEnabled(true);
                verifyEdit.clear();
                verifyEdit.requestFocus();
                changeImageButton.setEnabled(true);
                changeImageButton.setTextColor(getColor(R.color.primary));
                verifyImageView.setEnabled(false);
                verifyImageView.setImageBitmap(BitmapFactory.decodeByteArray(imageData, 0, imageData.length, px2dipOptions));
                break;
            case TASK_REGISTER:
                response = ServerInterfaces.analyseCommonContent((ServerInvoker.InvokeResult) result);
                switch (response.code) {
                    case ServerInterfaces.RESULT_CODE_SUCCESS:
                    case ServerInterfaces.RESULT_CODE_USER_ALREADY_EXISTS:
                        login();
                        break;
                    default:
                        super.onTaskResult(requestCode, result);
                        break;
                }
                break;
            case TASK_LOGIN:
                response = ServerInterfaces.analyseCommonContent((ServerInvoker.InvokeResult) result);
                if (response.code == ServerInterfaces.RESULT_CODE_SUCCESS) {
                    notifyParent(RESULT_LOGIN_SUCCESS, response.object.getField("admin"), response.object.getField("token").getValue());
                }
                else {
                    returnRegisterSuccess();
                }
                break;
        }
    }

    private void returnRegisterSuccess() {
        Toast.makeText(getContext(), R.string.toast_register_success, Toast.LENGTH_SHORT).show();
        notifyParent(RESULT_REGISTER_SUCCESS);
    }
}
