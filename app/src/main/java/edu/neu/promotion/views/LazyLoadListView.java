package edu.neu.promotion.views;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

import edu.neu.promotion.R;

public class LazyLoadListView extends ListView {

    public interface OnLoadListener {
        void onLoad();
    }

    public LazyLoadListView(Context context) {
        super(context);
        construct();
    }

    public LazyLoadListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        construct();
    }

    public LazyLoadListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        construct();
    }

    private static final int STATE_NONE = 0;
    private static final int STATE_LOADING = 1;
    private static final int STATE_NO_MORE = 2;

    private View footerView;
    private ImageView loadingIconView;
    private AnimationDrawable loadingAnimation;
    private TextView tipTextView;

    private OnLoadListener onLoadListener;

    private boolean footerViewInVision;
    private int state;

    private void construct() {
        footerView = LayoutInflater.from(getContext()).inflate(R.layout.item_list_footer, null);
        loadingIconView = footerView.findViewById(R.id.loadingIconView);
        loadingAnimation = (AnimationDrawable) ResourcesCompat.getDrawable(getResources(), R.drawable.bg_loading, null);
        loadingIconView.setImageDrawable(loadingAnimation);
        tipTextView = footerView.findViewById(R.id.tipTextView);
        addFooterView(footerView, null, false);
        setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (getChildCount() == 0) {
                    return;
                }
                if (getChildAt(getChildCount() - 1) == footerView) {
                    if (!footerViewInVision) {
                        //底部提示进入视野
                        footerViewInVision = true;
                        if (state != STATE_NO_MORE) {
                            loadingAnimation.start();
                        }
                        if (state == STATE_NONE) {
                            state = STATE_LOADING;
                            //TODO: 通知加载
                            if (onLoadListener != null) {
                                onLoadListener.onLoad();
                            }
                        }
                    }
                }
                else {
                    if (footerViewInVision) {
                        //底部提示退出视野
                        footerViewInVision = false;
                        loadingAnimation.stop();
                    }
                }
            }
        });
    }

    public void debug() {
        for (int i = 0; i < getChildCount(); i++) {
            System.out.println(getChildAt(i) == footerView);
        }
    }

    public void notifyLoadResult(boolean hasMore) {
        loadingAnimation.stop();
        if (hasMore) {
            state = STATE_NO_MORE;
            loadingIconView.setVisibility(GONE);
            tipTextView.setText(R.string.list_footer_no_more);
        }
        else {
            state = STATE_NONE;
        }
    }

    public void setOnLoadListener(OnLoadListener onLoadListener) {
        this.onLoadListener = onLoadListener;
    }
}