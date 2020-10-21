package kr.co.workaddict.MyPageFragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import kr.co.workaddict.R;
import kr.co.workaddict.Utility.Util;
import kr.co.workaddict.databinding.ActivityQuestionBinding;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

public class QuestionActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityQuestionBinding b;                               
    private BottomSheetBehavior behavior;
    private FAQPhoneBottomSheet faqPhoneBottomSheet;
//    private static final int PHONE_CALL_PERMISSION = 333;

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

        connectPhone();

    };

    private void connectPhone() {
        String company_tel = "tel:01030890122";
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(company_tel));
        startActivity(intent);
        behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }

    private final View.OnClickListener negativeListener = v -> {
        behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    };


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
