package edu.neu.promotion.pages;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.TextView;

import edu.neu.promotion.R;
import edu.neu.promotion.components.BaseActivity;
import edu.neu.promotion.components.Page;
import edu.neu.promotion.components.PageManager;
import edu.neu.promotion.views.PasswordEditText;

public class OriginalPasswordPage extends Page {

    private PasswordEditText passwordEdit;
    private TextView nextButton;

    public OriginalPasswordPage(PageManager pageManager, Object... args) {
        super(pageManager, args);
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        setActionbarStyle(BaseActivity.ACTIONBAR_STYLE_NO_TITLE);

        setContentView(R.layout.page_change_password);
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
        nextButton.setOnClickListener(v -> notifyParent(0, passwordEdit.getEditText().getText().toString()));
    }

    @Override
    protected void onSwitchedOn() {
        super.onSwitchedOn();
        passwordEdit.toInvisibleMode();
        passwordEdit.getEditText().setText("");
    }
}
