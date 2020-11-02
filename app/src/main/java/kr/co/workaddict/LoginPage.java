package kr.co.workaddict;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import kr.co.workaddict.Login.SessionCallback;

import kr.co.workaddict.R;

import kr.co.workaddict.Utility.UserInfo;
import kr.co.workaddict.Utility.Util;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.iid.FirebaseInstanceId;
import com.kakao.auth.AuthType;
import com.kakao.auth.Session;
import com.kakao.kakaolink.v2.KakaoLinkResponse;
import com.kakao.kakaolink.v2.KakaoLinkService;
import com.kakao.network.ErrorResult;
import com.kakao.network.callback.ResponseCallback;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import static com.kakao.util.maps.helper.Utility.getPackageInfo;


public class LoginPage extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private CallbackManager callbackManager;
    private SessionCallback callback;

    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;
    public ProgressDialog progressDialog;


    public static boolean isRegisterFinish = false;
    private static final String TAG = "LoginPage";
    private Button btn_login;
    private Button btn_moveToRegistration;
    public EditText etID;
    private EditText etPW;
    private ProgressBar progressBar;
    private FrameLayout loginButtonWrap;
    private CheckBox checkBoxAutoLogin;
    private TextView findID;
    private LinearLayout loginViewWrap;
    private LinearLayout findViewWrap;
    private long backBtnTime = 0;
    private IDFind idFind;
    private PWFind pwFind;
    public static LoginPage loginPage;
    public TextView idFindTextButton;
    public TextView pwFindTextButton;
    private ImageButton findBackButton;
    private Button googleLoginButton;
    private Button facebookLoginButton;
    private Button kakaoLoginButton;
    private Button naverLoginButton;


    @Override
    protected void onResume() {
        super.onResume();

        Log.e(TAG, "onResume: 로그인여부 : " + SaveSharedPreferences.getPrefIsLogin(this));

        if (isRegisterFinish) {

            Intent intent = getIntent();
            etID.setText(intent.getExtras().getString("ID"));
            login(intent.getExtras().getString("ID"), intent.getExtras().getString("PW"));
            isRegisterFinish = false;

        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);
        loginPage = this;
//        Util.saveContext(this);
        Log.d(TAG, "onCreate: hashKey" + getKeyHash(this));

        mAuth = FirebaseAuth.getInstance();
        SaveSharedPreferences.setPrefIsLogin(this, "n");

        etID = findViewById(R.id.etID);
        etPW = findViewById(R.id.etPW);
        findID = findViewById(R.id.findID);
        loginViewWrap = findViewById(R.id.loginViewWrap);
        findViewWrap = findViewById(R.id.findViewWrap);
        loginButtonWrap = findViewById(R.id.loginButtonWrap);
        checkBoxAutoLogin = findViewById(R.id.checkBoxAutoLogin);
        idFindTextButton = findViewById(R.id.idFindTextButton);
        pwFindTextButton = findViewById(R.id.pwFindTextButton);
        findBackButton = findViewById(R.id.findBackButton);

        googleLoginButton = findViewById(R.id.googleLoginButton);

        facebookLoginButton = findViewById(R.id.facebookLoginButton);
        kakaoLoginButton = findViewById(R.id.kakaoLoginButton);
        naverLoginButton = findViewById(R.id.naverLoginButton);

        progressBar = findViewById(R.id.progressBar);


        try {
            PackageInfo info = getPackageManager().getPackageInfo("kr.co.workaddict", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnSuccessListener(LoginPage.this, instanceIdResult -> {
                    String newToken = instanceIdResult.getToken();
                    Util.TOKEN = newToken;
                    Log.e("newToken", newToken);
                    Log.e("Util.Token", Util.TOKEN);
                });


        btn_login = findViewById(R.id.btn_login);
        btn_login.setOnClickListener(v -> {
            String strID = etID.getText().toString().trim();
            String strPW = etPW.getText().toString().trim();
            if (strID.isEmpty()) {
                Toast.makeText(this, "아이디를 입력해주세요", Toast.LENGTH_SHORT).show();
            } else if (strPW.isEmpty()) {
                Toast.makeText(this, "비밀번호를 입력해주세요", Toast.LENGTH_SHORT).show();
            } else {
                login(strID, strPW);

            }
        });

        btn_moveToRegistration = findViewById(R.id.btn_moveToRegistration);
        btn_moveToRegistration.setOnClickListener(v -> {
            Intent intent = new Intent(LoginPage.this, RegisterPageTerms.class);
            startActivity(intent);
        });


        findID.setOnClickListener(v -> {
            Log.e(TAG, "onCreate: findID 클릭");
            findViewWrap.setVisibility(View.VISIBLE);
            loginViewWrap.setVisibility(View.GONE);
            setIdFind();
        });

        idFindTextButton.setOnClickListener(v -> {
            Log.e(TAG, "onCreate: idFindTextButton 클릭");
            setIdFind();
        });

        pwFindTextButton.setOnClickListener(v -> {
            Log.e(TAG, "onCreate: pwFindTextButton 클릭");
            setPwFind();
        });

        findBackButton.setOnClickListener(v -> {
            onBackPressed();
        });


        googleLoginButton.setOnClickListener(v -> {
            googleSignIn();
        });

        facebookLoginButton.setOnClickListener(v -> {
            facebookLoginSetting();
        });


        //카카오 로그인 콜백 초기화
        Session session = Session.getCurrentSession();
        session.addCallback(new SessionCallback());
        //앱 실행 시 로그인 토큰이 있으면 자동으로 로그인 수행
//        Session.getCurrentSession().checkAndImplicitOpen();

        kakaoLoginButton.setOnClickListener(v -> {

            setProgressDialog();
            Session.getCurrentSession().open(AuthType.KAKAO_ACCOUNT, loginPage);
//            sessionCallback = new SessionCallback();
//            Session.getCurrentSession().addCallback(sessionCallback);

//            여기서, checkAndImplicitOpen()이란 함수가 눈에 띄실 겁니다.
//            이 함수는 현재 앱에 유효한 카카오 로그인 토큰이 있다면 바로 로그인을 시켜주는 함수입니다.
//            즉, 이전에 로그인한 기록이 있다면, 다음 번에 앱을 켰을 때 자동으로 로그인을 시켜주는 것이죠.
//            만약 저 함수를 주석처리하면 매 번 로그인 버튼을 눌러줘야 합니다.
        });

        naverLoginButton.setOnClickListener(v -> {
            Util.developingMessage(loginPage);

        });


        loginButtonEnableSetting();
        getPWEditTextCount();

        googleLoginSetting();

    }


    /**
     * 페이스북 로그인 세팅
     */
    private void facebookLoginSetting() {
        callbackManager = CallbackManager.Factory.create();

        LoginManager loginManager = LoginManager.getInstance();

        loginManager.logInWithReadPermissions(loginPage, Arrays.asList("email", "public_profile"));

        loginManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.e(TAG, "onSuccess: 페북 로그인 성공");
                handleFacebookAccessToken(loginResult.getAccessToken());

            }

            @Override
            public void onCancel() {
                Log.e(TAG, "onSuccess: 페북 로그인 실패");
            }

            @Override
            public void onError(FacebookException exception) {
                Log.e(TAG, "onSuccess: 페북 로그인 에러");
            }
        });

    }


    /**
     * 파이어베이스에 페이스북 아이디 확인 후 처리
     *
     * @param token
     */
    private void handleFacebookAccessToken(AccessToken token) {
        Log.e(TAG, "handleFacebookAccessToken:" + token);
        setProgressDialog();
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {

                        Log.e(TAG, "onComplete: 페이스북 userID : " + mAuth.getCurrentUser().getUid());
                        updateUI();
                        Log.e(TAG, "signInWithCredential:success");
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.e(TAG, "signInWithCredential:failure", task.getException());
                        Toast.makeText(LoginPage.this, "동일한 이메일 계정이 존재합니다.", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }

                });
    }

    /**
     * 구글 로그인 위해 필요한 세팅
     */
    private void googleLoginSetting() {

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


    }


    /**
     * 구글 로그인 인텐트
     */
    private void googleSignIn() {
        Log.e(TAG, "googleSignIn: ");
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e(TAG, "onActivityResult: 진입 ");
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                Log.e(TAG, "onActivityResult: 구글");
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Log.e(TAG, "onActivityResult: 구글로그인 에러 : " + e);
            }
        } else if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            Log.e(TAG, "onActivityResult: 카카오");
            super.onActivityResult(requestCode, resultCode, data);
            if (progressDialog != null) progressDialog.dismiss();
            return;

        } else {
            Log.e(TAG, "onActivityResult: 페이스북");
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }

        super.onActivityResult(requestCode, resultCode, data);


    }

    private void setProgressDialog() {
        Log.e(TAG, "setProgressDialog: ");
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("로그인중입니다");
        progressDialog.show();

    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        setProgressDialog();
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            Log.e(TAG, "onComplete: 구글 userID : " + mAuth.getCurrentUser().getUid());
                            Log.e(TAG, "onComplete: 구글 로그인 성공");

                            updateUI();

                        } else {
                            Log.e(TAG, "onComplete: 구글 로그인 실패");
                            // If sign in fails, display a message to the user.
//                            updateUI(null);
                        }
                    }
                });
    }


    private void updateUI() { //update ui code here
        FirebaseUser user = mAuth.getCurrentUser();
        Log.e(TAG, "updateUI: ");
        if (user != null) {
            Log.e(TAG, "updateUI: 존재");
            SaveSharedPreferences.setPrefAutoLogin(this, "y");
            SaveSharedPreferences.setPrefId(this, user.getEmail());
            if (SaveSharedPreferences.getDeleteLoginPhotoUrl(loginPage).equals("n")) {
                UserInfo.setLoginPhotoUrl(String.valueOf(user.getPhotoUrl()));
                SaveSharedPreferences.setLoginPhotoUrl(this, String.valueOf(user.getPhotoUrl()));
            } else {
                UserInfo.setLoginPhotoUrl("");
                SaveSharedPreferences.setLoginPhotoUrl(this, "");

            }


            UserInfo.setID(user.getEmail());
            UserInfo.setName(user.getDisplayName());
            UserInfo.setPhone(user.getPhoneNumber());

            Log.e(TAG, "updateUI: email : " + user.getEmail());
            Log.e(TAG, "updateUI: 이름 : " + user.getDisplayName());
            Log.e(TAG, "updateUI: phone : " + user.getPhoneNumber());
            Log.e(TAG, "updateUI: photo url : " + user.getPhotoUrl());
            Log.e(TAG, "updateUI: getUid : " + user.getUid());
            Log.e(TAG, "updateUI: getProviderId : " + user.getProviderId());

            setAutoLoginData(user);
            Intent intent = new Intent(LoginPage.this, BottomNavi.class);
            startActivity(intent);
            finish();
            progressDialog.dismiss();
        }


    }

    @Override
    public void onBackPressed() {
        if (findViewWrap.getVisibility() == View.VISIBLE) {
            findViewWrap.setVisibility(View.GONE);
            loginViewWrap.setVisibility(View.VISIBLE);


        } else {
            long curTime = System.currentTimeMillis();
            long gapTime = curTime - backBtnTime;
            if (0 <= gapTime && 2000 >= gapTime) {
                finish();
            } else {
                backBtnTime = curTime;
                Toast.makeText(this, "한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();
            }
        }

    }


    /**
     * 아이디 찾기 메소드 붙이기
     */
    private void setIdFind() {

        idFind = new IDFind(this);
        TextView findTitleText = findViewById(R.id.findTitleText);
        findTitleText.setText("아이디 찾기");
        idFindTextButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_selector_white));
        pwFindTextButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.background_soft_gray));

    }

    /**
     * 비밀번호 찾기 메소드
     */
    private void setPwFind() {
        pwFind = new PWFind(this);
        TextView findTitleText = findViewById(R.id.findTitleText);
        findTitleText.setText("비밀번호 찾기");
        idFindTextButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.background_soft_gray));
        pwFindTextButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_selector_white));

    }


    /**
     * 아이디랑 패스워스 count 0 이상일 때 로그인 버튼 활성화
     */
    private void loginButtonEnableSetting() {

        etID.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (count > 0) {
                    if (etPW.getText().length() > 0) {
                        btn_login.setEnabled(true);
                        loginButtonWrap.setBackgroundDrawable(getResources().getDrawable(R.drawable.back_purple_round_3dp));
                        btn_login.setTextColor(getResources().getColor(R.color.white));

                    } else {
                        btn_login.setEnabled(false);
                        loginButtonWrap.setBackgroundDrawable(getResources().getDrawable(R.drawable.back_gray_round_3dp));
                        btn_login.setTextColor(getResources().getColor(R.color.basic_gray));

                    }
                } else {
                    btn_login.setEnabled(false);
                    loginButtonWrap.setBackgroundDrawable(getResources().getDrawable(R.drawable.back_gray_round_3dp));
                    btn_login.setTextColor(getResources().getColor(R.color.basic_gray));

                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count > 0) {
                    if (etPW.getText().length() > 0) {
                        btn_login.setEnabled(true);
                        loginButtonWrap.setBackgroundDrawable(getResources().getDrawable(R.drawable.back_purple_round_3dp));
                        btn_login.setTextColor(getResources().getColor(R.color.white));
                    } else {
                        btn_login.setEnabled(false);
                        loginButtonWrap.setBackgroundDrawable(getResources().getDrawable(R.drawable.back_gray_round_3dp));
                        btn_login.setTextColor(getResources().getColor(R.color.basic_gray));

                    }
                } else {
                    btn_login.setEnabled(false);
                    loginButtonWrap.setBackgroundDrawable(getResources().getDrawable(R.drawable.back_gray_round_3dp));
                    btn_login.setTextColor(getResources().getColor(R.color.basic_gray));

                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                int count = s.length();
                if (count > 0) {
                    if (etPW.getText().length() > 0) {
                        btn_login.setEnabled(true);
                        loginButtonWrap.setBackgroundDrawable(getResources().getDrawable(R.drawable.back_purple_round_3dp));
                        btn_login.setTextColor(getResources().getColor(R.color.white));
                    } else {
                        btn_login.setEnabled(false);
                        loginButtonWrap.setBackgroundDrawable(getResources().getDrawable(R.drawable.back_gray_round_3dp));
                        btn_login.setTextColor(getResources().getColor(R.color.basic_gray));

                    }
                } else {
                    btn_login.setEnabled(false);
                    loginButtonWrap.setBackgroundDrawable(getResources().getDrawable(R.drawable.back_gray_round_3dp));
                    btn_login.setTextColor(getResources().getColor(R.color.basic_gray));

                }
            }
        });

    }

    /**
     * 아이디랑 패스워스 count 0 이상일 때 로그인 버튼 활성화
     */
    private void getPWEditTextCount() {
        etPW.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (count > 0) {
                    if (etID.getText().length() > 0) {
                        btn_login.setEnabled(true);
                        loginButtonWrap.setBackgroundDrawable(getResources().getDrawable(R.drawable.back_purple_round_3dp));
                        btn_login.setTextColor(getResources().getColor(R.color.white));
                    } else {
                        btn_login.setEnabled(false);
                        loginButtonWrap.setBackgroundDrawable(getResources().getDrawable(R.drawable.back_gray_round_3dp));
                        btn_login.setTextColor(getResources().getColor(R.color.basic_gray));

                    }

                } else {
                    btn_login.setEnabled(false);
                    loginButtonWrap.setBackgroundDrawable(getResources().getDrawable(R.drawable.back_gray_round_3dp));
                    btn_login.setTextColor(getResources().getColor(R.color.basic_gray));

                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count > 0) {
                    if (etID.getText().length() > 0) {
                        btn_login.setEnabled(true);
                        loginButtonWrap.setBackgroundDrawable(getResources().getDrawable(R.drawable.back_purple_round_3dp));
                        btn_login.setTextColor(getResources().getColor(R.color.white));
                    } else {
                        btn_login.setEnabled(false);
                        loginButtonWrap.setBackgroundDrawable(getResources().getDrawable(R.drawable.back_gray_round_3dp));
                        btn_login.setTextColor(getResources().getColor(R.color.basic_gray));

                    }

                } else {
                    btn_login.setEnabled(false);
                    loginButtonWrap.setBackgroundDrawable(getResources().getDrawable(R.drawable.back_gray_round_3dp));
                    btn_login.setTextColor(getResources().getColor(R.color.basic_gray));

                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                int count = s.length();

                if (count > 0) {
                    if (etID.getText().length() > 0) {
                        btn_login.setEnabled(true);
                        loginButtonWrap.setBackgroundDrawable(getResources().getDrawable(R.drawable.back_purple_round_3dp));
                        btn_login.setTextColor(getResources().getColor(R.color.white));
                    } else {
                        btn_login.setEnabled(false);
                        loginButtonWrap.setBackgroundDrawable(getResources().getDrawable(R.drawable.back_gray_round_3dp));
                        btn_login.setTextColor(getResources().getColor(R.color.basic_gray));
                    }

                } else {
                    btn_login.setEnabled(false);
                    loginButtonWrap.setBackgroundDrawable(getResources().getDrawable(R.drawable.back_gray_round_3dp));
                    btn_login.setTextColor(getResources().getColor(R.color.basic_gray));

                }

            }
        });
    }


    /**
     * 구글이나 페이스북 연동 없이 로그인
     *
     * @param id
     * @param pw
     */
    public void login(String id, String pw) {
        Log.e(TAG, "login: 로그인 진입");
        progressBar.setVisibility(View.VISIBLE);
        btn_login.setText("");
        btn_login.setEnabled(false);

        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("로그인중...");
        dialog.show();

        mAuth.signInWithEmailAndPassword(id, pw).addOnCompleteListener(LoginPage.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information

                    if (mAuth.getCurrentUser().isEmailVerified()) {


                        Log.e(TAG, "onComplete: userID : " + mAuth.getCurrentUser().getUid());

                        Log.e(TAG, "signInWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        String userEmail = user.getEmail();
                        String userName = user.getDisplayName();

                        UserInfo.setID(userEmail);
                        UserInfo.setName(userName);


                        if (checkBoxAutoLogin.isChecked()) {
                            SaveSharedPreferences.setPrefAutoLogin(LoginPage.this, "y");
                            SaveSharedPreferences.setPrefId(LoginPage.this, etID.getText().toString().trim());
                            SaveSharedPreferences.setPrefPw(LoginPage.this, etPW.getText().toString().trim());
                            SaveSharedPreferences.setPrefName(LoginPage.this, userName);
                        } else {
                            SaveSharedPreferences.setPrefAutoLogin(LoginPage.this, "n");
                            SaveSharedPreferences.setPrefId(LoginPage.this, "");
                            SaveSharedPreferences.setPrefPw(LoginPage.this, "");
                        }


                        Intent intent = new Intent(LoginPage.this, BottomNavi.class);
                        startActivity(intent);
                        finish();

                    } else {

                        Toast.makeText(loginPage, "가입하신 이메일에서 인증 후 이용하실 수 있습니다.", Toast.LENGTH_SHORT).show();
                    }


                } else {
                    // If sign in fails, display a message to the user.
                    Log.e(TAG, "signInWithEmail:failure", task.getException());

                    if (task.getException().toString().contains("The password is invalid or the user does not have a password")) {
                        Toast.makeText(LoginPage.this, "비밀번호를 확인해주세요", Toast.LENGTH_SHORT).show();
                    } else if (task.getException().toString().contains("There is no user record corresponding to this identifier")) {
                        Toast.makeText(LoginPage.this, "아이디를 확인해주세요", Toast.LENGTH_SHORT).show();
                    }

                }

                btn_login.setText("로그인");
                btn_login.setEnabled(true);
                progressBar.setVisibility(View.GONE);
                Util.hideKeyboard(loginPage);
                dialog.dismiss();
            }
        });


    }


    /**
     * 카카오 연동 로그인
     *
     * @param id
     * @param pw
     */
    public void login(String id, String pw, String name, String photoUrl) {
        Log.e(TAG, "login: 로그인 진입");
        progressBar.setVisibility(View.VISIBLE);
        btn_login.setText("");
        btn_login.setEnabled(false);

        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("로그인중...");
        dialog.show();

        mAuth.signInWithEmailAndPassword(id, pw).addOnCompleteListener(LoginPage.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information

                    Log.e(TAG, "signInWithEmail:success");
                    FirebaseUser user = mAuth.getCurrentUser();

                    UserInfo.setID(id);
                    UserInfo.setPW(pw);
                    UserInfo.setName(name);
                    UserInfo.setLoginPhotoUrl(photoUrl);


                    if (checkBoxAutoLogin.isChecked()) {
                        SaveSharedPreferences.setPrefAutoLogin(LoginPage.this, "y");
                        SaveSharedPreferences.setPrefId(LoginPage.this, id);
                        SaveSharedPreferences.setPrefPw(LoginPage.this, id);
                        SaveSharedPreferences.setPrefName(LoginPage.this, name);
                    } else {
                        SaveSharedPreferences.setPrefAutoLogin(LoginPage.this, "n");
                        SaveSharedPreferences.setPrefId(LoginPage.this, "");
                        SaveSharedPreferences.setPrefPw(LoginPage.this, "");
                    }


                    Intent intent = new Intent(LoginPage.this, BottomNavi.class);
                    startActivity(intent);
                    finish();


                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(LoginPage.this, "동일한 이메일 계정이 존재합니다.", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "signInWithEmail:failure", task.getException());
                    dialog.dismiss();

//                    if (task.getException().toString().contains("The password is invalid or the user does not have a password")) {
//                        Toast.makeText(LoginPage.this, "비밀번호를 확인해주세요", Toast.LENGTH_SHORT).show();
//                    } else if (task.getException().toString().contains("There is no user record corresponding to this identifier")) {
//                        Toast.makeText(LoginPage.this, "아이디를 확인해주세요", Toast.LENGTH_SHORT).show();
//                    }

                }

                btn_login.setText("로그인");
                btn_login.setEnabled(true);
                progressBar.setVisibility(View.GONE);
                Util.hideKeyboard(loginPage);
                dialog.dismiss();
            }
        });
    }


    /**
     * 자동로그인 설정
     *
     * @param user
     */
    private void setAutoLoginData(FirebaseUser user) {
        if (checkBoxAutoLogin.isChecked()) {
            String userEmail = user.getEmail();
            SaveSharedPreferences.setPrefAutoLogin(LoginPage.this, "y");
            SaveSharedPreferences.setPrefId(LoginPage.this, userEmail);

        } else {
            SaveSharedPreferences.setPrefAutoLogin(LoginPage.this, "n");
            SaveSharedPreferences.setPrefId(LoginPage.this, "");
        }
        SaveSharedPreferences.setPrefPw(LoginPage.this, "");
    }

    public static String getKeyHash(final Context context) {
        PackageInfo packageInfo = getPackageInfo(context, PackageManager.GET_SIGNATURES);
        if (packageInfo == null)
            return null;

        for (Signature signature : packageInfo.signatures) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                return Base64.encodeToString(md.digest(), Base64.NO_WRAP);
            } catch (NoSuchAlgorithmException e) {
                Log.w("TAG", "Unable to get MessageDigest. signature=" + signature, e);
            }
        }
        return null;
    }

    public void kakaoLink(String url) {

        KakaoLinkService.getInstance()
                .sendScrap(this, url, null, new ResponseCallback<KakaoLinkResponse>() {
                    @Override
                    public void onFailure(ErrorResult errorResult) {
                        Log.e("KAKAO_API", "카카오링크 공유 실패: " + errorResult);
                    }

                    @Override
                    public void onSuccess(KakaoLinkResponse result) {
                        Log.i("KAKAO_API", "카카오링크 공유 성공");

                        // 카카오링크 보내기에 성공했지만 아래 경고 메시지가 존재할 경우 일부 컨텐츠가 정상 동작하지 않을 수 있습니다.
                        Log.w("KAKAO_API", "warning messages: " + result.getWarningMsg());
                        Log.w("KAKAO_API", "argument messages: " + result.getArgumentMsg());
                    }
                });
    }


    @Override
    protected void onDestroy() {
        Log.e(TAG, "onDestroy: ");
        super.onDestroy();
//        Session.getCurrentSession().removeCallback(callback);

    }
}
