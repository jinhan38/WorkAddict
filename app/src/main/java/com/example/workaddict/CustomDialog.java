package com.example.workaddict;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

public class CustomDialog extends Dialog {

    private Button bt_logout_yes;
    private Button bt_logout_no;
    private View.OnClickListener positiveListener;
    private View.OnClickListener negativeListener;
    private TextView tv_contents;
    private String content;
    public static ProgressBar progressBar;
    public static LinearLayout ll_customDialog_button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_dialog);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        bt_logout_yes = findViewById(R.id.bt_logout_yes);
        bt_logout_no = findViewById(R.id.bt_logout_no);

        bt_logout_yes.setOnClickListener(positiveListener);
        bt_logout_no.setOnClickListener(negativeListener);

        tv_contents = findViewById(R.id.tv_contents);
        tv_contents.setText(content);
    }




    public CustomDialog(@NonNull Context context, View.OnClickListener positiveListener, View.OnClickListener negativeListener, String content) {
        super(context);
        this.positiveListener = positiveListener;
        this.negativeListener = negativeListener;
        this.content = content;
    }
}

