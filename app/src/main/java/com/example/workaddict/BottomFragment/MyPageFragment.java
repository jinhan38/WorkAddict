package com.example.workaddict.BottomFragment;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.example.workaddict.BottomNavi;
import com.example.workaddict.CustomDialog;
import com.example.workaddict.FollowInfo.Follows;
import com.example.workaddict.Interface.BackButton;
import com.example.workaddict.LoginPage;
import com.example.workaddict.MyPageFragment.AppInfoFragment;
import com.example.workaddict.MyPageFragment.InviteFragment;
import com.example.workaddict.MyPageFragment.NoticeFragment;
import com.example.workaddict.MyPageFragment.QuestionActivity;
import com.example.workaddict.MyPageFragment.SettingFragment;
import com.example.workaddict.MyPageFragment.TermsFragment;
import com.example.workaddict.R;
import com.example.workaddict.SaveSharedPreferences;
import com.example.workaddict.Utility.UserInfo;
import com.example.workaddict.Utility.Util;
import com.example.workaddict.databinding.ActivityMypageFragmentBinding;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;

import java.io.IOException;
import java.util.Objects;

public class MyPageFragment extends Fragment implements View.OnClickListener, BackButton {

    private static final String TAG = "MyPageFragment";
    public final static int GALLERY_OPEN = 100;
    public final static int EXTERNAL_READ_PERMISSION = 101;
    public static MyPageFragment singleton;
    public ActivityMypageFragmentBinding b;
    private long backBtnTime = 0;
    public int myPageFragmentNum = 0;
    public final static int MY_PAGE_FRAGMENT = 0;
    public final static int FOLLOWS_lIST = 1;
    public final static int FOLLOWS_INVITE = 2;
    public final static int SETTING_NUM = 3;
    public final static int ALERT_NUM = 4;
    public final static int NOTICE_NUM = 5;
    public final static int APP_INFO_NUM = 6;
    public final static int TERMS_NUM = 8;
    public final static int INVITE = 9;
    private CustomDialog customDialog;
    private boolean permissionResult;
    private StorageReference mStorageRef;
    private FragmentTransaction fragmentTransaction;

    private Fragment fragment = null;

    public static MyPageFragment newInstance() {
        if (singleton == null) {
            singleton = new MyPageFragment();
        }
        return singleton;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        b = DataBindingUtil.inflate(inflater, R.layout.activity_mypage_fragment, container, false);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        setupListener();
        initView();

        return b.getRoot();
    }


    private void initView() {
        b.textVersion.setText("현재 버전 " + getString(R.string.version));
        Util.getVersion();

        b.myPageID.setText(UserInfo.getID());
        b.myPageName.setText(UserInfo.getName());

        if (SaveSharedPreferences.getDeleteLoginPhotoUrl(getActivity()).equals("n")) {

            if (SaveSharedPreferences.getLoginPhotoUrl(getActivity()).length() > 0) {
                Glide.with(getActivity())
                        .load(SaveSharedPreferences.getLoginPhotoUrl(getActivity()))
                        .override(200, 200)
                        .fitCenter()
                        .circleCrop()
                        .into(b.profile);
            } else {
                downloadProfile();
            }

        }


    }

    private void setupListener() {
        b.followsWrap.setOnClickListener(this);
        b.setting.setOnClickListener(this);
        b.notice.setOnClickListener(this);
        b.appInfo.setOnClickListener(this);
        b.question.setOnClickListener(this);
        b.serviceTerms.setOnClickListener(this);
        BottomNavi.bottomNavi.b.toolbarLogout.setOnClickListener(this);
        b.profile.setOnClickListener(this);
        b.invite.setOnClickListener(this);

        profileDelete();

    }


    public void versionCheck() {
        if (getString(R.string.version).equals(UserInfo.getVersion())) {
            b.versionUpdate.setEnabled(false);
            b.versionUpdate.setText("최신버전 사용중");
            b.versionUpdate.setTextColor(getResources().getColor(R.color.middle_gray));
        } else {
            b.versionUpdate.setEnabled(true);
            b.versionUpdate.setText("업데이트");
            b.versionUpdate.setTextColor(getResources().getColor(R.color.deepPurple));
        }
    }

    public void noticeDateCheck() {
        if (!SaveSharedPreferences.getNoticeDate(getActivity()).equals(UserInfo.getNoticeDate())) {
            b.noticeNewAlert.setVisibility(View.VISIBLE);
        } else {
            b.noticeNewAlert.setVisibility(View.GONE);
        }
    }

