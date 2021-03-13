package edu.neu.promotion.components;

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
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

import edu.neu.promotion.R;

//为了避免部分机型出现BUG，此处使用懒加载
public class AlertDialog {

    private final Dialog dialog;
    private final TextView positiveButton;
    private final TextView negativeButton;

    private AlertDialog(Dialog dialog, TextView positiveButton, TextView negativeButton) {
        this.dialog = dialog;
        this.positiveButton = positiveButton;
        this.negativeButton = negativeButton;
    }

    public TextView getButton(int whichButton) {
        switch (whichButton) {
            case DialogInterface.BUTTON_POSITIVE:
                return positiveButton;
            case DialogInterface.BUTTON_NEGATIVE:
                return negativeButton;
            default:
                return null;
        }
    }

    public void cancel() {
        dialog.dismiss();
    }

    public static class Builder {

        private static final int DIALOG_MARGIN_DIP = 24;

        private final int contentWidthInPixel;
        private final boolean atBottom;
        private final Context context;
        private final LayoutInflater layoutInflater;

        private static final int CONTENT_TYPE_CUSTOM = 0;
        private static final int CONTENT_TYPE_MESSAGE = 1;
        private static final int CONTENT_TYPE_SELECTIONS = 2;
        private static final int CONTENT_TYPE_CHOICES = 3;
        private static final int CONTENT_TYPE_OPERATIONS = 4;
        private static final int CONTENT_TYPE_LOADING = 5;

        private static final FrameLayout.LayoutParams contentLayoutParameters = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);

        private boolean cancelable;
        private DialogInterface.OnCancelListener onCancelListener;
        private CharSequence title;
        private int contentType;
        private View contentView;
        private CharSequence positiveButtonText;
        private boolean positiveButtonEnabled;
        private CharSequence negativeButtonText;
        private boolean negativeButtonEnabled;
        private DialogInterface.OnClickListener onDialogButtonClickListener;

        private CharSequence message;
        private String[] options;
        private boolean[] optionChecks;
        private DialogInterface.OnMultiChoiceClickListener onSelectionsListener;
        private DialogInterface.OnClickListener onOperationsListener;

        public static Builder getCenter(Context context) {
            return new Builder(context, false);
        }

        public static Builder getBottom(Context context) {
            return new Builder(context, true);
        }

