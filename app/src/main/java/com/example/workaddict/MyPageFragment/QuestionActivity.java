package com.example.workaddict.MyPageFragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.example.workaddict.BottomFragment.MyPageFragment;
import com.example.workaddict.R;
import com.example.workaddict.Utility.Util;
import com.example.workaddict.databinding.ActivityQuestionBinding;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

public class QuestionActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityQuestionBinding b;
    private BottomSheetBehavior behavior;
    private FAQPhoneBottomSheet faqPhoneBottomSheet;
    private static final int PHONE_CALL_PERMISSION = 333;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = DataBindingUtil.setContentView(this, R.layout.activity_question);
        behavior = BottomSheetBehavior.from(b.faqPhoneBottomSheet);
        behavior.setState(BottomSheetBehavior.STATE_HIDDEN);


        setupListener();
    }


    private void setupListener() {
        b.myPageToolbarWrap.setOnClickListener(this);
        b.questionPhone.setOnClickListener(this);
        b.faqCoverView.setOnClickListener(this);
        b.questionMail.setOnClickListener(this);
        b.questionFAQ.setOnClickListener(this);
        b.questionChat.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.myPageToolbarWrap:
                onBackPressed();
                break;

            case R.id.questionPhone:
                faqPhoneBottomSheet = new FAQPhoneBottomSheet(behavior, b.faqPhoneBottomSheet, positiveListener, negativeListener, b.faqCoverView);
                break;
            case R.id.faqCoverView:
                b.faqCoverView.setVisibility(View.GONE);
                behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                break;
            case R.id.questionMail:
                Intent email = new Intent(Intent.ACTION_SEND);
                email.setType("message/rfc822");
                email.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{getString(R.string.faqEmail)});
                email.putExtra(Intent.EXTRA_SUBJECT, "<" + getString(R.string.app_name) + " " + getString(R.string.faqEmailTitle) + ">");
                email.putExtra(Intent.EXTRA_TEXT,
                        "앱 버전 : " + getString(R.string.version) +
                                "\nOS 버전 : " + Build.VERSION.RELEASE +
                                "\n모델명 : " + Build.MODEL +
                                "\n문의내용 : \n " + getString(R.string.faqEmailContent));
                startActivity(email);
                break;
            case R.id.questionFAQ:
            case R.id.questionChat:
                Util.developingMessage(this);
                break;

        }
    }

    private View.OnClickListener positiveListener = v -> {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, PHONE_CALL_PERMISSION);

        } else {

            connectPhone();

        }

    };

    private void connectPhone() {
        String company_tel = "tel:01030890122";
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(company_tel));
        startActivity(intent);
        behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }

    private View.OnClickListener negativeListener = v -> {
        behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    };


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PHONE_CALL_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                connectPhone();
            } else {
                Toast.makeText(this, "권한을 거부하였습니다", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onBackPressed() {
        if (behavior.getState() == BottomSheetBehavior.STATE_EXPANDED ||
                behavior.getState() == BottomSheetBehavior.STATE_SETTLING ||
                behavior.getState() == BottomSheetBehavior.STATE_HALF_EXPANDED) {
            behavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        } else {

            super.onBackPressed();

        }
    }
}
