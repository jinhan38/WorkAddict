package com.example.workaddict;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.workaddict.DataClass.Privacy;
import com.example.workaddict.Utility.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

class PWFind implements View.OnClickListener {

    private static final String TAG = "PWFind";
    private Activity activity;


//    private TextView idFindResult;

    private EditText pwFindID;
    private EditText pwFindName;
    private FrameLayout pwFindButtonWrap;
    private Button pwFindButton;
    private ProgressBar progressBarPW;
    private TextView pwFindResult;
    private LinearLayout pwFindResultWrap;
    private LinearLayout pwFindEditTextWrap;

    private View pwFindWrap;
    private View idFindWrap;
    private boolean exist = false;

    public PWFind(Activity activity) {
        this.activity = activity;
        initView();

    }


    private void initView() {
        Log.e(TAG, "initView: ");


        pwLayoutView();
        setupListener();
    }


    private void pwLayoutView() {
        pwFindID = activity.findViewById(R.id.pwFindID);
        pwFindName = activity.findViewById(R.id.pwFindName);
        pwFindButtonWrap = activity.findViewById(R.id.pwFindButtonWrap);
        progressBarPW = activity.findViewById(R.id.progressBarPW);
        pwFindButton = activity.findViewById(R.id.pwFindButton);
        pwFindResult = activity.findViewById(R.id.pwFindResult);
        pwFindResultWrap = activity.findViewById(R.id.pwFindResultWrap);
        pwFindEditTextWrap = activity.findViewById(R.id.pwFindEditTextWrap);
        pwFindWrap = activity.findViewById(R.id.pwFindWrap);
        idFindWrap = activity.findViewById(R.id.idFindWrap);

        pwFindButtonWrap.setVisibility(View.VISIBLE);
        pwFindEditTextWrap.setVisibility(View.VISIBLE);
        pwFindResultWrap.setVisibility(View.GONE);
        pwFindResult.setText("");
        pwFindWrap.setVisibility(View.VISIBLE);
        idFindWrap.setVisibility(View.GONE);
        pwFindID.setText("");
        pwFindName.setText("");
        pwFindButtonWrap.setVisibility(View.VISIBLE);
        pwFindButton.setEnabled(true);
    }


    private void setupListener() {
        pwFindButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pwFindButton:
                Log.e(TAG, "onClick: 비밀번호 찾기 클릭");
                resetPassword();
                break;
        }
    }


    private void resetPassword() {

        Util.hideKeyboard(activity);
        String id = pwFindID.getText().toString().trim();
        String name = pwFindName.getText().toString().trim();


        if (id.length() == 0) {
            Toast.makeText(activity, "아이디를 입력해주세요", Toast.LENGTH_SHORT).show();
        } else if (name.length() == 0) {
            Toast.makeText(activity, "이름을 입력해주세요", Toast.LENGTH_SHORT).show();
        } else {



            progressBarPW.setVisibility(View.VISIBLE);
            pwFindButton.setText("");
            pwFindButton.setEnabled(false);
            Util.getFirebaseDatabase().getReference().child("users").child("Privacy").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            Privacy privacy = postSnapshot.getValue(Privacy.class);
                            if (privacy.getId().equals(id) && privacy.getName().equals(name)) {
                                Log.e(TAG, "onDataChange: 일치하는 정보 찾음 아이디는 : " + privacy.getId());
                                exist = true;
                                pwFindResultWrap.setVisibility(View.VISIBLE);
                                pwFindEditTextWrap.setVisibility(View.GONE);
                                pwFindButtonWrap.setVisibility(View.GONE);
                                pwFindResult.setText(privacy.getId());

                                //메일 발송
                                sendEmail(privacy.getId());

                                if (LoginPage.loginPage != null) {
                                    LoginPage.loginPage.etID.setText(privacy.getId());
                                }
                            }
                        }

                    } else {
                        Toast.makeText(activity, "다시 시도해주시기 바랍니다.", Toast.LENGTH_SHORT).show();
                    }


                    if (exist) {
                        pwFindButtonWrap.setVisibility(View.GONE);
                    } else {
                        pwFindButton.setEnabled(true);
                        pwFindButtonWrap.setVisibility(View.VISIBLE);
                        Toast.makeText(activity, "일치하는 정보가 없습니다.", Toast.LENGTH_SHORT).show();
                    }

                    progressBarPW.setVisibility(View.GONE);
                    pwFindButton.setText("비밀번호 찾기");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e(TAG, "onCancelled: 실패");
                    progressBarPW.setVisibility(View.GONE);
                    pwFindButton.setText("비밀번호 찾기");
                    pwFindButton.setEnabled(true);
                }
            });
        }
    }


    private void sendEmail(String email) {

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Log.e(TAG, "onSuccess: 이메일 발송" );
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "onFailure: Exception e : " + e );
                Log.e(TAG, "onFailure: 이메일 발송 실패" );
            }
        });



    }
}
