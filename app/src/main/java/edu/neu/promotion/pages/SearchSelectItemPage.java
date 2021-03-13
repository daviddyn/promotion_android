package edu.neu.promotion.pages;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ListView;
import android.widget.TextView;

import com.davidsoft.utils.ChinesePronounce;
import com.davidsoft.utils.TextUtils;

import java.util.ArrayList;
import java.util.List;

import edu.neu.promotion.R;
import edu.neu.promotion.components.Page;
import edu.neu.promotion.components.PageManager;
import edu.neu.promotion.views.ClearableEditText;
import edu.neu.promotion.views.EntityAdapter;
import edu.neu.promotion.views.EntityFiller;
import edu.neu.promotion.views.IndexedListView;

public class SearchSelectItemPage extends Page {

    public static final class HighlightItem {
        private final int pos;
        private final String src;
        private final String find;

        private HighlightItem(int pos, String src, String find) {
            this.pos = pos;
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

    private final String title;
    private final int[] sortedItemsPos;
    private final ArrayList<Integer> sortedBelongs;
    private ArrayList<String> filteredIndex;
    private final ArrayList<String> sortedItems;

    private ArrayList<HighlightItem> searchResults;

    private ValueAnimator alphaAnimator;
    private int animateState;

    private IndexedListView listView;
    private View searchView;
    private ClearableEditText searchEdit;
    private ListView searchListView;
    private EntityAdapter<HighlightItem> searchAdapter;
    private TextView searchResultTip;

    public SearchSelectItemPage(PageManager pageManager, Object... args) {
        super(pageManager, args);
        title = (String) args[0];
        List<String> unsortedItems = (List<String>) args[1];
        sortedItemsPos = new int[unsortedItems.size()];
        sortedBelongs = new ArrayList<>(unsortedItems.size());
        if (args.length >= 4) {
            sortItemsByBelongs(unsortedItems, (List<String>) args[2], (List<Integer>) args[3]);
        }
        else {
            generateIndexByPY(unsortedItems);
        }
        sortedItems = new ArrayList<>(unsortedItems.size());
        for (int i : sortedItemsPos) {
            sortedItems.add(unsortedItems.get(i));
        }
    }

    private void sortItemsByBelongs(List<String> unsortedItems, List<String> rawIndex, List<Integer> unsortedBelongs) {
        ArrayList<Integer>[] bins = new ArrayList[rawIndex.size()];
        for (int i = 0; i < unsortedItems.size(); i++) {
            int binPos = unsortedBelongs.get(i);
            if (bins[binPos] == null) {
                bins[binPos] = new ArrayList<>();
            }
            bins[binPos].add(i);
        }
        filteredIndex = new ArrayList<>(bins.length);
        int k = 0;
        for (int i = 0; i < bins.length; i++) {
            if (bins[i] == null) {
                continue;
            }
            for (Integer pos : bins[i]) {
                sortedItemsPos[k++] = pos;
                sortedBelongs.add(filteredIndex.size());
            }
            filteredIndex.add(rawIndex.get(i));
        }
    }

    private void generateIndexByPY(List<String> unsortedItems) {
        ArrayList<Integer>[] bins = new ArrayList[28];
        for (int i = 0; i < unsortedItems.size(); i++) {
            String item = unsortedItems.get(i);
            int binPos;
            if (item.isEmpty()) {
                binPos = 27;
            }
            else {
                char firstChar = item.charAt(0);
                switch (TextUtils.getCharacterType(firstChar)) {
                    case NUMBER:
                        binPos = 0;
                        break;
                    case UPPER_CASE:
                        binPos = firstChar - 'A' + 1;
                        break;
                    case LOWER_CASE:
                        binPos = firstChar - 'a' + 1;
                        break;
                    case CHINESE:
                        binPos = ChinesePronounce.getInstance().getSelling(item).charAt(0);
                        if ('A' <= binPos && binPos <= 'Z') {
                            binPos = binPos - 'A' + 1;
                        }
                        else if ('a' <= binPos && binPos <= 'z') {
                            binPos = binPos - 'a' + 1;
                        }
                        else {
                            binPos = 27;
                        }
                        break;
                    default:
                        binPos = 27;
                        break;
                }
            }
            if (bins[binPos] == null) {
                bins[binPos] = new ArrayList<>();
            }
            bins[binPos].add(i);
        }
        filteredIndex = new ArrayList<>(bins.length);
        int k = 0;
        for (int i = 0; i < bins.length; i++) {
            if (bins[i] == null) {
                continue;
            }
            for (Integer pos : bins[i]) {
                sortedItemsPos[k++] = pos;
                sortedBelongs.add(filteredIndex.size());
            }
            switch (i) {
                case 0:
                    filteredIndex.add("#");
                    break;
                case 27:
                    filteredIndex.add("…");
                    break;
                default:
                    filteredIndex.add(String.valueOf((char)('A' + i - 1)));
                    break;
            }
        }
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
                ((TextView) itemView).setText((String)item);
            }

            @Override
            public void fillIndexView(View indexView, Object indexItem) {
                ((TextView) indexView).setText(((String) indexItem));
            }
        });
        listView.setData(sortedItems, filteredIndex, filteredIndex, sortedBelongs);
        listView.setOnItemClickListener((parent, view, position, id) -> notifyParent(0, sortedItemsPos[position]));
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
        searchListView.setOnItemClickListener((parent, view, position, id) -> notifyParent(0, sortedItemsPos[searchResults.get(position).pos]));
        searchResultTip = findViewById(R.id.searchResultTip);

        addActionbarButton(getDrawable(R.drawable.ic_actionbar_search), R.string.search);
    }

    private void doSearch(String search) {
        searchResults.clear();
        if (search.isEmpty()) {
            searchListView.setVisibility(View.GONE);
            searchResultTip.setVisibility(View.GONE);
        }
        else {
            for (int i = 0; i < sortedItems.size(); i++) {
                String item = sortedItems.get(i);
                if (item.contains(search)) {
                    HighlightItem highlightItem = new HighlightItem(i, item, search);
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
            addActionbarButton(getDrawable(R.drawable.ic_actionbar_search), R.string.search);
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
