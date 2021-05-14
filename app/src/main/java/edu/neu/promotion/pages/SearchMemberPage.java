package edu.neu.promotion.pages;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.davidsoft.utils.JsonNode;

import java.util.ArrayList;

import edu.neu.promotion.R;
import edu.neu.promotion.ServerInterfaces;
import edu.neu.promotion.ServerInvoker;
import edu.neu.promotion.activities.SearchMemberResultActivity;
import edu.neu.promotion.activities.SearchSelectItemActivity;
import edu.neu.promotion.components.AlertDialog;
import edu.neu.promotion.components.PageManager;
import edu.neu.promotion.components.RunNetworkTaskPage;
import edu.neu.promotion.enties.AdminNode;
import edu.neu.promotion.enties.DictionaryItemNode;
import edu.neu.promotion.enties.ServerResponseNode;

public class SearchMemberPage extends RunNetworkTaskPage {

    public static final int RESULT_MEMBER_MODIFIED = 1;

    private static final int TASK_GET_COLLEGE = 1;
    private static final int TASK_GET_CHECK_STATE = 2;

    private static final int ACTIVITY_REQUEST_SELECT_DEPARTMENT = 1;
    private static final int ACTIVITY_REQUEST_SHOW_SEARCH_RESULT = 2;

    private String[] memberLevelOptions;

    private DictionaryItemNode[] departments;
    private ArrayList<String> departmentDisplays;
    private DictionaryItemNode[] checkStates;
    private String[] checkStateDisplays;

    private DialogInterface.OnClickListener onDegreeDialogChoiceListener;
    private DialogInterface.OnClickListener onDegreeDialogListener;
    private DialogInterface.OnClickListener onCheckStateDialogChoiceListener;
    private DialogInterface.OnClickListener onCheckStateDialogListener;

    private EditText nameEdit;
    private TextView departmentEdit;
    private TextView degreeEdit;
    private EditText accountEdit;
    private EditText phoneEdit;
    private TextView checkStateEdit;
    private TextView searchButton;

    private DictionaryItemNode selectedDepartment;
    private int selectedLevelPos;
    private int editingLevelPos;
    private int selectedCheckStatePos;
    private int editingCheckStatePos;

    public SearchMemberPage(PageManager pageManager, Object... args) {
        super(pageManager, args);
    }

    @Override
    protected void onCreate() {
        super.onCreate();

        setTitle(R.string.member_search_title);

        memberLevelOptions = new String[] {
                getString(R.string.member_search_member_level_all),
                getString(R.string.member_search_member_level_students),
                getString(R.string.member_search_member_level_undergraduate),
                getString(R.string.member_search_member_level_graduate),
                getString(R.string.member_search_member_level_postgraduate)
        };

        onDegreeDialogChoiceListener = (dialog, which) -> editingLevelPos = which;
        onDegreeDialogListener = (dialog, which) -> {
            dialog.dismiss();
            if (which == DialogInterface.BUTTON_POSITIVE) {
                selectedLevelPos = editingLevelPos;
                degreeEdit.setText(memberLevelOptions[selectedLevelPos]);
            }
        };
        onCheckStateDialogChoiceListener = (dialog, which) -> editingCheckStatePos = which;
        onCheckStateDialogListener = (dialog, which) -> {
            dialog.dismiss();
            if (which == DialogInterface.BUTTON_POSITIVE) {
                selectedCheckStatePos = editingCheckStatePos;
                checkStateEdit.setText(checkStateDisplays[selectedCheckStatePos]);
            }
        };

        requestTasks();
    }

    private void requestTasks() {
        toLoadingState();
        if (departments == null) {
            runTask(ServerInterfaces.Dictionary.getDictionaryItems(ServerInterfaces.Dictionary.DICTIONARY_TYPE_COLLEGE), TASK_GET_COLLEGE);
        }
        if (checkStates == null) {
            runTask(ServerInterfaces.Dictionary.getDictionaryItems(ServerInterfaces.Dictionary.DICTIONARY_TYPE_ADMIN_CHECK_STATE), TASK_GET_CHECK_STATE);
        }
    }

