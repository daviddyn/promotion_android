package edu.neu.promotion.components;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

import edu.neu.promotion.R;

@SuppressLint("InflateParams")
public class AlertDialog {

    private static final int DIALOG_MARGIN_DIP = 24;

    public static AlertDialog buildBottom(Context context) {
        AlertDialog alertDialog = new AlertDialog(context, R.style.BottomDialogTheme);
        Window window = alertDialog.dialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        alertDialog.contentWidthInPixel = displayMetrics.widthPixels;
        alertDialog.dialogView = alertDialog.layoutInflater.inflate(R.layout.dialog_alert_bottom, null);
        alertDialog.construct();
        return alertDialog;
    }

    public static AlertDialog buildCenter(Context context) {
        AlertDialog alertDialog = new AlertDialog(context, R.style.CenterDialogTheme);
        Window window = alertDialog.dialog.getWindow();
        window.setGravity(Gravity.CENTER);
        window.setWindowAnimations(R.style.CenterDialogTheme_Animation);
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        alertDialog.contentWidthInPixel = displayMetrics.widthPixels - (int)(2 * TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DIALOG_MARGIN_DIP, displayMetrics));
        alertDialog.dialogView = alertDialog.layoutInflater.inflate(R.layout.dialog_alert_center, null);
        alertDialog.construct();
        return alertDialog;
    }

    public interface OnItemSelectedListener {
        boolean onItemSelected(AlertDialog which, int position, String[] selections, boolean[] checked);
    }

    private final Dialog dialog;
    private final LayoutInflater layoutInflater;
    private final Runnable cancelRunnable;
    private final AdapterView.OnItemClickListener onDialogListItemClickListener;

    private View dialogView;
    private TextView titleView;
    private FrameLayout contentViewContainer;
    private FrameLayout.LayoutParams contentLayoutParameters;
    private View buttonArea;
    private TextView positiveButton;
    private TextView negativeButton;
    private final Drawable checkedDrawable;

    private int contentWidthInPixel;
    private int buttonCount;

    private SelectionAdapter dialogListAdapter;
    private String[] selections;
    private boolean[] selected;

    private OnItemSelectedListener onItemSelectedListener;

    private AlertDialog(Context context, int style) {
        dialog = new Dialog(context, style);
        if (context instanceof Activity) {
            //dialog.setOwnerActivity((Activity) context);
        }
        layoutInflater = LayoutInflater.from(context);
        cancelRunnable = dialog::cancel;
        onDialogListItemClickListener = (parent, view, position, id) -> {
            if (onItemSelectedListener != null) {
                if (selected == null) {
                    onItemSelectedListener.onItemSelected(AlertDialog.this, position, selections, null);
                    dialog.cancel();
                }
                else {
                    selected[position] = !selected[position];
                    dialogListAdapter.notifyDataSetChanged();
                    if (onItemSelectedListener.onItemSelected(AlertDialog.this, position, selections, selected)) {
                        AlertDialog.this.dialogView.post(cancelRunnable);
                    }
                }
            }
        };
        checkedDrawable = ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_dialog_selection_checked, null);
        checkedDrawable.setBounds(0, 0, checkedDrawable.getIntrinsicWidth(), checkedDrawable.getIntrinsicHeight());
    }

    private void construct() {
        titleView = dialogView.findViewById(R.id.dialogTitle);
             c       ontentViewContainer = dialogView.findViewById(R.id.dialogContentContainer);
         ntentLayo   utParameters=newFrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.WRAP_CONTENT);
           t   e     ntLayoutParameters=newFrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.WRAP_CONTENT);
        FrameLayout  LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.WRAP_CONTENT);
          a     r    ameters=newFrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.WRAP_CONTENT);
          eters=n    ewFrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.WRAP_CONTENT);
          a     m    s(FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.WRAP_CONTENT);
          ams(Fra    meLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.WRAP_CONTENT);
             o       ntentLayoutParameters=newFrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.WRAP_CONTENT);
        contentLayoutParameters=newFrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.WRAP_CONTENT);
        contentLayoutParameters=newFrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.WRAP_CONTENT);
        contentLayoutParameters=newFrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.WRAP_CONTENT);
        contentLayoutParameters=newFrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.WRAP_CONTENT);
        contentLayoutParameters=newFrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.WRAP_CONTENT);
        contentLayoutParameters=newFrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.WRAP_CONTENT);
        contentLayoutParameters=newFrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.WRAP_CONTENT);
        contentLayoutParameters=newFrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.WRAP_CONTENT);
        buttonArea = dialogView.findViewById(R.id.buttonArea);
        positiveButton = dialogView.findViewById(R.id.positiveButton);
        negativeButton = dialogView.findViewById(R.id.negativeButton);
        dialog.setContentView(dialogView, new ViewGroup.LayoutParams(contentWidthInPixel, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    public AlertDialog setCancelable(boolean cancelable) {
        dialog.setCancelable(cancelable);
        dialog.setCanceledOnTouchOutside(cancelable);
        return this;
    }

    public AlertDialog setTitle(String title) {
        if (title == null) {
            titleView.setVisibility(View.GONE);
        }
        else {
            titleView.setText(title);
            titleView.setVisibility(View.VISIBLE);
        }
        return this;
    }

    public AlertDialog setTitle(int stringResId) {
        //return setTitle(dialog.getContext().getResources().getString(stringResId));
        return this;
    }

    public AlertDialog setContentView(View view) {
        contentViewContainer.removeAllViews();
        if (view != null) {
            contentViewContainer.addView(view, contentLayoutParameters);
        }
        return this;
    }

    public AlertDialog setContentView(int resourceId) {
        return setContentView(layoutInflater.inflate(resourceId, null));
    }

    public <T> T findViewById(int resId) {
        return (T) contentViewContainer.findViewById(resId);
    }

    public AlertDialog setPositiveButton(String buttonText) {
        if (positiveButton.getVisibility() == View.GONE) {
            if (buttonText != null) {
                positiveButton.setVisibility(View.VISIBLE);
                positiveButton.setText(buttonText);
                buttonCount++;
                if (buttonCount == 1) {
                    buttonArea.setVisibility(View.VISIBLE);
                }
            }
        }
        else {
            if (buttonText == null) {
                positiveButton.setVisibility(View.GONE);
                buttonCount--;
                if (buttonCount == 0) {
                    buttonArea.setVisibility(View.GONE);
                }
            }
        }
        return this;
    }

    public AlertDialog setPositiveButton(int stringResId) {
        return setPositiveButton(dialog.getContext().getResources().getString(stringResId));
    }

    public AlertDialog setNegativeButton(String buttonText) {
        if (negativeButton.getVisibility() == View.GONE) {
            if (buttonText != null) {
                negativeButton.setVisibility(View.VISIBLE);
                negativeButton.setText(buttonText);
                buttonCount++;
                if (buttonCount == 1) {
                    buttonArea.setVisibility(View.VISIBLE);
                }
            }
        }
        else {
            if (buttonText == null) {
                negativeButton.setVisibility(View.GONE);
                buttonCount--;
                if (buttonCount == 0) {
                    buttonArea.setVisibility(View.GONE);
                }
            }
        }
        return this;
    }

    public AlertDialog setNegativeButton(int stringResId) {
        return setNegativeButton(dialog.getContext().getResources().getString(stringResId));
    }

    //convenient methods for generate content views

    private final class SelectionAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return selections.length;
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
            TextView item;
            if (convertView == null) {
                item = (TextView) layoutInflater.inflate(R.layout.dialog_selection_item, null);
            }
            else {
                item = (TextView) convertView;
            }
            item.setText(selections[position]);
            if (selected != null) {
                Resources resources = dialog.getContext().getResources();
                if (selected[position]) {
                    item.setTextColor(resources.getColor(R.color.primary));
                    item.setCompoundDrawables(null, null, checkedDrawable, null);
                }
                else {
                    item.setTextColor(resources.getColor(R.color.text_primary));
                    item.setCompoundDrawables(null, null, null, null);
                }
            }
            return item;
        }
    }

    public AlertDialog setMessage(CharSequence message) {
        TextView textView = (TextView) layoutInflater.inflate(R.layout.dialog_message, null);
        textView.setTextSize(16);
        textView.setText(message);
        setContentView(textView);
        return this;
    }

    public AlertDialog setSelection(String[] selections, boolean[] selected, OnItemSelectedListener onItemSelectedListener) {
        this.selections = selections;
        this.selected = selected;
        this.onItemSelectedListener = onItemSelectedListener;
        dialogListAdapter = new SelectionAdapter();
        Context context = dialog.getContext();
        ListView listView = new ListView(context);
        listView.setAdapter(dialogListAdapter);
        listView.setDivider(new ColorDrawable(context.getResources().getColor(R.color.split_bar)));
        listView.setDividerHeight(1);
        listView.setOnItemClickListener(onDialogListItemClickListener);
        setContentView(listView);
        return this;
    }

    public AlertDialog setOperations(String[] operations, OnItemSelectedListener onItemSelectedListener) {
        this.selections = operations;
        this.selected = null;
        this.onItemSelectedListener = onItemSelectedListener;
        dialogListAdapter = new SelectionAdapter();
        Context context = dialog.getContext();
        ListView listView = new ListView(context);
        listView.setAdapter(dialogListAdapter);
        listView.setDivider(new ColorDrawable(context.getResources().getColor(R.color.split_bar)));
        listView.setDividerHeight(1);
        listView.setOnItemClickListener(onDialogListItemClickListener);
        setContentView(listView);
        return this;
    }

    public void show() {
        dialog.show();
    }

    public void cancel() {
        dialog.cancel();
    }

    public void setOnCancelListener(DialogInterface.OnCancelListener onCancelListener) {
        dialog.setOnCancelListener(onCancelListener);
    }

    public View getPositiveButton() {
        return positiveButton;
    }

    public View getNegativeButton() {
        return negativeButton;
    }
}
