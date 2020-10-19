package kr.co.workaddict.Login;


import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import kr.co.workaddict.BottomNavi;
import kr.co.workaddict.LoginPage;
import kr.co.workaddict.SaveSharedPreferences;
import kr.co.workaddict.Utility.UserInfo;

import com.kakao.auth.ApiErrorCode;
import com.kakao.auth.ISessionCallback;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;
import com.kakao.util.OptionalBoolean;
import com.kakao.util.exception.KakaoException;

public class SessionCallback implements ISessionCallback {
    private static final String TAG = "SessionCallback";
    private Activity activity = LoginPage.loginPage;


    // 로그인에 성공한 상태

    @Override

    public void onSessionOpened() {      //세션이 성공적으로 열림
        Log.e(TAG, "onSessionOpened: ");
        UserManagement.getInstance().me(new MeV2ResponseCallback() {
            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                int result = errorResult.getErrorCode(); //오류 코드를 받아온다.

                if (result == ApiErrorCode.CLIENT_ERROR_CODE) { //클라이언트 에러인 경우: 네트워크 오류
                    Toast.makeText(activity, "네트워크 연결이 불안정합니다. 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                } else { //클라이언트 에러가 아닌 경우: 기타 오류
                    Toast.makeText(activity, "로그인 도중 오류가 발생했습니다: " + errorResult.getErrorMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onSuccess(MeV2Response result) {  //유저 정보를 가져오는데 성공한 경우


                String needsScopeAutority = ""; //이메일, 성별, 연령대, 생일 정보 가져오는 권한 체크용
                if (result.getKakaoAccount().needsScopeAccountEmail()) { //이메일 정보를 가져오는 데 사용자가 동의하지 않은 경우
                    needsScopeAutority = needsScopeAutority + "이메일";
                }

//                if (result.getKakaoAccount().needsScopeGender()) { //성별 정보를 가져오는 데 사용자가 동의하지 않은 경우
//                    needsScopeAutority = needsScopeAutority + ", 성별";
//                }
//                if (result.getKakaoAccount().needsScopeAgeRange()) { //연령대 정보를 가져오는 데 사용자가 동의하지 않은 경우
//                    needsScopeAutority = needsScopeAutority + ", 연령대";
//                }
//                if (result.getKakaoAccount().needsScopeBirthday()) { //생일 정보를 가져오는 데 사용자가 동의하지 않은 경우
//                    needsScopeAutority = needsScopeAutority + ", 생일";
//                }


                Log.e(TAG, "onSuccess: profile : " + result.getKakaoAccount().getProfile().getNickname());
                Log.e(TAG, "onSuccess: profile : " + result.getKakaoAccount().getProfile().getProfileImageUrl());
                Log.e(TAG, "onSuccess: profile : " + result.getKakaoAccount().getEmail());

                Log.e(TAG, "onSuccess: 로그인 여부 : " + SaveSharedPreferences.getPrefIsLogin(activity) );
                String id = result.getKakaoAccount().getEmail();
                String name = result.getKakaoAccount().getProfile().getNickname();
                String imageUrl = result.getKakaoAccount().getProfile().getProfileImageUrl();

                UserInfo.setID(id);
                UserInfo.setName(name);
                UserInfo.setLoginPhotoUrl(imageUrl);


                SaveSharedPreferences.setPrefAutoLogin(activity, "y");
                SaveSharedPreferences.setPrefId(activity, id);
                SaveSharedPreferences.setPrefName(activity, name);



                Intent intent = new Intent(activity, BottomNavi.class);
                activity.startActivity(intent);

//                LoginPage.loginPage.login(result.getKakaoAccount().getEmail(),
//                        Util.allPassword,
//                        result.getKakaoAccount().getProfile().getNickname(),
//                        result.getKakaoAccount().getProfile().getProfileImageUrl());

//                FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();


//                if (needsScopeAutority.length() != 0) {
//                    if (needsScopeAutority.charAt(0) == ',') {
//                        needsScopeAutority = needsScopeAutority.substring(2);
//                    }
//                    Toast.makeText(activity, needsScopeAutority + "에 대한 권한이 허용되지 않았습니다. 개인정보 제공에 동의해주세요.", Toast.LENGTH_SHORT).show();
//
//                    //회원탈퇴 수행
//                    //회원탈퇴에 대한 자세한 내용은 MainActivity의 회원탈퇴 버튼 참고
//                    UserManagement.getInstance().requestUnlink(new UnLinkResponseCallback() {
//                        @Override
//                        public void onFailure(ErrorResult errorResult) {
//                            int result = errorResult.getErrorCode();
//
//                            if (result == ApiErrorCode.CLIENT_ERROR_CODE) {
//                                Toast.makeText(activity, "네트워크 연결이 불안정합니다. 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
//                            } else {
//                                Toast.makeText(activity, "오류가 발생했습니다. 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
//                            }
//                        }
//
//                        @Override
//                        public void onSessionClosed(ErrorResult errorResult) {
//                            Toast.makeText(activity, "로그인 세션이 닫혔습니다. 다시 로그인해 주세요.", Toast.LENGTH_SHORT).show();
//                        }
//
//                        @Override
//                        public void onNotSignedUp() {
//                            Toast.makeText(activity, "가입되지 않은 계정입니다. 다시 로그인해 주세요.", Toast.LENGTH_SHORT).show();
//                        }
//
//                        @Override
//                        public void onSuccess(Long result) {
//                        }
//                    });
//
//                } else {
//
//
//                    Intent intent = new Intent(activity, BottomNavi.class);
//                    intent.putExtra("name", result.getNickname()); //유저 이름(String)
//                    intent.putExtra("profile", result.getProfileImagePath()); //유저 프로필 사진 주소(String)
//
//                    if (result.getKakaoAccount().hasEmail() == OptionalBoolean.TRUE)
//                        intent.putExtra("email", result.getKakaoAccount().getEmail()); //이메일이 있다면 -> 이메일 값 넘겨줌(String)
//                    else
//                        intent.putExtra("email", "none"); //이메일이 없다면 -> 이메일 자리에 none 집어넣음.
//                    if (result.getKakaoAccount().hasAgeRange() == OptionalBoolean.TRUE)
//                        intent.putExtra("ageRange", result.getKakaoAccount().getAgeRange().getValue()); //연령대 정보 있다면 -> 연령대 정보를 String으로 변환해서 넘겨줌
//                    else
//                        intent.putExtra("ageRange", "none");
//                    if (result.getKakaoAccount().hasGender() == OptionalBoolean.TRUE)
//                        intent.putExtra("gender", result.getKakaoAccount().getGender().getValue()); //성별 정보가 있다면 -> 성별 정보를 String으로 변환해서 넘겨줌
//                    else
//                        intent.putExtra("gender", "none");
//                    if (result.getKakaoAccount().hasBirthday() == OptionalBoolean.TRUE)
//                        intent.putExtra("birthday", result.getKakaoAccount().getBirthday()); //생일 정보가 있다면 -> 생일 정보를 String으로 변환해서 넘겨줌
//                    else
//                        intent.putExtra("birthday", "none");
//
//                }
            }
        });


        if (LoginPage.loginPage.progressDialog != null)
            LoginPage.loginPage.progressDialog.dismiss();
        // bottomNavi로 intent 시키기
//        redirectLoginActivity();

//        requestMe();

    }


    // 로그인에 실패한 상태
    @Override
    public void onSessionOpenFailed(KakaoException exception) {
        if (exception != null) {
            Log.e("SessionCallback :: ", "onSessionOpenFailed : " + exception.getMessage());
        }
        if (LoginPage.loginPage.progressDialog != null)
            LoginPage.loginPage.progressDialog.dismiss();
        Log.e(TAG, "onSessionOpenFailed: 카카오 로그인 취소" );
    }


    public void redirectLoginActivity() {
        Log.e(TAG, "redirectLoginActivity: ");
        final Intent intent = new Intent(LoginPage.loginPage, BottomNavi.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        LoginPage.loginPage.startActivity(intent);
        LoginPage.loginPage.finish();
    }


    // 사용자 정보 요청
    public void requestMe() {

        // 사용자정보 요청 결과에 대한 Callback
//        List<String> keys = new ArrayList<>();
//        keys.add("properties.nickname");
//        keys.add("properties.profile_image");
//        keys.add("kakao_account.email");


        UserManagement.getInstance().me(new MeV2ResponseCallback() {
            @Override
            public void onSessionClosed(ErrorResult errorResult) {
//                redirectLoginActivity();
                Log.e("SessionCallback :: ", "onSessionClosed : " + errorResult.getErrorMessage());
            }

            @Override
            public void onFailure(ErrorResult errorResult) {
                super.onFailure(errorResult);
                int result = errorResult.getErrorCode();

                if (result == ApiErrorCode.CLIENT_ERROR_CODE) {
                    Toast.makeText(LoginPage.loginPage, "네트워크 연결이 불안정합니다. 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                    LoginPage.loginPage.finish();
                } else {
                    Toast.makeText(LoginPage.loginPage, "로그인 도중 오류가 발생했습니다: " + errorResult.getErrorMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onSuccess(MeV2Response result) {
                Log.e("SessionCallback :: ", "onSuccess");

                if (result.getKakaoAccount().hasEmail() == OptionalBoolean.TRUE) {
                    Log.e(TAG, "onSuccess: 이메일 : " + result.getKakaoAccount().getEmail());
                }


//                Intent intent = new Intent(LoginPage.loginPage, BottomNavi.class);
//                intent.putExtra("name", result.getNickname());
//                intent.putExtra("profile", result.getProfileImagePath());
//                LoginPage.loginPage.startActivity(intent);
//                LoginPage.loginPage.finish();

                String nickname = result.getNickname();
                String email = result.getKakaoAccount().getEmail();
                String phone = result.getKakaoAccount().getPhoneNumber();
                long id = result.getId();


                Log.e(TAG, "onSuccess: 이메일 동의 : " + result.getKakaoAccount().emailNeedsAgreement());
                Log.e("Profile : ", nickname + "");
                Log.e("Profile : ", email + "");
                Log.e("Profile : ", phone + "");
                Log.e("Profile : ", id + "");
                Log.e(TAG, "onSuccess: 프로필  " + result.getKakaoAccount().getDisplayId());
                Log.e(TAG, "onSuccess: 프로필  " + result.getKakaoAccount().getProfile());
                Log.e(TAG, "onSuccess: 프로필  " + result.getKakaoAccount().getLegalName());

                if (LoginPage.loginPage.progressDialog != null)
                    LoginPage.loginPage.progressDialog.dismiss();


            }
        });


    }
}