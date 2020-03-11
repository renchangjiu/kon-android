package com.htt.kon.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import com.htt.kon.R;
import com.htt.kon.function.Function1;
import com.htt.kon.util.TextWatcherWrapper;
import com.htt.kon.util.stream.Optional;

import org.apache.commons.lang3.StringUtils;

import lombok.Getter;

/**
 * 封装了对话框, 包含两个按钮, 可以设置 message 或 view (二选一)
 *
 * @author su
 * @date 2020/02/10 13:07
 */
public class OptionDialog {

    private Context context;

    @Getter
    private AlertDialog dialog;

    private FrameLayout container;

    @Getter
    private View child;

    private TextView message;

    @Getter
    private TextView positiveBtn;
    private OnClickListener positiveListener;

    @Getter
    private TextView negativeBtn;
    private OnClickListener negativeListener;


    public static OptionDialog of(Context context) {
        OptionDialog of = new OptionDialog();
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_option, null);
        of.context = context;
        of.container = view.findViewById(R.id.do_frameLayout);
        of.message = view.findViewById(R.id.do_textViewMessage);
        of.positiveBtn = view.findViewById(R.id.do_textViewPositiveBtn);
        of.negativeBtn = view.findViewById(R.id.do_textViewNegativeBtn);
        of.dialog = new AlertDialog.Builder(context).setView(view).create();
        return of;
    }

    /**
     * 为创建歌单弹出框预定义的方法
     *
     * @param context          context
     * @param defMusicListName default music list name
     * @param func             callback function
     */
    public static void ofCreateMusicList(Context context, @Nullable String defMusicListName, @NonNull Function1<String, Void> func) {
        OptionDialog of = OptionDialog.of(context)
                .setChild(LayoutInflater.from(context).inflate(R.layout.dialog_child_create_music_list, null))
                .setTitle(context.getString(R.string.create_music_list))
                .disabled(DialogInterface.BUTTON_POSITIVE)
                .setPositiveButton(context.getString(R.string.submit), (child) -> {
                    EditText et = child.findViewById(R.id.dccml_editText);
                    String name = et.getText().toString();
                    func.invoke(name);
                })
                .setNegativeButton(child -> {
                })
                .end();
        EditText et = of.getChild().findViewById(R.id.dccml_editText);
        et.addTextChangedListener(new TextWatcherWrapper() {
            @Override
            public void afterTextChanged(Editable s) {
                String str = s.toString();
                if (StringUtils.isNotEmpty(str)) {
                    of.enabled(DialogInterface.BUTTON_POSITIVE);
                } else {
                    of.disabled(DialogInterface.BUTTON_POSITIVE);
                }
            }
        });
        et.setText(defMusicListName);
        of.show();
        of.show();
    }


    public OptionDialog setTitle(CharSequence title) {
        this.dialog.setTitle(title);
        return this;
    }

    public OptionDialog setContent(String content) {
        this.message.setText(content);
        this.message.setVisibility(View.VISIBLE);
        return this;
    }

    public OptionDialog setChild(View child) {
        this.container.addView(child);
        this.child = child;
        return this;
    }


    public OptionDialog disabled(int whichButton) {
        TextView tv = whichButton == DialogInterface.BUTTON_NEGATIVE ? this.negativeBtn : this.positiveBtn;
        tv.setTextColor(ContextCompat.getColor(this.context, R.color.pink));
        tv.setOnClickListener(null);
        tv.setClickable(false);
        return this;
    }

    public OptionDialog enabled(int whichButton) {
        TextView tv = whichButton == DialogInterface.BUTTON_NEGATIVE ? this.negativeBtn : this.positiveBtn;
        tv.setTextColor(ContextCompat.getColor(this.context, R.color.red1));
        this.setClickListener(tv, whichButton == DialogInterface.BUTTON_NEGATIVE ? this.negativeListener : this.positiveListener);
        return this;
    }

    public OptionDialog setPositiveButton(@NonNull final OnClickListener listener) {
        return this.setPositiveButton(this.context.getString(R.string.determine), listener);
    }

    public OptionDialog setPositiveButton(@NonNull String text, final OnClickListener listener) {
        this.positiveListener = listener;
        this.positiveBtn.setText(text);
        this.positiveBtn.setVisibility(View.VISIBLE);
        if (this.positiveBtn.isClickable()) {
            this.setClickListener(this.positiveBtn, listener);
        }
        return this;
    }

    public OptionDialog setNegativeButton(@NonNull final OnClickListener listener) {
        return this.setNegativeButton(this.context.getString(R.string.cancel), listener);
    }

    public OptionDialog setNegativeButton(@NonNull String text, final OnClickListener listener) {
        this.negativeListener = listener;
        this.negativeBtn.setText(text);
        this.negativeBtn.setVisibility(View.VISIBLE);
        if (this.negativeBtn.isClickable()) {
            this.setClickListener(this.negativeBtn, listener);
        }
        return this;
    }

    private void setClickListener(TextView btn, final OnClickListener listener) {
        btn.setOnClickListener(v -> {
            this.dialog.dismiss();
            Optional.of(listener).ifPresent(vv -> vv.onClick(this.child != null ? this.child : this.message));
        });
    }

    public OptionDialog end() {
        return this;
    }

    public OptionDialog show() {
        if (StringUtils.isNotEmpty(this.message.getText()) && this.child != null) {
            throw new RuntimeException();
        }
        this.negativeBtn.setVisibility(this.negativeListener != null ? View.VISIBLE : View.GONE);
        this.positiveBtn.setVisibility(this.positiveListener != null ? View.VISIBLE : View.GONE);
        this.dialog.show();
        return this;
    }

    private OptionDialog() {
    }

    public interface OnClickListener {
        void onClick(View child);
    }

}
