package kr.co.workaddict.MyPageFragment;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
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
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import kr.co.workaddict.BottomFragment.MyPageFragment;
import kr.co.workaddict.R;
import kr.co.workaddict.Utility.UserInfo;
import kr.co.workaddict.Utility.Util;
import kr.co.workaddict.databinding.ActivityInviteFragmentBinding;

import com.kakao.kakaolink.v2.KakaoLinkResponse;
import com.kakao.kakaolink.v2.KakaoLinkService;
import com.kakao.network.ErrorResult;
import com.kakao.network.callback.ResponseCallback;

import org.apache.http.protocol.HTTP;

import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

public class InviteFragment extends Fragment implements View.OnClickListener {
    private ActivityInviteFragmentBinding b;

    private static final String TAG = "InviteFragment";
//    private static final int SMS_PERMISSION = 1000;
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

                openAddressBook();

                break;
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
                Log.e(TAG, "onActivityResult: addressBookPhoneNum : " + addressBookPhoneNum);

                addressBookPhoneNum = addressBookPhoneNum.replaceAll("-", "");

                if (addressBookPhoneNum.length() == 11) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage(addressBookName + "님에게 초대문자를 보내시겠습니까?");

                    builder.setPositiveButton("초대", (dialog, which) -> {

                        String content = UserInfo.getName() + "이 일중독 APP에 초대하셨습니다.\n" + Util.DYNAMIC_LINK_ADDRESS;

                        try {
                            byte[] contentBytes = content.getBytes("KSC5601");

                            Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + addressBookPhoneNum ));
                            intent.putExtra("sms_body", new String(contentBytes, "KSC5601"));
                            startActivity(intent);

                            if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                                startActivity(intent);
                            }

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