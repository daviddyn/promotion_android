package edu.neu.promotion.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import edu.neu.promotion.MyApplication;
import edu.neu.promotion.R;
import edu.neu.promotion.ServerInvoker;
import edu.neu.promotion.StorageManager;
import edu.neu.promotion.components.BaseActivity;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        if (!((MyApplication) getApplication()).firstRun) {
            showMainActivity();
            return;
        }

        ((MyApplication) getApplication()).firstRun = false;
        String version = "0.0.0";
        try {
            version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        ServerInvoker.initialize(getPackageName(), version);

        setContentView(R.layout.activity_welcome);
        ((TextView) findViewById(R.id.versionText)).setText(getResources().getString(R.string.app_version_name, version));

        getWindow().getDecorView().postDelayed(this::showMainActivity, 2500);
    }

    private void showMainActivity() {
        String tokenType = StorageManager.getValue(this, StorageManager.TOKEN_TYPE);
        if (tokenType == null) {
            startActivityForResult(new Intent(this, LoginRegisterActivity.class), 1);
        }
        else switch (tokenType) {
            case "role":
                startActivityForResult(new Intent(this, SelectRoleActivity.class), 2);
                break;
            case "normal":
                startActivityForResult(new Intent(this, MainActivity.class), 3);
                break;
            default:
                startActivityForResult(new Intent(this, LoginRegisterActivity.class), 1);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    //登录成功
                    startActivityForResult(new Intent(this, SelectRoleActivity.class), 2);
                }
                else {
                    finish();
                }
                break;
            case 2:
                switch (resultCode) {
                    case BaseActivity.RESULT_NEED_FINISH:
                        startActivityForResult(new Intent(this, LoginRegisterActivity.class), 1);
                        break;
                    case RESULT_OK:
                        startActivityForResult(new Intent(this, MainActivity.class), 3);
                        break;
                    default:
                        finish();
                        break;
                }
                break;
            default:
                if (resultCode == BaseActivity.RESULT_NEED_FINISH) {
                    if (data == null || !data.hasExtra("selectRole")) {
                        startActivityForResult(new Intent(this, LoginRegisterActivity.class), 1);
                    }
                    else {
                        startActivityForResult(new Intent(this, SelectRoleActivity.class), 2);
                    }
                }
                else {
                    finish();
                }
                break;
        }
    }
}
