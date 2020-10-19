package kr.co.workaddict;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import kr.co.workaddict.R;

import kr.co.workaddict.Utility.UserInfo;
import kr.co.workaddict.Utility.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Hashtable;

public class Registration extends AppCompatActivity {

    private static final String TAG = "Registration";
    private EditText et_id_registration;
    private EditText et_pw_registration;
    private EditText et_pw_registration_confirm;
    private EditText et_name_registration;
    private Button btn_registration_complete;
    private FirebaseAuth mAuth;
    private Registration registration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        Util.saveContext(this);
        registration = this;
        mAuth = FirebaseAuth.getInstance();
        et_id_registration = findViewById(R.id.et_id_registration);
        et_pw_registration = findViewById(R.id.et_pw_registration);
        et_pw_registration_confirm = findViewById(R.id.et_pw_registration_confirm);
        et_name_registration = findViewById(R.id.et_name_registration);
        btn_registration_complete = findViewById(R.id.btn_registration_complete);
        btn_registration_complete.setEnabled(true);
        setupListener();

    }


    private void setupListener() {
        btn_registration_complete.setOnClickListener(v -> {

            int errNum = 0;

            if (et_id_registration.length() == 0) {
                ++errNum;
                et_id_registration.requestFocus();
                Util.showToast(getApplicationContext(), "이메일 형식 아이디를 입력해주세요");
            } else if (7 >= et_pw_registration.length()) {
                ++errNum;
                et_pw_registration.requestFocus();
                Util.showToast(getApplicationContext(), "비밀번호를 8자리 이상 입력해주세요");
            } else if (7 >= et_pw_registration.length()) {
                ++errNum;
                et_pw_registration_confirm.requestFocus();
                Util.showToast(getApplicationContext(), "비밀번호를 8자리 이상 입력해주세요");
            } else if (!et_pw_registration_confirm.getText().toString().equals(et_pw_registration.getText().toString())) {
                ++errNum;
                et_pw_registration_confirm.requestFocus();
                Util.showToast(getApplicationContext(), "비밀번호가 일치하지 않습니다.");
            } else if (1 >= et_name_registration.length()) {
                ++errNum;
                et_name_registration.requestFocus();
                Util.showToast(getApplicationContext(), "이름을 입력해주세요");
            }  else {

                final ProgressDialog dialog = new ProgressDialog(this);
                dialog.setMessage("회원가입이 진행중입니다");
                dialog.show();

                btn_registration_complete.setText("가입중");
                btn_registration_complete.setEnabled(false);

                Util.hideKeyboard(Registration.this);

                String strID = et_id_registration.getText().toString().trim();
                String strPW = et_pw_registration_confirm.getText().toString().trim();
                String strName = et_name_registration.getText().toString().trim();

                UserInfo.setID(strID);
                UserInfo.setPW(strPW);
                UserInfo.setName(strName);

                final boolean[] firstRegistration = {false};
                final boolean[] secondRegistration = {false};

                mAuth.createUserWithEmailAndPassword(strID, strPW).addOnCompleteListener(Registration.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Log.e(TAG, "onComplete: 이메일 발송  ");

                                    if (task.isSuccessful()) {

                                        Log.e(TAG, "onComplete: 이메일 발송 성공 ");
                                        
                                        DatabaseReference myRef = Util.getFirebaseDatabase().getReference("users")
                                                .child(UserInfo.getID().replaceAll("\\.", ""))
                                                .child("Privacy").push();
                                        Calendar cl = Calendar.getInstance();
                                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd:hh:mm:ss a");
                                        String dateTime = dateFormat.format(cl.getTime());

                                        Hashtable<String, String> sendText = new Hashtable<String, String>();
                                        sendText.put("id", strID);
                                        sendText.put("password", strPW);
                                        sendText.put("name", strName);
                                        sendText.put("phone", "");
                                        sendText.put("dateOfBirth", "");
                                        sendText.put("date", dateTime);

                                        UserInfo.setID(strID);
                                        UserInfo.setPW(strPW);
                                        UserInfo.setName(strName);
                                        myRef.setValue(sendText)
                                                .addOnSuccessListener(aVoid -> {
                                                    firstRegistration[0] = true;
                                                    Log.e(TAG, "첫번째 회원가입 성공 ");
                                                    btn_registration_complete.setText("회원가입");
                                                    btn_registration_complete.setEnabled(true);
                                                    Intent intent = new Intent(registration, LoginPage.class);
                                                    intent.putExtra("ID", strID);
                                                    intent.putExtra("PW", et_pw_registration.getText().toString().trim());
                                                    LoginPage.isRegisterFinish = true;
                                                    startActivity(intent);
                                                }).addOnFailureListener(e -> {

                                            Log.e(TAG, "회원가입 실패 ");
                                            btn_registration_complete.setText("회원가입");
                                            btn_registration_complete.setEnabled(true);
                                            dialog.dismiss();

                                        });


                                        DatabaseReference myRef2 = Util.getFirebaseDatabase().getReference("users").child("Privacy").push();
                                        Calendar cl2 = Calendar.getInstance();
                                        SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd:hh:mm:ss a");
                                        String dateTime2 = dateFormat2.format(cl2.getTime());

                                        Hashtable<String, String> sendText2 = new Hashtable<String, String>();
                                        sendText2.put("id", strID);
                                        sendText2.put("password", strPW);
                                        sendText2.put("name", strName);
                                        sendText2.put("date", dateTime2);

                                        myRef2.setValue(sendText2)
                                                .addOnSuccessListener(aVoid2 -> {
                                                    secondRegistration[0] = true;
                                                    Log.e(TAG, "두번째 회원가입 성공 ");

                                                }).addOnFailureListener(e -> {

                                            Log.e(TAG, "회원가입 실패 ");
                                            btn_registration_complete.setText("회원가입");
                                            btn_registration_complete.setEnabled(true);
                                            dialog.dismiss();

                                        });


                                        final Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (firstRegistration[0] && secondRegistration[0]) {

                                                    Log.e(TAG, "run: 둘다 트루 ");

                                                    finish();
                                                    handler.postDelayed(this, 100);
                                                    return;
                                                }
                                            }
                                        }, 100);

                                    }

                                }

                            }).addOnFailureListener(e -> Log.e(TAG, "onFailure: 이메일 발송 실패 : " + e));
                            


                        } else {

                            btn_registration_complete.setText("회원가입");
                            btn_registration_complete.setEnabled(true);
                            Log.e(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(Registration.this, "이미 존재하는 아이디입니다.", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }

                    }

                });



            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
//        updateUI(currentUser);
    }
}