        private Builder(Context context, boolean atBottom) {
            DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
            if (atBottom) {
                contentWidthInPixel = displayMetrics.widthPixels - 2;
            }
            else {
                int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DIALOG_MARGIN_DIP, displayMetrics);
                contentWidthInPixel = displayMetrics.widthPixels - margin - margin;
            }
            cancelable = true;
            this.atBottom = atBottom;
            this.context = context;
            layoutInflater = LayoutInflater.from(context);
        }

        public Builder setCancelable(boolean cancelable) {
            this.cancelable = cancelable;
            return this;
        }

        public Builder setOnCancelListener(DialogInterface.OnCancelListener onCancelListener) {
            this.onCancelListener = onCancelListener;
            return this;
        }

        public Builder setTitle(CharSequence title) {
            this.title = title;
            return this;
        }

        public Builder setTitle(int stringResId) {
            return setTitle(context.getResources().getText(stringResId));
        }

        public Builder setContentView(View contentView) {
            contentType = CONTENT_TYPE_CUSTOM;
            this.contentView = contentView;
            message = null;
            options = null;
            optionChecks = null;
            onSelectionsListener = null;
            onOperationsListener = null;
            return this;
        }

        public Builder setContentView(int resourceId) {
            return setContentView(layoutInflater.inflate(resourceId, null));
        }

        public <T> T findViewById(int resId) {
            if (contentView == null) {
                return null;
            }
            return (T) contentView.findViewById(resId);
        }

        private static final class OnButtonClickListener implements View.OnClickListener {
            private final DialogInterface dialog;
            private final int whichButton;
            private final DialogInterface.OnClickListener onClickListener;

            private OnButtonClickListener(DialogInterface dialog, int whichButton, DialogInterface.OnClickListener onClickListener) {
                this.dialog = dialog;
                this.whichButton = whichButton;
                this.onClickListener = onClickListener;
            }

            @Override
            public void onClick(View v) {
                if (onClickListener == null) {
                    dialog.dismiss();
                }
                else {
                    onClickListener.onClick(dialog, whichButton);
                }
            }
        }

        public Builder setButton(int whichButton, CharSequence buttonText, boolean initialEnabled) {
            switch (whichButton) {
                case DialogInterface.BUTTON_POSITIVE:
                    positiveButtonText = buttonText;
                    positiveButtonEnabled = initialEnabled;
                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    negativeButtonText = buttonText;
                    negativeButtonEnabled = initialEnabled;
                    break;
            }
            return this;
        }

        public Builder setButton(int whichButton, int stringResId, boolean initialEnabled) {
            return setButton(whichButton, context.getResources().getText(stringResId), initialEnabled);
        }

        public Builder setOnDialogButtonClickListener(DialogInterface.OnClickListener onDialogButtonClickListener) {
            this.onDialogButtonClickListener = onDialogButtonClickListener;
            return this;
        }

        //convenient methods for generate content views

        public Builder setMessage(CharSequence message) {
            contentType = CONTENT_TYPE_MESSAGE;
            contentView = null;
            this.message = message;
            options = null;
            optionChecks = null;
            onSelectionsListener = null;
            onOperationsListener = null;
            return this;
        }

        public Builder setMessage(int stringResId) {
            return setMessage(context.getString(stringResId));
        }

        public Builder setSelections(String[] selections, boolean[] selected, DialogInterface.OnMultiChoiceClickListener onSelectionsListener) {
            contentType = CONTENT_TYPE_SELECTIONS;
            contentView = null;
            message = null;
            options = selections;
            optionChecks = selected;
            this.onSelectionsListener = onSelectionsListener;
            onOperationsListener = null;
            return this;
        }

        public Builder setChoices(String[] choices, int initialChoice, DialogInterface.OnClickListener onChoiceListener) {
            contentType = CONTENT_TYPE_CHOICES;
            contentView = null;
            message = null;
            options = choices;
            optionChecks = new boolean[choices.length];
            if (initialChoice >= 0 && initialChoice < choices.length) {
                optionChecks[initialChoice] = true;
            }
            onSelectionsListener = null;
            onOperationsListener = onChoiceListener;
            return this;
        }

        public Builder setOperations(String[] operations, DialogInterface.OnClickListener onOperationsListener) {
            contentType = CONTENT_TYPE_OPERATIONS;
            contentView = null;
            message = null;
            options = operations;
            optionChecks = null;
            onSelectionsListener = null;
            this.onOperationsListener = onOperationsListener;
            return this;
        }

        public Builder setLoading() {
            contentType = CONTENT_TYPE_LOADING;
            contentView = null;
            message = null;
            options = null;
            optionChecks = null;
            onSelectionsListener = null;
            onOperationsListener = null;
            return this;
        }

        private View generateMessageContentView() {
            TextView textView = (TextView) layoutInflater.inflate(R.layout.dialog_message, null);
            textView.setTextSize(16);
            textView.setText(message);
            return textView;
        }

        private static final class SelectionAdapter extends BaseAdapter {

            private final Context context;
            private final LayoutInflater layoutInflater;
            private final String[] options;
            private final boolean[] optionChecks;
            private Drawable checkedDrawable;

            private SelectionAdapter(Context context, String[] options, boolean[] optionChecks) {
                this.context = context;
                layoutInflater = LayoutInflater.from(context);
                this.options = options;
                this.optionChecks = optionChecks;
            }

            @Override
            public int getCount() {
                return options.length;
            }

            @Override
            public Object getItem(int position) {
                return null;
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            private Drawable getCheckedDrawable() {
                if (checkedDrawable == null) {
                    checkedDrawable = ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_dialog_selection_checked, null);
                    checkedDrawable.setBounds(0, 0, checkedDrawable.getIntrinsicWidth(), checkedDrawable.getIntrinsicHeight());
                }
                return checkedDrawable;
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
                item.setText(options[position]);
                if (optionChecks != null) {
                    Resources resources = context.getResources();
                    if (optionChecks[position]) {
                        item.setTextColor(resources.getColor(R.color.primary));
                        item.setCompoundDrawables(null, null, getCheckedDrawable(), null);
                    }
                    else {
                        item.setTextColor(resources.getColor(R.color.text_primary));
                        item.setCompoundDrawables(null, null, null, null);
                    }
                }
                return item;
            }

            boolean isChecked(int position) {
                return optionChecks[position];
            }

            void setChecked(int position, boolean checked) {
                optionChecks[position] = checked;
            }
        }

        private static final class OnSelectionsClickListener implements AdapterView.OnItemClickListener {

            private final DialogInterface dialog;
            private final SelectionAdapter adapter;
            private final DialogInterface.OnMultiChoiceClickListener onMultiChoiceClickListener;

            private OnSelectionsClickListener(DialogInterface dialog, SelectionAdapter adapter, DialogInterface.OnMultiChoiceClickListener onMultiChoiceClickListener) {
                this.dialog = dialog;
                this.adapter = adapter;
                this.onMultiChoiceClickListener = onMultiChoiceClickListener;
            }

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                boolean checked = !adapter.isChecked(position);
                adapter.setChecked(position, checked);
                adapter.notifyDataSetChanged();
                if (onMultiChoiceClickListener != null) {
                    onMultiChoiceClickListener.onClick(dialog, position, checked);
                }
            }
        }

        private static final class OnChoicesClickListener implements AdapterView.OnItemClickListener {

            private final DialogInterface dialog;
            private final SelectionAdapter adapter;
            private final DialogInterface.OnClickListener onClickListener;

            private OnChoicesClickListener(DialogInterface dialog, SelectionAdapter adapter, DialogInterface.OnClickListener onClickListener) {
                this.dialog = dialog;
                this.adapter = adapter;
                this.onClickListener = onClickListener;
            }

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                for (int i = 0; i < adapter.getCount(); i++) {
                    adapter.setChecked(i, false);
                }
                adapter.setChecked(position, true);
                adapter.notifyDataSetChanged();
                if (onClickListener != null) {
                    onClickListener.onClick(dialog, position);
                }
            }
        }

        private static final class OnOperationsClickListener implements AdapterView.OnItemClickListener {

            private final DialogInterface dialog;
            private final DialogInterface.OnClickListener onClickListener;

            private OnOperationsClickListener(DialogInterface dialog, DialogInterface.OnClickListener onClickListener) {
                this.dialog = dialog;
                this.onClickListener = onClickListener;
            }

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (onClickListener != null) {
                    onClickListener.onClick(dialog, position);
                }
                dialog.dismiss();
            }
        }

        private View generateSelectionsContentView(DialogInterface dialog) {
            SelectionAdapter selectionAdapter = new SelectionAdapter(context, options, optionChecks);
            ListView listView = new ListView(context);
            listView.setAdapter(selectionAdapter);
            listView.setDivider(new ColorDrawable(context.getResources().getColor(R.color.split_bar)));
            listView.setDividerHeight(1);
            listView.setOnItemClickListener(new OnSelectionsClickListener(dialog, selectionAdapter, onSelectionsListener));
            return listView;
        }

        private View generateChoicesContentView(DialogInterface dialog) {
            SelectionAdapter selectionAdapter = new SelectionAdapter(context, options, optionChecks);
            ListView listView = new ListView(context);
            listView.setAdapter(selectionAdapter);
            listView.setDivider(new ColorDrawable(context.getResources().getColor(R.color.split_bar)));
            listView.setDividerHeight(1);
            listView.setOnItemClickListener(new OnChoicesClickListener(dialog, selectionAdapter, onOperationsListener));
            return listView;
        }

        private View generateOperationsContentView(DialogInterface dialog) {
            SelectionAdapter selectionAdapter = new SelectionAdapter(context, options, null);
            ListView listView = new ListView(context);
            listView.setAdapter(selectionAdapter);
            listView.setDivider(new ColorDrawable(context.getResources().getColor(R.color.split_bar)));
            listView.setDividerHeight(1);
            listView.setOnItemClickListener(new OnOperationsClickListener(dialog, onOperationsListener));
            return listView;
        }

        private View generateLoadingContentView() {
            return layoutInflater.inflate(R.layout.dialog_loading, null);
        }

        public AlertDialog show() {
            //创建Dialog实例
            Dialog dialog = new Dialog(context, atBottom ? R.style.BottomDialogTheme : R.style.CenterDialogTheme);

            //根据需求构建View
            View contentView;
            switch (contentType) {
                case CONTENT_TYPE_MESSAGE:
                    contentView = generateMessageContentView();
                    break;
                case CONTENT_TYPE_SELECTIONS:
                    contentView = generateSelectionsContentView(dialog);
                    break;
                case CONTENT_TYPE_CHOICES:
                    contentView = generateChoicesContentView(dialog);
                    break;
                case CONTENT_TYPE_OPERATIONS:
                    contentView = generateOperationsContentView(dialog);
                    break;
                case CONTENT_TYPE_LOADING:
                    contentView = generateLoadingContentView();
                    break;
                default:
                    if (this.contentView == null) {
                        contentView = new View(context);
                    }
                    else {
                        contentView = this.contentView;
                    }
                    break;
            }

            //创建Dialog的View
            View dialogView = layoutInflater.inflate(atBottom ? R.layout.dialog_alert_bottom : R.layout.dialog_alert_center, null);
            TextView titleView = dialogView.findViewById(R.id.dialogTitle);
            if (title == null) {
                titleView.setVisibility(View.GONE);
            }
            else {
                titleView.setText(title);
            }
            FrameLayout contentViewContainer = dialogView.findViewById(R.id.dialogContentContainer);
            contentViewContainer.addView(contentView, contentLayoutParameters);
            View buttonArea = dialogView.findViewById(R.id.buttonArea);
            TextView positiveButton;
            TextView negativeButton;
            if (positiveButtonText == null && negativeButtonText == null) {
                buttonArea.setVisibility(View.GONE);
                positiveButton = null;
                negativeButton = null;
            }
            else {
                positiveButton = dialogView.findViewById(R.id.positiveButton);
                if (positiveButtonText == null) {
                    positiveButton.setVisibility(View.GONE);
                    positiveButton = null;
                }
                else {
                    positiveButton.setText(positiveButtonText);
                    positiveButton.setOnClickListener(new OnButtonClickListener(dialog, DialogInterface.BUTTON_POSITIVE, onDialogButtonClickListener));
                    positiveButton.setEnabled(positiveButtonEnabled);
                }
                negativeButton = dialogView.findViewById(R.id.negativeButton);
                if (negativeButtonText == null) {
                    negativeButton.setVisibility(View.GONE);
                    negativeButton = null;
                }
                else {
                    negativeButton.setText(negativeButtonText);
                    negativeButton.setOnClickListener(new OnButtonClickListener(dialog, DialogInterface.BUTTON_NEGATIVE, onDialogButtonClickListener));
                    negativeButton.setEnabled(negativeButtonEnabled);
                }
            }

            //dialog的其他参数
            if (context instanceof Activity) {
                dialog.setOwnerActivity((Activity) context);
            }
            dialog.getWindow().setGravity(atBottom ? Gravity.BOTTOM : Gravity.CENTER);
            dialog.setCancelable(cancelable);
            dialog.setCanceledOnTouchOutside(cancelable);
            dialog.setOnCancelListener(onCancelListener);
            dialog.setContentView(dialogView, new ViewGroup.LayoutParams(contentWidthInPixel, ViewGroup.LayoutParams.WRAP_CONTENT));

            dialog.show();
            return new AlertDialog(dialog, positiveButton, negativeButton);
        }
    }
}
