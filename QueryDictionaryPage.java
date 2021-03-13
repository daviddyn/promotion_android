package edu.neu.promotion.pages;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.davidsoft.utils.ChinesePronounce;
import com.davidsoft.utils.JsonNode;
import com.davidsoft.utils.TextUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import edu.neu.promotion.R;
import edu.neu.promotion.ServerInterfaces;
import edu.neu.promotion.ServerInvoker;
import edu.neu.promotion.components.PageManager;
import edu.neu.promotion.components.RunNetworkTaskPage;
import edu.neu.promotion.enties.DictionaryItemNode;
import edu.neu.promotion.enties.ServerResponseNode;
import edu.neu.promotion.views.ClearableEditText;
import edu.neu.promotion.views.EntityAdapter;
import edu.neu.promotion.views.EntityFiller;
import edu.neu.promotion.views.IndexedListView;

public class QueryDictionaryPage extends RunNetworkTaskPage {

    public static final int RESULT_OK = 1;
    public static final int RESULT_CANCEL = 0;

    private static final int TASK_QUERY_DICTIONARY = 1;

    private final String title;
    private final String dictionaryType;

    public static final class HighlightItem {
        private final int pos;
        private final String id;
        private final String src;
        private final String find;

        private HighlightItem(int pos, String id, String src, String find) {
            this.pos = pos;
            this.id = id;
            this.src = src;
            this.find = find;
        }
    }

    public static final class SearchEntityFiller implements EntityFiller<HighlightItem> {

        private TextView rootView;

        @Override
        public View generateView(Context context) {
            rootView = (TextView) LayoutInflater.from(context).inflate(R.layout.item_dictionary, null);
            return rootView;
        }

        @Override
        public void fill(HighlightItem entity, Object tag) {
            SpannableString spannableString = new SpannableString(entity.src);
            int findStart = 0;
            while (true) {
                findStart = entity.src.indexOf(entity.find, findStart);
                if (findStart == -1) {
                    break;
                }
                spannableString.setSpan(new ForegroundColorSpan(rootView.getContext().getResources().getColor(R.color.primary)), findStart, findStart + entity.find.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                findStart += entity.find.length();
            }
            rootView.setText(spannableString);
        }

        @Override
        public View getClickableView() {
            return null;
        }
    }

    private List<DictionaryItemNode> mainListItems;
    private List<String> mainListIndex;
    private List<Integer> mainListBelongs;
    private ArrayList<HighlightItem> searchResults;

    private ValueAnimator alphaAnimator;
    private int animateState;

    private IndexedListView listView;
    private View searchView;
    private ClearableEditText searchEdit;
    private ListView searchListView;
    private EntityAdapter<HighlightItem> searchAdapter;
    private TextView searchResultTip;

    public QueryDictionaryPage(PageManager pageManager, Object... args) {
        super(pageManager, args);
        title = (String) args[0];
        dictionaryType = (String) args[1];
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        setTitle(title);

        searchResults = new ArrayList<>();

        alphaAnimator = new ValueAnimator();
        alphaAnimator.setInterpolator(new DecelerateInterpolator());
        alphaAnimator.setDuration(getResource().getInteger(android.R.integer.config_shortAnimTime));
        alphaAnimator.addUpdateListener(animation -> {
            float value = (float) animation.getAnimatedValue();
            listView.setAlpha(value);
            searchView.setAlpha(1 - value);
        });
        alphaAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                switch (animateState) {
                    case 1:
                        searchView.setVisibility(View.GONE);
                        animateState = 0;
                        break;
                    case 2:
                        listView.setVisibility(View.GONE);
                        animateState = 3;
                        break;
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        runTask(ServerInterfaces.Dictionary.getDictionaryItems(dictionaryType), TASK_QUERY_DICTIONARY);
    }

    private void loadMainViews() {
        setContentView(R.layout.page_query_dictionary);

        listView = findViewById(R.id.listView);
        listView.getListView().setDividerHeight(0);
        listView.setIndexTipHeightInPx((int) getDimension(R.dimen.list_index_item_height));
        listView.setViewGenerator(new IndexedListView.ViewGenerator() {
            @Override
            public View generateItemView() {
                return getLayoutInflater().inflate(R.layout.item_dictionary, null);
            }

            @Override
            public View generateIndexView() {
                return getLayoutInflater().inflate(R.layout.item_list_index, null);
            }

            @Override
            public void fillItemView(View itemView, Object item) {
                ((TextView) itemView).setText(((DictionaryItemNode) item).dictionaryName);
            }

            @Override
            public void fillIndexView(View indexView, Object indexItem) {
                ((TextView) indexView).setText(((String) indexItem));
            }
        });
        listView.setData(mainListItems, mainListIndex, mainListIndex, mainListBelongs);
        listView.setOnItemClickListener((parent, view, position, id) -> notifyParent(RESULT_OK, mainListItems.get(position)));
        searchView = findViewById(R.id.searchView);
        searchEdit = findViewById(R.id.searchEdit);
        searchEdit.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                doSearch(s.toString());
            }
        });
        searchListView = findViewById(R.id.searchListView);
        searchAdapter = new EntityAdapter<>(searchResults, SearchEntityFiller.class);
        searchListView.setAdapter(searchAdapter);
        searchListView.setOnItemClickListener((parent, view, position, id) -> notifyParent(RESULT_OK, mainListItems.get(searchResults.get(position).pos)));
        searchResultTip = findViewById(R.id.searchResultTip);

