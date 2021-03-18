package edu.neu.promotion;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;

import edu.neu.promotion.R;
import edu.neu.promotion.components.BaseActivity;
import edu.neu.promotion.components.Page;
import edu.neu.promotion.components.PageManager;
import edu.neu.promotion.views.PasswordEditText;
import edu.neu.promotion.views.StepView;
import edu.neu.promotion.views.VibratingTextView;

public class RegisterPasswordPage extends Page {

    private StepView passwordStepper;
    private PasswordEditText passwordEdit;
    private VibratingTextView passwordRequirementTip;
    private PasswordEditText passwordAgainEdit;
    private VibratingTextView passwordMatchTip;
    private TextView nextButton;

    private int currentStep;

    public RegisterPasswordPage(PageManager pageManager, Object... args) {
        super(pageManager, args);
    }

    @Override
    protected void onCreate() {
        super.onCreate();

        setActionbarStyle(BaseActivity.ACTIONBAR_STYLE_NO_TITLE);

        setContentView(R.layout.page_register_password);

        passwordStepper = findViewById(R.id.passwordStepper);
        passwordEdit = findViewById(R.id.passwordEdit);
        passwordEdit.setTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (currentStep == 0) {
                    nextButton.setEnabled(s.length() >= 8);
                }
            }
        });
        passwordRequirementTip = findViewById(R.id.passwordRequirementTip);
        passwordAgainEdit = findViewById(R.id.passwordAgainEdit);
        passwordAgainEdit.setTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (currentStep == 1) {
                    nextButton.setEnabled(s.length() >= 8);
                }
            }
        });
        passwordMatchTip = findViewById(R.id.passwordMatchTip);
        nextButton = findViewById(R.id.nextButton);
        nextButton.setOnClickListener(v -> onNextStep());
    }

    @Override
    protected void onSwitchedOn() {
        super.onSwitchedOn();
        onGoBack();
    }

    private int getCharType(char c) {
        if ('0' <= c && c <= '9') {
            return 1;
        }
        else if ('A' <= c && c <= 'Z' || 'a' <= c && c <= 'z') {
            return 2;
        }
        else {
            return 0;
        }
    }

    private boolean checkPasswordInput(String input) {
        int firstCharType = getCharType(input.charAt(0));
        if (firstCharType == 0) {
            return true;
        }
        for (int i = 1; i < input.length(); i++) {
            int charType = getCharType(input.charAt(i));
            if (charType != firstCharType) {
                return true;
            }
        }
        return false;
    }

    private void onNextStep() {
        String input;
        switch (currentStep) {
            case 0:
                input = passwordEdit.getEditText().getText().toString();
                if (checkPasswordInput(input)) {
                    currentStep = 1;
                    passwordStepper.navigateTo(1);
                    nextButton.setEnabled(false);
                    passwordAgainEdit.getEditText().setText("");
                    passwordAgainEdit.requestFocus();
                    passwordMatchTip.setVisibility(View.INVISIBLE);
                }
                else {
                    passwordRequirementTip.setTextColor(getColor(R.color.critical));
                    passwordRequirementTip.startVibe();
                }
                break;
            case 1:
                if (passwordAgainEdit.getEditText().getText().toString().equals(passwordEdit.getEditText().getText().toString())) {
                    notifyParent(0, passwordEdit.getEditText().getText().toString());
                }
                else {
                    passwordMatchTip.setVisibility(View.VISIBLE);
                    passwordMatchTip.startVibe();
                }
                break;
        }
    }

    @Override
    protected boolean onGoBack() {
        if (currentStep == 0) {
            return false;
        }
        currentStep = 0;
        passwordEdit.getEditText().setText("");
        passwordEdit.toInvisibleMode();
        passwordRequirementTip.setTextColor(getColor(R.color.primary));
        nextButton.setEnabled(false);
        passwordStepper.navigateTo(0);
        passwordEdit.requestFocus();
        return true;
    }
}
