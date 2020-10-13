package com.example.workaddict.Login;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;

import com.example.workaddict.R;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.SignInButton;
import com.google.firebase.auth.FirebaseAuth;

public class GoogleLogin extends AppCompatActivity {
    private FirebaseAuth mAuth ;
    private Button btn_logout;
    private Button btn_revoke;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_login);

        mAuth = FirebaseAuth.getInstance();

        btn_logout = findViewById(R.id.btn_logout);
        btn_revoke = findViewById(R.id.btn_revoke);
        
        btn_logout.setOnClickListener(v ->{
            signOut();
        });

        btn_revoke.setOnClickListener(v ->{
            revokeAccess();
        });
    }

    private void signOut() {
        FirebaseAuth.getInstance().signOut();
        finish();
    }

    private void revokeAccess() {
        mAuth.getCurrentUser().delete();
        finish();
    }
}