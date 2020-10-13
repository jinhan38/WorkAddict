package com.example.workaddict.MyPageFragment;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
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

import com.example.workaddict.BottomFragment.MyPageFragment;
import com.example.workaddict.R;
import com.example.workaddict.Utility.UserInfo;
import com.example.workaddict.Utility.Util;
import com.example.workaddict.databinding.ActivityInviteFragmentBinding;
import com.kakao.kakaolink.v2.KakaoLinkResponse;
import com.kakao.kakaolink.v2.KakaoLinkService;
import com.kakao.network.ErrorResult;
import com.kakao.network.callback.ResponseCallback;

import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

public class InviteFragment extends Fragment implements View.OnClickListener {
    private ActivityInviteFragmentBinding b;

    private static final String TAG = "InviteFragment";
    private static final int SMS_PERMISSION = 1000;
    private static final int ADDRESS_BOOK_PERMISSION = 1001;
    private String addressBookPhoneNum = "";
    private String addressBookName = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        b = DataBindingUtil.inflate(inflater, R.layout.activity_invite_fragment, container, false);

        setupListener();
        return b.getRoot();
    }

    private void setupListener() {
        b.kakaoInvite.setOnClickListener(this);
        b.sendSMS.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibMapPageBack:
                MyPageFragment.singleton.onBackPressed();
                break;
            case R.id.kakaoInvite:
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("카카오톡으로 초대하시겠습니까?");
                builder.setPositiveButton("초대", (dialog, which) -> {
                    dialog.dismiss();
                    kakao();
                }).setNegativeButton("취소", (dialog, which) -> dialog.dismiss());

                AlertDialog dialog = builder.create();
                dialog.show();

                break;
            case R.id.sendSMS:

                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                    Log.e(TAG, "문자 발송 권한 요청");
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.SEND_SMS}, SMS_PERMISSION);

                } else {
                    //권한 허용했으면 주소록 오픈
                    openAddressBook();

                }
                break;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        Log.e(TAG, "onRequestPermissionsResult: 문자 궎한 허용 요청" + requestCode);
        switch (requestCode) {
            case SMS_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openAddressBook();
                } else {
                    Toast.makeText(getActivity(), "권한을 거부하였습니다", Toast.LENGTH_SHORT).show();
                }
        }
    }


    private void openAddressBook() {

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setData(ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        startActivityForResult(intent, ADDRESS_BOOK_PERMISSION);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (resultCode == RESULT_OK) {

            //주소록에서 주소 선택하고 돌아옴
            if (requestCode == ADDRESS_BOOK_PERMISSION) {

                Cursor cursor = getActivity().getContentResolver().query(data.getData(),
                        new String[]{ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                                ContactsContract.CommonDataKinds.Phone.NUMBER},
                        null, null, null);

                cursor.moveToFirst();

                addressBookName = cursor.getString(0);     //0은 이름
                addressBookPhoneNum = cursor.getString(1);   //1은 번호
                Log.e(TAG, "onActivityResult: addressBookPhoneNum : " + addressBookPhoneNum );

                addressBookPhoneNum = addressBookPhoneNum.replaceAll("-", "");

                if (addressBookPhoneNum.length() == 11) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage(addressBookName + "님에게 초대문자를 보내시겠습니까?");

                    builder.setPositiveButton("초대", (dialog, which) -> {

                        String content = UserInfo.getName() + "이 일중독 APP에 초대하셨습니다.\n" + Util.DYNAMIC_LINK_ADDRESS;

                        try {
                            byte[] contentBytes = content.getBytes("KSC5601");


                            SmsManager smsManager = SmsManager.getDefault();
                            smsManager.sendTextMessage(addressBookPhoneNum, null,
                                    new String(contentBytes, "KSC5601"), null, null);

                            Toast.makeText(getActivity(), addressBookName + "님을 초대했습니다.", Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            Toast.makeText(getActivity(), "전송 오류가 발생했습니다. 다시 시도해주시기 바랍니다.", Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }

                    }).setNegativeButton("취소", (dialog, which) -> dialog.dismiss());

                    AlertDialog dialog = builder.create();
                    dialog.show();

                } else {
                    Toast.makeText(getActivity(), "문자를 보낼 수 없는 연락처입니다", Toast.LENGTH_SHORT).show();
                }

            }


        }

        super.onActivityResult(requestCode, resultCode, data);
    }




    public void kakao() {
        String templateId = "24204";

        Map<String, String> templateArgs = new HashMap<>();
        templateArgs.put("template_arg1", "invite");

        // 커스텀 템플릿으로 카카오링크 보내기
        KakaoLinkService.getInstance()
                .sendCustom(getActivity(), templateId, templateArgs
                        , null, new ResponseCallback<KakaoLinkResponse>() {
                            @Override
                            public void onFailure(ErrorResult errorResult) {
                                Log.e("KAKAO_API", "카카오링크 보내기 실패: " + errorResult);
                            }

                            @Override
                            public void onSuccess(KakaoLinkResponse result) {
                                Log.i("KAKAO_API", "카카오링크 보내기 성공");

                            }
                        });
    }
}