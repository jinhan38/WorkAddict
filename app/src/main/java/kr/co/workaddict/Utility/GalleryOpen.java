package kr.co.workaddict.Utility;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.IOException;

public class GalleryOpen extends AppCompatActivity {
    public final static int GALLERY_OPEN = 100;
    public final static int EXTERNAL_READ_PERMISSION = 101;
    private static final String TAG = "GalleryOpen";

    private ImageView imageView;
    private Activity activity;
    private boolean permissionResult = false;


    public GalleryOpen(ImageView imageView, Activity activity) {
        this.imageView = imageView;
        this.activity = activity;
        Log.e(TAG, "GalleryOpen: imageView: " + imageView );

        requestPermission();

    }

    private void requestPermission() {



        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.READ_EXTERNAL_STORAGE)) {

            } else {

                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        EXTERNAL_READ_PERMISSION);
            }

        } else {

//            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE)
//                    == PackageManager.PERMISSION_DENIED) {
//                Log.e(TAG, "requestPermission: 거부");
//            }
         permissionResult = true;
        }

        Log.e(TAG, "requestPermission: permissionResult : " + permissionResult );
        if (permissionResult) initView();

    }

    private void initView() {

        Log.e(TAG, "initView: " );
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        Log.e(TAG, "initView: activity : " + activity );
        activity.startActivityForResult(intent, GALLERY_OPEN);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, "onActivityResult: requestCode : " + requestCode );
        if (requestCode == GALLERY_OPEN) {
            Uri image = data.getData();
            Log.e(TAG, "onActivityResult: " );
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), image);
                Log.e(TAG, "onActivityResult: bitmap : " + bitmap );
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.e(TAG, "onRequestPermissionsResult: " );
        if (requestCode == EXTERNAL_READ_PERMISSION) {
            initView();
            permissionResult = true;
            Log.e(TAG, "onRequestPermissionsResult: 안쪽" );
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
