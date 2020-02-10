package com.htt.kon.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.htt.kon.R;
import com.htt.kon.util.stream.Optional;

/**
 * 封装了包含两个按钮的对话框
 *
 * @author su
 * @date 2020/02/10 13:07
 */
public class OptionDialog {
    private Context context;

    private AlertDialog dialog;

    private View view;

    private TextView textViewMessage;

    private TextView textViewPositiveBtn;

    private TextView textViewNegativeBtn;

    public static OptionDialog of(Context context, String content) {
        OptionDialog instance = new OptionDialog();
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_option, null);
        TextView textViewMessage = view.findViewById(R.id.do_textViewMessage);
        TextView textViewPositiveBtn = view.findViewById(R.id.do_textViewPositiveBtn);
        TextView textViewNegativeBtn = view.findViewById(R.id.do_textViewNegativeBtn);
        textViewPositiveBtn.setVisibility(View.GONE);
        textViewNegativeBtn.setVisibility(View.GONE);

        textViewMessage.setText(content);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);

        instance.dialog = builder.create();
        instance.context = context;
        instance.view = view;
        instance.textViewMessage = textViewMessage;
        instance.textViewPositiveBtn = textViewPositiveBtn;
        instance.textViewNegativeBtn = textViewNegativeBtn;
        return instance;
    }

    public OptionDialog setPositiveButton(final OnClickListener listener) {
        return this.setPositiveButton(this.context.getString(R.string.determine), listener);
    }

    public OptionDialog setPositiveButton(String text, final OnClickListener listener) {
        this.textViewPositiveBtn.setText(text);
        this.textViewPositiveBtn.setVisibility(View.VISIBLE);
        this.textViewPositiveBtn.setOnClickListener(v -> {
            this.dialog.dismiss();
            Optional.of(listener).ifPresent(OnClickListener::onClick);
        });
        return this;
    }

    public OptionDialog setNegativeButton(final OnClickListener listener) {
        return this.setNegativeButton(this.context.getString(R.string.cancel), listener);
    }

    public OptionDialog setNegativeButton(String text, final OnClickListener listener) {
        this.textViewNegativeBtn.setText(text);
        this.textViewNegativeBtn.setVisibility(View.VISIBLE);
        this.textViewNegativeBtn.setOnClickListener(v -> {
            this.dialog.dismiss();
            Optional.of(listener).ifPresent(OnClickListener::onClick);
        });
        return this;
    }

    public OptionDialog show() {
        this.dialog.show();
        return this;
    }

    private OptionDialog() {
    }

    public interface OnClickListener {
        void onClick();
    }

}
