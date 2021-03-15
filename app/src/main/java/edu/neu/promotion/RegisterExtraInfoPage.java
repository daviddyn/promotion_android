package edu.neu.promotion;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.davidsoft.utils.JsonNode;

import java.util.ArrayList;

import edu.neu.promotion.R;
import edu.neu.promotion.SearchSelectItemActivity;
import edu.neu.promotion.ServerInterfaces;
import edu.neu.promotion.ServerInvoker;
import edu.neu.promotion.components.AlertDialog;
import edu.neu.promotion.components.BaseActivity;
import edu.neu.promotion.components.PageManager;
import edu.neu.promotion.components.RunNetworkTaskPage;
import edu.neu.promotion.enties.DictionaryItemNode;
import edu.neu.promotion.enties.ServerResponseNode;
import edu.neu.promotion.views.ClearableEditText;

public class RegisterExtraInfoPage extends RunNetworkTaskPage {

    private static final int TASK_GET_COLLEGE = 1;

    private static final int ACTIVITY_REQUEST_SELECT_DEPARTMENT = 1;

    private DictionaryItemNode[] departments;
    private ArrayList<String> departmentDisplays;

    private DialogInterface.OnClickListener onDegreeDialogChoiceListener;
    private DialogInterface.OnClickListener onDegreeDialogListener;
    private TextView departmentEdit;
    private RadioGroup studentRadioGroup;
    private View positionTitleView;
    private ClearableEditText positionEdit;
    private View degreeTitleView;
    private TextView degreeEdit;
    private TextView nextButton;

    private DictionaryItemNode selectedDepartment;
    private boolean studentMode;
    private int selectedDegree;
    private int editingDegree;

    public RegisterExtraInfoPage(PageManager pageManager, Object... args) {
        super(pageManager, args);
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        setActionbarStyle(BaseActivity.ACTIONBAR_STYLE_NO_TITLE);

        runTask(ServerInterfaces.Dictionary.getDictionaryItems("COLLEGE"), TASK_GET_COLLEGE);
    }

    private void loadMainViews() {
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
            if (v == departmentEdit) {
                Intent intent = new Intent(getContext(), SearchSelectItemActivity.class);
                intent.putExtra(SearchSelectItemActivity.REQUEST_EXTRA_TITLE, getString(R.string.register_extra_select_department_title));
                intent.putExtra(SearchSelectItemActivity.REQUEST_EXTRA_ITEMS, departmentDisplays);
                startActivityForResult(intent, ACTIVITY_REQUEST_SELECT_DEPARTMENT);
            }
            else if (v == degreeEdit) {
                editingDegree = selectedDegree;
                AlertDialog.Builder.getBottom(getContext())
                        .setChoices(new String[] {getString(R.string.degree_undergraduate), getString(R.string.degree_graduate), getString(R.string.degree_postgraduate)}, editingDegree, onDegreeDialogChoiceListener)
                        .setButton(DialogInterface.BUTTON_POSITIVE, R.string.ok, true)
                        .setButton(DialogInterface.BUTTON_NEGATIVE, R.string.cancel, true)
                        .setOnDialogButtonClickListener(onDegreeDialogListener)
                        .show();
            }
            else if (v == nextButton) {
                onNextStep();
            }
        };
        onDegreeDialogChoiceListener = (dialog, which) -> editingDegree = which;
        onDegreeDialogListener = (dialog, which) -> {
            dialog.dismiss();
            if (which == DialogInterface.BUTTON_POSITIVE) {
                selectedDegree = editingDegree;
                switch (selectedDegree) {
                    case 0:
                        degreeEdit.setText(R.string.degree_undergraduate);
                        break;
                    case 1:
                        degreeEdit.setText(R.string.degree_graduate);
                        break;
                    case 2:
                        degreeEdit.setText(R.string.degree_postgraduate);
                        break;
                }
            }
        };

        setContentView(R.layout.page_register_extra);

        departmentEdit = findViewById(R.id.departmentEdit);
        departmentEdit.setOnClickListener(onClickListener);
        studentRadioGroup = findViewById(R.id.studentRadioGroup);
        studentRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.studentRadio) {
                toStudentMode();
            }
            else {
                toTeacherMode();
            }
        });
        positionTitleView = findViewById(R.id.positionTitleView);
        positionEdit = findViewById(R.id.positionEdit);
        positionEdit.setTextChangedListener(textWatcher);
        degreeTitleView = findViewById(R.id.degreeTitleView);
        degreeEdit = findViewById(R.id.degreeEdit);
        degreeEdit.setOnClickListener(onClickListener);
        nextButton = findViewById(R.id.nextButton);
        nextButton.setOnClickListener(onClickListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            selectedDepartment = departments[data.getIntExtra(SearchSelectItemActivity.RESULT_EXTRA_POSITION, 0)];
            departmentEdit.setText(selectedDepartment.dictionaryName);
        }
    }

    @Override
    protected void onTaskBegin(int requestCode) {
        super.onTaskBegin(requestCode);
        toLoadingState();
    }

    @Override
    protected void onTaskResult(int requestCode, Object result) {
        super.onTaskResult(requestCode, result);
        ServerResponseNode response = ServerInterfaces.analyseCommonContent((ServerInvoker.InvokeResult) result);
        if (response.code == ServerInterfaces.RESULT_CODE_SUCCESS) {
            departments = JsonNode.toObject(response.object, DictionaryItemNode[].class);
            departmentDisplays = new ArrayList<>(departments.length);
            for (DictionaryItemNode node : departments) {
                departmentDisplays.add(node.dictionaryName);
            }
            loadMainViews();
        }
        toNormalState();
    }

    @Override
    protected void onTaskRetryFailed(int requestCode) {
        super.onTaskRetryFailed(requestCode);
        toErrorState();
    }

    @Override
    protected void onErrorStateRetry() {
        super.onErrorStateRetry();
        retryTask(TASK_GET_COLLEGE);
    }

    private void toStudentMode() {
        positionTitleView.setVisibility(View.GONE);
        positionEdit.setVisibility(View.GONE);
        degreeTitleView.setVisibility(View.VISIBLE);
        degreeEdit.setVisibility(View.VISIBLE);
        studentMode = true;
        testNextButtonEnabled();
    }

    private void toTeacherMode() {
        positionTitleView.setVisibility(View.VISIBLE);
        positionEdit.setVisibility(View.VISIBLE);
        degreeTitleView.setVisibility(View.GONE);
        degreeEdit.setVisibility(View.GONE);
        studentMode = false;
        testNextButtonEnabled();
    }

    private void testNextButtonEnabled() {
        if (!studentMode) {
            if (positionEdit.getEditText().length() == 0) {
                nextButton.setEnabled(false);
                return;
            }
        }
        nextButton.setEnabled(true);
    }

    private void onNextStep() {
        notifyParent(
                0,
                selectedDepartment,
                studentMode ? "学生" : positionEdit.getEditText().getText().toString(),
                studentMode ? degreeEdit.getText().toString() : ""
        );
    }
}
