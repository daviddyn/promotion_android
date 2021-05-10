package edu.neu.promotion.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import java.util.ArrayList;

import edu.neu.promotion.components.Page;
import edu.neu.promotion.components.PageActivity;
import edu.neu.promotion.pages.SearchMemberResultPage;

public class SearchMemberResultActivity extends PageActivity {

    public static final String REQUEST_EXTRA_ADMIN_INFO = "admin";
    public static final String REQUEST_EXTRA_ADMIN_CHECK_STATE = "checkState";
    public static final String RESULT_EXTRA_ACCEPT_IDS = "acceptIds";
    public static final String RESULT_EXTRA_NEW_CHECK_STATES = "newCheckStates";
    public static final String RESULT_EXTRA_DENIED_IDS = "deniedIds";

    private ArrayList<String> acceptIds;
    private ArrayList<String> newCheckStates;
    private ArrayList<String> deniedIds;

    private Intent resultData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        acceptIds = new ArrayList<>();
        newCheckStates = new ArrayList<>();
        deniedIds = new ArrayList<>();
        Intent intent = getIntent();
        setContentPage(new SearchMemberResultPage(this, intent.getSerializableExtra(REQUEST_EXTRA_ADMIN_INFO), intent.getStringExtra(REQUEST_EXTRA_ADMIN_CHECK_STATE)));
    }

    @Override
    public void onPageNotify(Page who, int notifyCode, Object... args) {
        super.onPageNotify(who, notifyCode, args);
        switch (notifyCode) {
            case SearchMemberResultPage.RESULT_ACCEPT:
                acceptIds.add((String) args[0]);
                newCheckStates.add((String) args[1]);
                setResults();
                break;
            case SearchMemberResultPage.RESULT_DENIED:
                deniedIds.add((String) args[0]);
                setResults();
                break;
        }
    }

    private void setResults() {
        if (resultData == null) {
            resultData = new Intent();
            resultData.putStringArrayListExtra(RESULT_EXTRA_ACCEPT_IDS, acceptIds);
            resultData.putStringArrayListExtra(RESULT_EXTRA_NEW_CHECK_STATES, newCheckStates);
            resultData.putStringArrayListExtra(RESULT_EXTRA_DENIED_IDS, deniedIds);
            setResult(RESULT_OK, resultData);
        }
    }
}