    /**
     * 프로필 롱클릭 했을 때 지우는 메소드
     */
    private void profileDelete() {

        if (b.profile != null) {
            b.profile.setOnLongClickListener(v -> {

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("프로필 이미지를 삭제하시겠습니까?");

                builder.setPositiveButton("삭제", (dialog, which) -> {

                    SaveSharedPreferences.setDeleteLoginPhotoUrl(getActivity(), "y");

                    if (SaveSharedPreferences.getLoginPhotoUrl(getActivity()).length() > 0) {
                        SaveSharedPreferences.setLoginPhotoUrl(getActivity(), "");
                        setBasicProfile();

                    } else {

                        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
                        StorageReference riversRef = mStorageRef.child("users/" +
                                UserInfo.getID().replaceAll("\\.", "") + "/profile/profile.jpg");

                        riversRef.delete().addOnSuccessListener(aVoid -> {
                            Toast.makeText(getActivity(), "프로필이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                            setBasicProfile();

                        }).addOnFailureListener(e -> {
                            Log.e(TAG, "profileDelete: e : " + e);
                            Toast.makeText(getActivity(), "일시적인 에러가 발생했습니다. 다시 시도해주시기 바랍니다.", Toast.LENGTH_SHORT).show();
                        });
                    }


                }).setNegativeButton("취소", (dialog, which) -> dialog.dismiss());

                AlertDialog dialog = builder.create();
                dialog.show();

                return false;
            });
        }
    }

    private void setBasicProfile() {
        Glide.with(getActivity())
                .load(R.drawable.profile_icon)
                .override(200, 200)
                .fitCenter()
                .circleCrop()
                .into(b.profile);
    }


    private void downloadProfile() {

        StorageReference riversRef = mStorageRef.child("users/" + UserInfo.getID().replaceAll("\\.", "") + "/profile/profile.jpg");

        riversRef.getDownloadUrl().addOnSuccessListener(uri -> {
            if (uri != null) {

                Glide.with(getActivity())
                        .load(uri)
                        .override(200, 200)
                        .placeholder(R.drawable.image_download_loading)
                        .fitCenter()
                        .circleCrop()
                        .into(b.profile);

            }
        });

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.followsWrap:
                getActivity().startActivity(new Intent(getActivity(), Follows.class));
                break;
            case R.id.notice:
                SaveSharedPreferences.setNoticeDate(getActivity(), UserInfo.getNoticeDate());
                b.noticeNewAlert.setVisibility(View.GONE);
                myPageFragmentNum = NOTICE_NUM;
                myPageChangeFragment();
                break;
            case R.id.invite:
                myPageFragmentNum = INVITE;
                myPageChangeFragment();
                break;
            case R.id.appInfo:
                myPageFragmentNum = APP_INFO_NUM;
                myPageChangeFragment();
                break;
            case R.id.question:
                startActivity(new Intent(getActivity(), QuestionActivity.class));
                break;
            case R.id.setting:
                myPageFragmentNum = SETTING_NUM;
                myPageChangeFragment();
                break;
            case R.id.serviceTerms:
                myPageFragmentNum = TERMS_NUM;
                myPageChangeFragment();
                break;
            case R.id.ibMapPageBack:
                onBackPressed();
                break;
            case R.id.toolbarLogout:
                logout();
                break;
            case R.id.profile:
                requestPermission();
                break;
            default:
                break;

        }
    }


    public void myPageChangeFragment() {
        b.myPageMenuTextWrap.setVisibility(View.GONE);
        b.myPageFragmentContainer.setVisibility(View.VISIBLE);
        String menuName = "";
        fragment = null;
        switch (myPageFragmentNum) {
//            case FOLLOWS_lIST:
//                fragment = new Follows();
//                menuName = "친구정보";
//                break;
//            case FOLLOWS_INVITE:
//                fragment = new FollowInvite();
//                menuName = "친구추가";
//                break;
            case SETTING_NUM:
                fragment = new SettingFragment();
                menuName = "설정";
                break;
            case NOTICE_NUM:
                fragment = new NoticeFragment();
                menuName = "공지사항";
                break;
            case APP_INFO_NUM:
                fragment = new AppInfoFragment();
                menuName = "앱정보";
                break;
            case TERMS_NUM:
                fragment = new TermsFragment();
                menuName = "이용약관";
                break;
            case INVITE:
                fragment = new InviteFragment();
                menuName = "초대";
                break;
            default:
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.myPageFragmentContainer, fragment);
            fragmentTransaction.commit();
        }

        b.myPageFragmentContainer.setVisibility(View.VISIBLE);

        BottomNavi.bottomNavi.b.toolbarLogout.setVisibility(View.GONE);
        BottomNavi.bottomNavi.b.ibMapPageBack.setVisibility(View.VISIBLE);
        BottomNavi.bottomNavi.b.tvMapPageAppName.setText(menuName);
    }


    private void requestPermission() {


        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {

            } else {

                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        EXTERNAL_READ_PERMISSION);
            }

        } else {

//            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE)
//                    == PackageManager.PERMISSION_DENIED) {
//                Log.e(TAG, "requestPermission: 거부");
//            }
            openGallery();
            permissionResult = true;
        }

        Log.e(TAG, "requestPermission: permissionResult : " + permissionResult);
