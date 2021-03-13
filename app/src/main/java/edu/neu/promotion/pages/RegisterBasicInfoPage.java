package edu.neu.promotion.pages;

import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import edu.neu.promotion.R;
import edu.neu.promotion.components.BaseActivity;
import edu.neu.promotion.components.Page;
import edu.neu.promotion.components.PageManager;
import edu.neu.promotion.views.ClearableEditText;
import edu.neu.promotion.views.IdentifyNumberInputFilter;
import edu.neu.promotion.views.PhoneInputFilter;

public class RegisterBasicInfoPage extends Page {

    public RegisterBasicInfoPage(PageManager pageManager, Object... args) {
        super(pageManager, args);
    }

    private ClearableEditText nameEdit;
    private TextView maleButton;
    private TextView femaleButton;
    private ClearableEditText identifyEdit;
    private ClearableEditText phoneEdit;
    private TextView nextButton;

    private boolean sex;

    @Override
    protected void onCreate() {
        super.onCreate();
        setActionbarStyle(BaseActivity.ACTIONBAR_STYLE_NO_TITLE);

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                testNextButtonEnabled();
            }
        };

        View.OnClickListener onClickListener = v -> {
            if (v == maleButton) {
                maleButton.setBackgroundResource(R.drawable.button_primary_normal_background);
                maleButton.setTextColor(getColor(R.color.on_primary));
                femaleButton.setBackgroundResource(R.drawable.check_button_female_unchecked);
                femaleButton.setTextColor(getColor(R.color.female));
                sex = false;
            }
            else if (v == femaleButton) {
                maleButton.setBackgroundResource(R.drawable.check_button_primary_unchecked);
                maleButton.setTextColor(getColor(R.color.primary));
                femaleButton.setBackgroundResource(R.drawable.button_female_background);
                femaleButton.setTextColor(getColor(R.color.on_primary));
                sex = true;
            }
            else if (v == nextButton) {
                onNextStep();
            }
        };

        setContentView(R.layout.page_register_basic);
        nameEdit = findViewById(R.id.nameEdit);
        nameEdit.setTextChangedListener(textWatcher);
        maleButton = findViewById(R.id.maleButton);
        maleButton.setOnClickListener(onClickListener);
        femaleButton = findViewById(R.id.femaleButton);
        femaleButton.setOnClickListener(onClickListener);
        identifyEdit = findViewById(R.id.identifyEdit);
        identifyEdit.getEditText().setFilters(new InputFilter[]{new IdentifyNumberInputFilter()});
        phoneEdit = findViewById(R.id.phoneEdit);
        phoneEdit.getEditText().setFilters(new InputFilter[]{new PhoneInputFilter()});
        phoneEdit.setTextChangedListener(textWatcher);
        nextButton = findViewById(R.id.nextButton);
        nextButton.setOnClickListener(onClickListener);
    }

    private static boolean checkIdentifyInput(String identifyInput) {
        if (identifyInput.length() != 18) {
            return false;
        }
        char c;
        for (int i = 0; i < 17; i++) {
            c = identifyInput.charAt(i);
            if (c < '0' || c > '9') {
                return false;
            }
        }
        c = identifyInput.charAt(17);
        return c >= '0' && c <= '9' || c == 'X';
    }

    private void testNextButtonEnabled() {
        nextButton.setEnabled(
                nameEdit.getEditText().length() > 0 && phoneEdit.getEditText().length() == 11
        );
    }

    private void onNextStep() {
        String userIdentify = identifyEdit.getEditText().getText().toString();
        if (userIdentify.length() > 0) {
            if (!checkIdentifyInput(userIdentify)) {
                Toast.makeText(getContext(), R.string.toast_identify_number_error, Toast.LENGTH_SHORT).show();
                identifyEdit.requestFocus();
                return;
            }
        }
        String userName = nameEdit.getEditText().getText().toString();
        String userPhone = phoneEdit.getEditText().getText().toString();
        notifyParent(0, userName, sex, userIdentify, userPhone);
    }
}
