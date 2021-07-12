package com.rabunabi.friends.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import android.view.View;

import com.rabunabi.friends.R;

public class DialogMessage extends Dialog implements View.OnClickListener {
    protected AppCompatTextView tvMessage;
    protected AppCompatTextView tvYes;

    private OnDialogMessageListener mListener;
    private String message;
    private String textYes;

    public DialogMessage(@NonNull Context context) {
        super(context, R.style.DialogMessageStyle);
    }

    public DialogMessage(@NonNull Context context, String message,
                         String textYes, OnDialogMessageListener mListener) {
        super(context, R.style.DialogMessageStyle);
        this.mListener = mListener;
        this.message = message;
       // this.textYes = textYes;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_dialog_message);
        findView();
        initListener();
        tvMessage.setText(message != null ? message : "");
        tvYes.setText(textYes != null ? textYes : "");
    }

    private void initListener() {
        tvYes.setOnClickListener(this);
    }

    private void findView() {
        tvMessage = findViewById(R.id.tvMessage);
        tvYes = findViewById(R.id.tvYes);
    }

    public void yesClick() {
        if (mListener != null) {
            mListener.yesClick();
        }
        dismiss();
    }

    public void noClick() {
        dismiss();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tvYes) {
            yesClick();
        }
    }

    public interface OnDialogMessageListener {
        void yesClick();
    }
}