        addActionbarButton(getDrawable(R.drawable.ic_actionbar_search));
    }

    private void processQueryResult(DictionaryItemNode[] queryResult) {
        TreeMap<String, ArrayList<DictionaryItemNode>> index = new TreeMap<>();
        ArrayList<DictionaryItemNode> numbers = new ArrayList<>();
        ArrayList<DictionaryItemNode> others = new ArrayList<>();
        for (DictionaryItemNode node : queryResult) {
            ArrayList<DictionaryItemNode> bin;
            if (node.dictionaryName.length() == 0) {
                bin = others;
            }
            else {
                String key;
                char firstChar = node.dictionaryName.charAt(0);
                switch (TextUtils.getCharacterType(firstChar)) {
                    case NUMBER:
                        bin = numbers;
                        break;
                    case UPPER_CASE:
                    case LOWER_CASE:
                        key = String.valueOf(firstChar).toUpperCase();
                        bin = index.get(key);
                        if (bin == null) {
                            bin = new ArrayList<>();
                            index.put(key, bin);
                        }
                        break;
                    case CHINESE:
                        key = ChinesePronounce.getInstance().getSelling(node.dictionaryName).substring(0, 1).toUpperCase();
                        bin = index.get(key);
                        if (bin == null) {
                            bin = new ArrayList<>();
                            index.put(key, bin);
                        }
                        break;
                    default:
                        bin = others;
                        break;
                }
            }
            bin.add(node);
        }
        mainListItems = new ArrayList<>(queryResult.length);
        mainListIndex = new ArrayList<>(index.size() + 2);
        mainListBelongs = new ArrayList<>(queryResult.length);

        int position = 0;
        if (!numbers.isEmpty()) {
            mainListItems.addAll(numbers);
            mainListIndex.add("#");
            for (int i = 0; i < numbers.size(); i++) {
                mainListBelongs.add(0);
            }
            position++;
        }
        for (Map.Entry<String, ArrayList<DictionaryItemNode>> entry : index.entrySet()) {
            mainListItems.addAll(entry.getValue());
            mainListIndex.add(entry.getKey());
            for (int i = 0; i < entry.getValue().size(); i++) {
                mainListBelongs.add(position);
            }
            position++;
        }
        if (!others.isEmpty()) {
            mainListItems.addAll(others);
            mainListIndex.add("…");
            for (int i = 0; i < others.size(); i++) {
                mainListBelongs.add(position);
            }
        }
    }

    private void doSearch(String search) {
        searchResults.clear();
        if (search.isEmpty()) {
            searchListView.setVisibility(View.GONE);
            searchResultTip.setVisibility(View.GONE);
        }
        else {
            for (int i = 0; i < mainListItems.size(); i++) {
                DictionaryItemNode node = mainListItems.get(i);
                if (node.dictionaryName.contains(search)) {
                    HighlightItem highlightItem = new HighlightItem(i, node.dictionaryId, node.dictionaryName, search);
                    searchResults.add(highlightItem);
                }
            }
            if (searchResults.isEmpty()) {
                searchListView.setVisibility(View.GONE);
                searchResultTip.setVisibility(View.VISIBLE);
            }
            else {
                searchListView.setVisibility(View.VISIBLE);
                searchResultTip.setVisibility(View.GONE);
                searchAdapter.notifyDataSetChanged();
            }
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
            processQueryResult(JsonNode.toObject(response.object, DictionaryItemNode[].class));
            loadMainViews();
        }
        else {
            notifyParent(RESULT_CANCEL);
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
        retryTask(TASK_QUERY_DICTIONARY);
    }

    @Override
    protected void onActionbarButtonClick(int position, View viewForAnchor) {
        super.onActionbarButtonClick(position, viewForAnchor);
        switch (animateState) {
            case 0:
                animateState = 2;
                searchEdit.getEditText().setText("");
                removeActionbarButton(0);
                searchView.setVisibility(View.VISIBLE);
                searchEdit.requestFocus();
                alphaAnimator.setFloatValues(1, 0);
                alphaAnimator.start();
                break;
        }
    }

    @Override
    protected boolean onGoBack() {
        if (animateState == 3) {
            animateState = 1;
            addActionbarButton(getDrawable(R.drawable.ic_actionbar_search));
            hideSoftKeyboard();
            listView.setVisibility(View.VISIBLE);
            alphaAnimator.setFloatValues(0, 1);
            alphaAnimator.start();
            return true;
        }
        else {
            return super.onGoBack();
        }
    }
}
