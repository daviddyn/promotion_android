package edu.neu.promotion.views;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class IndexedListView extends ViewGroup {

    public interface ViewGenerator {

        View generateItemView();

        View generateIndexView();

        void fillItemView(View itemView, Object item);

        void fillIndexView(View indexView, Object indexItem);
    }

    public IndexedListView(Context context) {
        super(context);
        construct();
    }

    public IndexedListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        construct();
    }

    public IndexedListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        construct();
    }

    private static final class ListItemData {
        boolean isIndex;
        int position;
    }

    private ViewGenerator viewGenerator;
    private List<?> items;
    private List<?> index;
    private List<Integer> belongs;
    private List<Integer> indexItemPositions;
    private ArrayList<ListItemData> listItemData;

    private int indexTipHeightInPx;
    private ViewGroup indexTipContainer;
    private FrameLayout.LayoutParams listItemItemLayoutParams;    //MATCH_PARENT, WRAP_CONTENT
    private FrameLayout.LayoutParams listItemIndexLayoutParams;    //MATCH_PARENT, indexTipHeightInPx
    private ListView listView;
    private BaseAdapter listViewAdapter;
    private IndexedScrollbarView indexedScrollbarView;

    private int lastTipIndexPosition1;
    private int lastTipIndexPosition2;

    private AdapterView.OnItemClickListener fixedOnItemClickListener;
    private AdapterView.OnItemClickListener onItemClickListener;

    private void construct() {
        Context context = getContext();
        indexItemPositions = new ArrayList<>();
        listItemData = new ArrayList<>();
        indexTipContainer = new ViewGroup(context) {
            @Override
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(indexTipHeightInPx, MeasureSpec.EXACTLY);
                for (int i = 0; i < getChildCount(); i++) {
                    getChildAt(i).measure(widthMeasureSpec, childHeightMeasureSpec);
                }
                setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), indexTipHeightInPx);
            }

            @Override
            protected void onLayout(boolean changed, int l, int t, int r, int b) {
                int currTop = 0;
                for (int i = 0; i < getChildCount(); i++) {
                    int bottom = currTop + indexTipHeightInPx;
                    getChildAt(i).layout(0, currTop, r - l, bottom);
                    currTop = bottom;
                }
            }
        };
        indexTipContainer.setBackgroundColor(0xFFFFFFFF);
        listItemItemLayoutParams = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        listItemIndexLayoutParams = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0);
        listView = new ListView(context);
        listView.setVerticalScrollBarEnabled(false);
        listViewAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return listItemData.size();
            }

            @Override
            public Object getItem(int position) {
                return null;
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (viewGenerator == null) {
                    return null;
                }
                FrameLayout listItemView;
                if (convertView == null) {
                    listItemView = new FrameLayout(getContext());
                    listItemView.addView(viewGenerator.generateIndexView(), listItemIndexLayoutParams);
                    listItemView.addView(viewGenerator.generateItemView(), listItemItemLayoutParams);
                }
                else {
                    listItemView = (FrameLayout) convertView;
                }
                View indexView = listItemView.getChildAt(0);
                View itemView = listItemView.getChildAt(1);
                ListItemData data = listItemData.get(position);
                if (data.isIndex) {
                    indexView.setVisibility(VISIBLE);
                    viewGenerator.fillIndexView(indexView, index.get(data.position));
                    itemView.setVisibility(GONE);
                }
                else {
                    indexView.setVisibility(GONE);
                    itemView.setVisibility(VISIBLE);
                    viewGenerator.fillItemView(itemView, items.get(data.position));
                }
                return listItemView;
            }

            @Override
            public boolean isEnabled(int position) {
                return !listItemData.get(position).isIndex;
            }
        };
        listView.setOverScrollMode(OVER_SCROLL_NEVER);
        listView.setAdapter(listViewAdapter);
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {}

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                //所有学问均在这里
                if (listItemData.size() <= 1 || listView.getChildCount() == 0) {
                    return;
                }
                //1.找到被遮挡后，第一个能露在外面的View。值得一提的是，divider在ListView中不是Child
                int find;
                int lastBottom = 0;
                for (find = 0; find < listView.getChildCount(); find++) {
                    lastBottom = listView.getChildAt(find).getBottom();
                    if (lastBottom > listItemIndexLayoutParams.height) {
                        break;
                    }
                }
                if (find == listView.getChildCount()) {
                    return;
                }
                int dataPosition = find + firstVisibleItem;
                if (dataPosition < 0 || dataPosition >= listItemData.size()) {
                    return;
                }
                ListItemData data = listItemData.get(dataPosition);
                if (data.isIndex) {
                    int indexPosition = data.position - 1;
                    if (lastTipIndexPosition1 != indexPosition) {
                        lastTipIndexPosition1 = indexPosition;
                        viewGenerator.fillIndexView(indexTipContainer.getChildAt(0), index.get(indexPosition));
                        indexedScrollbarView.setSelectedItem(indexPosition);
                    }
                    indexPosition = data.position;
                    if (lastTipIndexPosition2 != indexPosition) {
                        lastTipIndexPosition2 = indexPosition;
                        viewGenerator.fillIndexView(indexTipContainer.getChildAt(1), index.get(indexPosition));
                    }
                    indexTipContainer.setScrollY(listItemIndexLayoutParams.height + listItemIndexLayoutParams.height - lastBottom);
                }
                else {
                    int indexPosition = belongs.get(data.position);
                    if (lastTipIndexPosition1 != indexPosition) {
                        lastTipIndexPosition1 = indexPosition;
                        viewGenerator.fillIndexView(indexTipContainer.getChildAt(0), index.get(indexPosition));
                        indexedScrollbarView.setSelectedItem(indexPosition);
                    }
                    if (indexTipContainer.getScrollY() != 0) {
                        indexTipContainer.setScrollY(0);
                    }
                }
            }
        });
        indexedScrollbarView = new IndexedScrollbarView(getContext());
        indexedScrollbarView.setPadding(
                0, 0, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics()), 0
        );
        indexedScrollbarView.setOnSelectChangedListener((who, selectedItem) -> listView.setSelection(indexItemPositions.get(selectedItem)));
        addView(listView);
        addView(indexTipContainer);
        addView(indexedScrollbarView);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        indexTipContainer.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(listItemIndexLayoutParams.height, MeasureSpec.EXACTLY));
        listView.measure(widthMeasureSpec, heightMeasureSpec);
        int contentWidth = listView.getMeasuredWidth(), contentHeight = listView.getMeasuredHeight();
        indexedScrollbarView.measure(
                MeasureSpec.makeMeasureSpec(contentWidth, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(contentHeight, MeasureSpec.EXACTLY)
        );
        setMeasuredDimension(contentWidth, contentHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        listView.layout(0, 0, r - l, b - t);
        indexTipContainer.layout(0, 0, r - l, listItemIndexLayoutParams.height);
        indexedScrollbarView.layout(0, 0, r - l, b - t);
    }

    public void setIndexTipHeightInPx(int indexTipHeightInPx) {
        this.indexTipHeightInPx = indexTipHeightInPx;
        listItemIndexLayoutParams.height = indexTipHeightInPx;
        requestLayout();
    }

    public void setViewGenerator(ViewGenerator viewGenerator) {
        this.viewGenerator = viewGenerator;
        indexTipContainer.setClipToOutline(false);
        indexTipContainer.setClipToPadding(false);
        indexTipContainer.addView(viewGenerator.generateIndexView());
        indexTipContainer.addView(viewGenerator.generateIndexView());
    }

    public void setData(List<?> items, List<?> index, List<String> indexString, List<Integer> itemBelongs) {
        this.items = items;
        this.index = index;
        this.belongs = itemBelongs;
        //多退
        int listItemCount = items.size() + index.size();
        for (int i = listItemData.size() - 1; i >= listItemCount; i--) {
            listItemData.remove(i);
        }
        //少补
        for (int i = listItemData.size(); i < listItemCount; i++) {
            listItemData.add(new ListItemData());
        }
        indexItemPositions.clear();
        if (!listItemData.isEmpty()) {
            //设置内容
            int i = 0, j = 0, k = 0;
            for (; i < index.size(); i++) {
                listItemData.get(k).isIndex = true;
                listItemData.get(k).position = i;
                indexItemPositions.add(k);
                k++;
                for (; j < items.size() && itemBelongs.get(j) == i; j++) {
                    listItemData.get(k).isIndex = false;
                    listItemData.get(k).position = j;
                    k++;
                }
            }
        }
        lastTipIndexPosition1 = -1;
        lastTipIndexPosition2 = -1;
        listViewAdapter.notifyDataSetChanged();
        indexTipContainer.setVisibility(listItemData.size() <= 1 ? GONE : VISIBLE);
        indexedScrollbarView.setIndex(indexString);
    }

    public ListView getListView() {
        return listView;
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
        if (onItemClickListener == null) {
            listView.setOnItemClickListener(null);
        }
        else {
            if (fixedOnItemClickListener == null) {
                fixedOnItemClickListener = (parent, view, position, id) -> IndexedListView.this.onItemClickListener.onItemClick(parent, view, listItemData.get(position).position, id);
            }
            listView.setOnItemClickListener(fixedOnItemClickListener);
        }
    }
}
