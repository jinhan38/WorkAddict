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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

class IDFind implements View.OnClickListener {

    private static final String TAG = "IdAndPwFind";
    private Activity activity;
    private EditText idFindPhone;
    private EditText idFindName;
    private Button idFindButton;
    private ProgressBar progressBarID;
    private LinearLayout idFindEditTextWrap;
    private LinearLayout idFindResultWrap;
    private TextView idFindResult;
    private FrameLayout idFindButtonWrap;
    private boolean exist = false;


    private View idFindWrap;
    private View pwFindWrap;

    public IDFind(Activity activity) {
        this.activity = activity;
        initView();

    }



    private void initView() {

        idLayoutView();
        setupListener();
    }


    private void idLayoutView() {

        idFindPhone = activity.findViewById(R.id.idFindPhone);
        idFindName = activity.findViewById(R.id.idFindName);
        idFindButton = activity.findViewById(R.id.idFindButton);
        progressBarID = activity.findViewById(R.id.progressBarID);
        idFindEditTextWrap = activity.findViewById(R.id.idFindEditTextWrap);
        idFindResultWrap = activity.findViewById(R.id.idFindResultWrap);
        idFindResult = activity.findViewById(R.id.idFindResult);
        idFindWrap = activity.findViewById(R.id.idFindWrap);
        pwFindWrap = activity.findViewById(R.id.pwFindWrap);
        idFindButtonWrap = activity.findViewById(R.id.idFindButtonWrap);

        idFindWrap.setVisibility(View.VISIBLE);
        pwFindWrap.setVisibility(View.GONE);
        idFindPhone.setText("");
        idFindName.setText("");
        idFindEditTextWrap.setVisibility(View.VISIBLE);
        idFindResultWrap.setVisibility(View.GONE);
        idFindResult.setText("");
        idFindButtonWrap.setVisibility(View.VISIBLE);
        idFindButton.setEnabled(true);
    }



    private void setupListener() {
        idFindButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.idFindButton:
                Log.e(TAG, "onClick: 아이디찾기 클릭");
                idFindInspection();
                break;
        }
    }


    /**
     * 입력한 아이디와 이름을 검색해서 파이어베이스에 해당 privacy가 있는지 확인하자.
     */
    private void idFindInspection() {

        Util.hideKeyboard(activity);
        String phone = idFindPhone.getText().toString().trim();
        String name = idFindName.getText().toString().trim();

        if (phone.length() == 0) {
            Toast.makeText(activity, "연락처를 입력해주세요", Toast.LENGTH_SHORT).show();
        } else if (name.length() == 0) {
            Toast.makeText(activity, "이름을 입력해주세요", Toast.LENGTH_SHORT).show();
        } else {

            progressBarID.setVisibility(View.VISIBLE);
            idFindButton.setText("");
            Log.e(TAG, "idFindInspection: 텍스트 초기화" );
            idFindButton.setEnabled(false);


            Util.getFirebaseDatabase().getReference().child("users").child("Privacy").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            Privacy privacy = postSnapshot.getValue(Privacy.class);
                            if (privacy.getPhone().equals(phone) && privacy.getName().equals(name)) {
                                Log.e(TAG, "onDataChange: 일치하는 정보 찾음 아이디는 : " + privacy.getId());
                                exist = true;
                                idFindResultWrap.setVisibility(View.VISIBLE);
                                idFindEditTextWrap.setVisibility(View.GONE);
                                idFindResult.setText(privacy.getId());

                                if (LoginPage.loginPage != null ){
                                    LoginPage.loginPage.etID.setText(privacy.getId());
                                }
                            }
                        }

                    } else {
                        Toast.makeText(activity, "다시 시도해주시기 바랍니다.", Toast.LENGTH_SHORT).show();
                    }


                    if (exist) {
                        idFindButtonWrap.setVisibility(View.GONE);
                    } else {
                        idFindButton.setEnabled(true);
                        idFindButtonWrap.setVisibility(View.VISIBLE);
                        Toast.makeText(activity, "일치하는 정보가 없습니다.", Toast.LENGTH_SHORT).show();
                    }

                    progressBarID.setVisibility(View.GONE);
                    idFindButton.setText("아이디 찾기");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e(TAG, "onCancelled: 실패");
                    progressBarID.setVisibility(View.GONE);
                    idFindButton.setText("아이디 찾기");
                    idFindButton.setEnabled(true);
                }
            });
        }
    }
}