    private void loadMainViews() {
        View.OnClickListener onClickListener = v -> {
            if (v == departmentEdit) {
                Intent intent = new Intent(getContext(), SearchSelectItemActivity.class);
                intent.putExtra(SearchSelectItemActivity.REQUEST_EXTRA_TITLE, getString(R.string.register_extra_select_department_title));
                intent.putExtra(SearchSelectItemActivity.REQUEST_EXTRA_ITEMS, departmentDisplays);
                startActivityForResult(intent, ACTIVITY_REQUEST_SELECT_DEPARTMENT);
            }
            else if (v == degreeEdit) {
                editingLevelPos = selectedLevelPos;
                AlertDialog.Builder.getBottom(getContext())
                        .setChoices(memberLevelOptions, editingLevelPos, onDegreeDialogChoiceListener)
                        .setButton(DialogInterface.BUTTON_POSITIVE, R.string.ok, true)
                        .setButton(DialogInterface.BUTTON_NEGATIVE, R.string.cancel, true)
                        .setOnDialogButtonClickListener(onDegreeDialogListener)
                        .show();
            }
            else if (v == checkStateEdit) {
                editingCheckStatePos = selectedCheckStatePos;
                AlertDialog.Builder.getBottom(getContext())
                        .setChoices(checkStateDisplays, editingCheckStatePos, onCheckStateDialogChoiceListener)
                        .setButton(DialogInterface.BUTTON_POSITIVE, R.string.ok, true)
                        .setButton(DialogInterface.BUTTON_NEGATIVE, R.string.cancel, true)
                        .setOnDialogButtonClickListener(onCheckStateDialogListener)
                        .show();
            }
            else if (v == searchButton) {
                AdminNode adminInfo = new AdminNode();
                adminInfo.adminName = nameEdit.getText().toString();
                adminInfo.adminCollege = selectedDepartment == null ? "" : selectedDepartment.dictionaryId;
                switch (selectedLevelPos) {
                    case 0:
                        adminInfo.adminPosition = "";
                        adminInfo.adminDegree = "";
                        break;
                    case 1:
                        adminInfo.adminPosition = "学生";
                        adminInfo.adminDegree = "";
                        break;
                    case 2:
                        adminInfo.adminPosition = "学生";
                        adminInfo.adminDegree = "本科";
                        break;
                    case 3:
                        adminInfo.adminPosition = "学生";
                        adminInfo.adminDegree = "硕士";
                        break;
                    case 4:
                        adminInfo.adminPosition = "学生";
                        adminInfo.adminDegree = "博士";
                        break;
                }
                adminInfo.adminAccount = accountEdit.getText().toString();
                adminInfo.adminPhone = phoneEdit.getText().toString();
                String checkState = selectedCheckStatePos == 0 ? "" : checkStates[selectedCheckStatePos - 1].dictionaryId;
                Intent intent = new Intent(getContext(), SearchMemberResultActivity.class);
                intent.putExtra(SearchMemberResultActivity.REQUEST_EXTRA_ADMIN_INFO, adminInfo);
                intent.putExtra(SearchMemberResultActivity.REQUEST_EXTRA_ADMIN_CHECK_STATE, checkState);
                startActivityForResult(intent, ACTIVITY_REQUEST_SHOW_SEARCH_RESULT);
            }
        };
        setContentView(R.layout.page_search_member);
        nameEdit = findViewById(R.id.nameEdit);
        departmentEdit = findViewById(R.id.departmentEdit);
        departmentEdit.setOnClickListener(onClickListener);
        departmentEdit.setText(R.string.unspecified);
        degreeEdit = findViewById(R.id.degreeEdit);
        degreeEdit.setOnClickListener(onClickListener);
        degreeEdit.setText(memberLevelOptions[0]);
        accountEdit = findViewById(R.id.accountEdit);
        phoneEdit = findViewById(R.id.phoneEdit);
        checkStateEdit = findViewById(R.id.checkStateEdit);
        checkStateEdit.setOnClickListener(onClickListener);
        checkStateEdit.setText(checkStateDisplays[0]);
        searchButton = findViewById(R.id.searchButton);
        searchButton.setOnClickListener(onClickListener);
    }

    @Override
    protected void onTaskResult(int requestCode, Object result) {
        super.onTaskResult(requestCode, result);
        ServerResponseNode response = ServerInterfaces.analyseCommonContent((ServerInvoker.InvokeResult) result);
        if (response.code != ServerInterfaces.RESULT_CODE_SUCCESS) {
            return;
        }
        switch (requestCode) {
            case TASK_GET_COLLEGE:
                departments = JsonNode.toObject(response.object, DictionaryItemNode[].class);
                departmentDisplays = new ArrayList<>(departments.length);
                for (DictionaryItemNode node : departments) {
                    departmentDisplays.add(node.dictionaryName);
                }
                if (checkStates != null) {
                    loadMainViews();
                    toNormalState();
                }
                break;
            case TASK_GET_CHECK_STATE:
                checkStates = JsonNode.toObject(response.object, DictionaryItemNode[].class);
                checkStateDisplays = new String[checkStates.length + 1];
                checkStateDisplays[0] = getString(R.string.unspecified);
                for (int i = 0; i < checkStates.length; i++) {
                    checkStateDisplays[i + 1] = checkStates[i].dictionaryName;
                }
                if (departments != null) {
                    loadMainViews();
                    toNormalState();
                }
                break;
        }
    }

    @Override
    protected void onTaskRetryFailed(int requestCode) {
        super.onTaskRetryFailed(requestCode);
        switch (requestCode) {
            case TASK_GET_COLLEGE:
                cancelTask(TASK_GET_CHECK_STATE);
                toErrorState();
                break;
            case TASK_GET_CHECK_STATE:
                cancelTask(TASK_GET_COLLEGE);
                toErrorState();
                break;
        }
    }

    @Override
    protected void onErrorStateRetry() {
        super.onErrorStateRetry();
        requestTasks();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ACTIVITY_REQUEST_SELECT_DEPARTMENT:
                if (resultCode == Activity.RESULT_OK) {
                    selectedDepartment = departments[data.getIntExtra(SearchSelectItemActivity.RESULT_EXTRA_POSITION, 0)];
                    departmentEdit.setText(selectedDepartment.dictionaryName);
                }
                break;
            case ACTIVITY_REQUEST_SHOW_SEARCH_RESULT:
                if (resultCode == Activity.RESULT_OK) {
                    notifyParent(RESULT_MEMBER_MODIFIED, data);
                }
                break;
        }
    }
}
