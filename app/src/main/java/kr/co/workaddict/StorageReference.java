package kr.co.workaddict;

import android.util.Log;

import kr.co.workaddict.Utility.UserInfo;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

public class StorageReference {

    private static final String TAG = "StorageReference";

    ArrayList<String> stringUri;
    ArrayList<String> keys;

    public StorageReference() {
        getImageData();
    }

    private void getImageData() {

        com.google.firebase.storage.StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
        com.google.firebase.storage.StorageReference riversRef = mStorageRef
                .child("users/" + UserInfo.getID().replaceAll("\\.", "") + "/timeline/");

        
        riversRef.getDownloadUrl().addOnSuccessListener(uri -> {
            if (uri != null) {
                stringUri.add(String.valueOf(uri));
            }
        }).addOnFailureListener(e -> Log.e(TAG, "onFailure: 이미지데이터 받기 실패 : " + e));
    }

    private  ArrayList<String> getUri(){
        return stringUri;
    }
}