//        if (permissionResult) openGallery();

    }

    private void openGallery() {
        Log.e(TAG, "initView: ");
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, GALLERY_OPEN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null) {

            if (requestCode == GALLERY_OPEN) {

                Uri image = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), image);

//                    b.profile.setImageBitmap(bitmap);
                    SaveSharedPreferences.setDeleteLoginPhotoUrl(getActivity(), "n");
                    Glide.with(Objects.requireNonNull(getActivity()))
                            .load(bitmap)
                            .override(200, 200)
                            .placeholder(R.drawable.image_download_loading)
                            .fitCenter()
                            .circleCrop()
                            .into(b.profile);

                    StorageReference riversRef = mStorageRef.child("users/" + UserInfo.getID().replaceAll("\\.", "") + "/profile/profile.jpg");

                    riversRef.putFile(image).addOnSuccessListener(taskSnapshot -> {

                        Log.e(TAG, "onSuccess: 업로드 성공 : " + taskSnapshot.toString());

                    }).addOnFailureListener(exception -> {
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        }
    }

    @Override
    public void onResume() {
//        if (BottomNavi.bottomNavi.followerData != null) {
//            b.followerCount.setText(BottomNavi.bottomNavi.followerData.size() + "\n팔로워");
//        }
//
//        if (BottomNavi.bottomNavi.followingData != null) {
//            b.followingCount.setText(BottomNavi.bottomNavi.followingData.size() + "\n팔로잉");
//        }

        super.onResume();
    }


    @Override
    public void onBackPressed() {

        if (BottomNavi.bottomNavi.b.viewPager.getCurrentItem() == 3) {

            if (b.myPageFragmentContainer.getVisibility() == View.VISIBLE) {

                b.myPageMenuTextWrap.setVisibility(View.VISIBLE);
                getActivity().getSupportFragmentManager().beginTransaction().remove(fragment).commit();


                b.myPageFragmentContainer.setVisibility(View.GONE);
                BottomNavi.bottomNavi.b.toolbarLogout.setVisibility(View.VISIBLE);
                BottomNavi.bottomNavi.b.ibMapPageBack.setVisibility(View.GONE);
                BottomNavi.bottomNavi.b.tvMapPageAppName.setText(getString(R.string.mypage));

            } else {
                long curTime = System.currentTimeMillis();
                long gapTime = curTime - backBtnTime;
                if (0 <= gapTime && 2000 >= gapTime) {
                    getActivity().finish();
                } else {
                    backBtnTime = curTime;
                    Toast.makeText(getActivity(), "한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void logout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("로그아웃 하시겠습니까?");

        builder.setPositiveButton("네", (dialog, which) -> {

            BottomNavi.placeData.clear();
            BottomNavi.placeDataKeyList.clear();
            BottomNavi.timeLines.clear();
            BottomNavi.timeLineDataKeyList.clear();
            BottomNavi.categoryData.clear();
            BottomNavi.categoryDataKeyList.clear();
            BottomNavi.checkedPositions.clear();
            BottomNavi.addressDocuments.clear();
            BottomNavi.followerData.clear();
            BottomNavi.followerKeyList.clear();
            BottomNavi.followingData.clear();
            BottomNavi.followingKeyList.clear();
            UserInfo.logoutReset();
            SaveSharedPreferences.setPrefAutoLogin(getActivity(), "n");
            SaveSharedPreferences.setPrefId(getActivity(), "");
            SaveSharedPreferences.setPrefPw(getActivity(), "");
            SaveSharedPreferences.setPrefIsLogin(getActivity(), "n");
            SaveSharedPreferences.setLoginPhotoUrl(getActivity(), "");


            dialog.dismiss();

            //파이어베이스에서 구글이나 페이스북 등으로 연동해서 한경우 아래 한줄이면 모두 로그아웃 가능
            FirebaseAuth.getInstance().signOut();

            //페이스북 로그아웃
            LoginManager.getInstance().logOut();

            //카카오 로그아웃
            UserManagement.getInstance().requestLogout(new LogoutResponseCallback() {
                @Override
                public void onCompleteLogout() {
                    Log.e(TAG, "onCompleteLogout: 카카오 로그아웃 성공" );
                    //로그아웃에 성공하면: LoginPage로 이동
//                    Intent intent = new Intent(getActivity(), LoginPage.class);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    startActivity(intent);
//                    BottomNavi.bottomNavi.finish();

                }
            });

            getActivity().startActivity(new Intent(getActivity(), LoginPage.class));
            getActivity().finish();
            BottomNavi.bottomNavi.finish();

        }).setNegativeButton("아니오", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }


}



