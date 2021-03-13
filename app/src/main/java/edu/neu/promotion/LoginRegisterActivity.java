package edu.neu.promotion;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.davidsoft.utils.JsonNode;

import edu.neu.promotion.components.Page;
import edu.neu.promotion.components.PageNavigatingActivity;
import edu.neu.promotion.pages.LoginPasswordPage;
import edu.neu.promotion.pages.LoginUseridPage;
import edu.neu.promotion.pages.RegisterBasicInfoPage;
import edu.neu.promotion.pages.RegisterExtraInfoPage;
import edu.neu.promotion.pages.RegisterPasswordPage;
import edu.neu.promotion.pages.RegisterVerifyCodePage;

public class LoginRegisterActivity extends PageNavigatingActivity {

    private String userId;
    private String userPassword;
    private String userName;
    private boolean userSex;
    private String userIdentify;
    private String userPhone;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        navigateForward(new LoginUseridPage(this));
    }

    @Override
    public void onPageNotify(Page who, int notifyCode, Object... args) {
        super.onPageNotify(who, notifyCode, args);
        if (who instanceof LoginUseridPage) {
            switch (notifyCode) {
                case LoginUseridPage.RESULT_REGISTER:
                    userId = (String) args[0];
                    navigateForward(new RegisterPasswordPage(this));
                    break;
                case LoginUseridPage.RESULT_LOGIN:
                    navigateForward(new LoginPasswordPage(this, args[0]));
                    break;
            }
        }
        else if (who instanceof RegisterPasswordPage) {
            userPassword = (String) args[0];
            navigateForward(new RegisterBasicInfoPage(this));
        }
        else if (who instanceof RegisterBasicInfoPage) {
            userName = (String) args[0];
            userSex = (boolean) args[1];
            userIdentify = (String) args[2];
            userPhone = (String) args[3];
            navigateForward(new RegisterExtraInfoPage(this));
        }
        else if (who instanceof RegisterExtraInfoPage) {
            navigateForward(new RegisterVerifyCodePage(
                    this,
                    userId, userPassword, userName, userSex, userIdentify, userPhone,
                    args[0], args[1], args[2]
            ));
        }
        else if (who instanceof RegisterVerifyCodePage) {
            switch (notifyCode) {
                case RegisterVerifyCodePage.RESULT_LOGIN_SUCCESS:
                    onLoginSuccess((JsonNode) args[0], (String)args[1]);
                    break;
                case RegisterVerifyCodePage.RESULT_REGISTER_SUCCESS:
                    navigateBackTo(0);
                    break;
            }
        }
        else if (who instanceof LoginPasswordPage) {
            onLoginSuccess((JsonNode) args[0], (String)args[1]);
        }
    }

    private void onLoginSuccess(JsonNode userInfo, String token) {
        StorageManager.setJson(this, StorageManager.USER_INFO, userInfo);
        StorageManager.setValue(this, StorageManager.TOKEN, token);
        StorageManager.setValue(this, StorageManager.TOKEN_TYPE, "role");
        setResult(RESULT_OK);
        finish();
    }
}
