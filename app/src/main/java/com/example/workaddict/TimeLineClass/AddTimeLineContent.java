package com.example.workaddict.TimeLineClass;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.Image;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.workaddict.BottomFragment.TimeLinePage;
import com.example.workaddict.BottomNavi;
import com.example.workaddict.BuildConfig;
import com.example.workaddict.R;
import com.example.workaddict.Utility.UserInfo;
import com.example.workaddict.databinding.ActivityAddTimelineContentBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Objects;

public class AddTimeLineContent extends AppCompatActivity {

    private final int PERMISSIONS_REQUEST_CODE = 1;
    public static final int PICK_FROM_ALBUM = 200;
    public static final int PICK_FROM_CAMERA = 300;
    private static final int CROP_FROM_CAMERA = 400;
    public static int PICK_NUM = 0;
    private static final String TAG = "AddTimeLineContent";
    public ActivityAddTimelineContentBinding b;
    public int currentPage = 1;
    public final static int CHOICE_PLACE = 1;
    public final static int CHOICE_DATE = 2;
    public final static int SET_TIME = 3;
    public final static int INPUT_SOMETHING = 4;
    public static AddTimeLineContent addTimeLineContent;
    public String strCategoryName;
    public String strPlaceName;
    public String pickedDate;
    public String pickedTime;
    private ProgressDialog dialog;

    private String type = "";
    private File file;
    private Uri uploadUri = null;
    public boolean imageExist = false;
    private File tempFile;
    private ImageView imageView;
    private Bitmap bitmap = null;
    private Bitmap rotateBitmap = null;
    int degree = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addTimeLineContent = this;
        b = DataBindingUtil.setContentView(this, R.layout.activity_add_timeline_content);

        pageChange(CHOICE_PLACE);
        init();
    }


    public void init() {
        Log.e(TAG, "init: 초기화" );
        imageView = findViewById(R.id.addTimelineImage);
        setToolbar();
        requestPermission();
    }


    public void imageFileInit(){
        imageView.setImageBitmap(null);
        tempFile = null;
        rotateBitmap = null;

    }
    public void imageUpload(String key, DatabaseReference myRef, String categoryName,
                            String PlaceName, String date, String someThing,
                            String title, Context context, ProgressDialog dialog, TimeLineAdapter adapter) {

        this.dialog = dialog;

        Log.e(TAG, "imageUpload: uploadUri : " + uploadUri);

        Log.e(TAG, "imageUpload: key : " + key);
        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
        StorageReference riversRef = mStorageRef.child("users/" + UserInfo.getID().replaceAll("\\.", "")
                + "/timeline/"
                + key + ".jpg");
        riversRef.putFile(uploadUri).addOnSuccessListener(taskSnapshot -> {

            String strResult = String.valueOf(taskSnapshot.getUploadSessionUri());
            Log.e(TAG, "imageUpload: strResult : " + strResult);

            Hashtable<String, String> sendText = new Hashtable<String, String>();
            sendText.put("categoryName", categoryName);
            sendText.put("PlaceName", PlaceName);
            sendText.put("date", date);
            sendText.put("someThing", someThing);
            sendText.put("title", title);
            sendText.put("action", "n");
            sendText.put("imageKey", key);

            myRef.setValue(sendText)
                    .addOnSuccessListener(aVoid -> {
                        Log.e(TAG, "addTimeLine: 타임라인 이미지 포함 추가 성공");
                        if (TimeLinePage.singlton != null) {
                            Log.e(TAG, "onDataChange: 셋어댑터");
                            TimeLinePage.singlton.setAdapter(BottomNavi.timeLines);
                        }
                        dialog.dismiss();
//                            TimeLinePage.singlton.setAdapter(BottomNavi.timeLines);
                        finish();
                    }).addOnFailureListener(e -> {
                dialog.dismiss();
                Log.e(TAG, "addTimeLine: 타임라인 추가 실패 ");
            });

        }).addOnFailureListener(exception -> {
            Log.e(TAG, "imageUpload: exception : " + exception);
            Toast.makeText(addTimeLineContent, "네트워크를 확인해주세요", Toast.LENGTH_SHORT).show();
        });

    }

    public void pageChange(int currentPage) {

        this.currentPage = currentPage;

        b.categoryChoice.setVisibility(View.GONE);
        b.dateChoice.setVisibility(View.GONE);
        b.timePickerWrap.setVisibility(View.GONE);
        b.timeLineInputSomeThing.setVisibility(View.GONE);

        Log.e(TAG, "pageChange: " + currentPage);

        switch (currentPage) {

            case 1:
                b.categoryChoice.setVisibility(View.VISIBLE);
                getIntentType();
                break;

            case 2:
                b.dateChoice.setVisibility(View.VISIBLE);
                new AddTimeLineDatePicker(findViewById(R.id.dataPicker));
                break;

            case 3:
                b.timePickerWrap.setVisibility(View.VISIBLE);
                new TimePickerTask(
                        findViewById(R.id.timePicker),
                        findViewById(R.id.btnTimePickConfirm));
                break;

            case 4:
                b.timeLineInputSomeThing.setVisibility(View.VISIBLE);
                new AddTimeLineInputSomeThing(
                        findViewById(R.id.inputTimeLineSomeThing),
                        imageView,
                        findViewById(R.id.addTimelineCamera),
                        findViewById(R.id.addTimelineGallery),
                        findViewById(R.id.addTimelineDelete),
                        findViewById(R.id.btnAddTimeLine),
                        strCategoryName,
                        strPlaceName,
                        pickedDate + " " + pickedTime,
                        addTimeLineContent
                );
                break;
        }

        setToolbar();
    }


    private void getIntentType() {
        Intent intent = getIntent();
        type = intent.getExtras().getString("pageType");
        if (type.equals("2")) {

            strCategoryName = intent.getExtras().getString("categoryName");
            strPlaceName = intent.getExtras().getString("placeName");
            pageChange(CHOICE_DATE);

        } else {

            currentPage = CHOICE_PLACE;
            new ChoiceCategoryList(
                    (findViewById(R.id.categoryRecyclerView)),
                    (findViewById(R.id.placeRecyclerView)),
                    BottomNavi.categoryData,
                    b.categoryChoice,
                    addTimeLineContent);
        }
    }


    private void setToolbar() {

        setSupportActionBar(b.addTimeLineToolbar);
        getSupportActionBar().setTitle("(" + currentPage + "/4) TimeLine 추가");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    @Override
    public void onBackPressed() {

        if (currentPage == 1) {
            super.onBackPressed();
        } else if (currentPage == 2) {
            if (type.equals("2")) {
                finish();
            } else {
                pageChange(CHOICE_PLACE);
            }
        } else if (currentPage == 3) {
            pageChange(CHOICE_DATE);
        } else if (currentPage == 4) {
            pageChange(SET_TIME);
        }

    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Log.e(TAG, "onOptionsItemSelected: home");
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void goToAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }

    public void takePhoto() {
        Log.e(TAG, "takePhoto: 진입");
        Log.e(TAG, "takePhoto: tempFile : " + tempFile);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (tempFile != null) {
            Log.e(TAG, "takePhoto: tempFile not null");
            /**
             *  안드로이드 OS 누가 버전 이후부터는 file:// URI 의 노출을 금지로 FileUriExposedException 발생
             *  Uri 를 FileProvider 도 감싸 주어야 합니다.
             *  참고 자료 http://programmar.tistory.com/4 , http://programmar.tistory.com/5
             */
            uploadUri = FileProvider.getUriForFile(this,
                    "com.example.workaddict.provider", tempFile);
            Log.e(TAG, "takePhoto: uploadUri : " + uploadUri );
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uploadUri);
            startActivityForResult(intent, PICK_FROM_CAMERA);
        } else {
            try {
                tempFile = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            takePhoto();
        }
    }

//    public void goToCamera() {
//
//        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//
//        startActivityForResult(intent, PICK_FROM_CAMERA);
//
//    }


    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) {
            return;
        }


        if (requestCode == PICK_FROM_ALBUM) {
            uploadUri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uploadUri);
                imageView.setImageBitmap(bitmap);
                imageView.setVisibility(View.VISIBLE);
                (findViewById(R.id.addTimelineDelete)).setVisibility(View.VISIBLE);
                imageExist = true;
            } catch (IOException e) {
                e.printStackTrace();
            }


        } else if (requestCode == PICK_FROM_CAMERA) {
            Log.e(TAG, "onActivityResult: PICK_FROM_CAMERA 인텐트 받음 ");
            cropImage();
            MediaScannerConnection.scanFile(this,
                    new String[]{uploadUri.getPath()}, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                        }
                    });

//            Log.e(TAG, "onActivityResult: PICK_FROM_CAMERA 인텐트 받음 ");
//            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
//            Log.e(TAG, "onActivityResult: bitmap : " + bitmap);
//                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
//            imageView.setImageBitmap(bitmap);
//            Log.e(TAG, "onActivityResult: uploadUri : " + uploadUri);
//            imageView.setVisibility(View.VISIBLE);
//            (findViewById(R.id.addTimelineDelete)).setVisibility(View.VISIBLE);
            imageExist = true;
        } else if (requestCode == CROP_FROM_CAMERA) {
            imageView.setImageURI(null);
            setImage(tempFile);
        }


    }


    //Android N crop image
    public void cropImage() {

        Log.e(TAG, "cropImage: 진입");
        Log.e(TAG, "cropImage: 버전확인 : " + Build.VERSION.SDK_INT );

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Log.e(TAG, "cropImage: 첫번째 퍼미션 " );
            this.grantUriPermission("com.android.camera", uploadUri,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uploadUri, "image/*");

        List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, 0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            grantUriPermission(list.get(0).activityInfo.packageName, uploadUri,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        int size = list.size();
        if (size == 0) {
            return;
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            }
            intent.putExtra("crop", "true");
//            intent.putExtra("aspectX", 5);
//            intent.putExtra("aspectY", 7);
            intent.putExtra("scale", true);
            File croppedFileName = null;
            try {
                croppedFileName = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
                       


            File folder = new File(Objects.requireNonNull(getExternalFilesDir(null)).toString() + "/workaholic/");
            Log.e(TAG, "cropImage: folder toString : " + folder.toString() );
            tempFile = new File(folder.toString(), croppedFileName.getName());

            uploadUri = FileProvider.getUriForFile(this,
                    "com.example.workaddict.provider", tempFile);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            }

            intent.putExtra("return-data", false);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uploadUri);
            intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());


            Intent i = new Intent(intent);
            ResolveInfo res = list.get(0);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                i.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                grantUriPermission(res.activityInfo.packageName, uploadUri,
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }
            i.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            startActivityForResult(i, CROP_FROM_CAMERA);
        }
    }


    /**
     * tempFile 을 bitmap 으로 변환
     * 이미지 안에 설정된 회전 값을 구해서 회전 적용한 후 ImageView 에 설정한다.
     */
    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void setImage(File file) {
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.fromFile(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.e(TAG, "setImage: " + bitmap);
        imageView.setVisibility(View.VISIBLE);
        imageView.setImageBitmap(bitmap);
        rotateBitmap = bitmap;
        degree = 0;

        (findViewById(R.id.addTimelineDelete)).setVisibility(View.VISIBLE);
//        ll_camera_gallery_wrap.setVisibility(View.GONE);
//        fl_rotate_wrap.setVisibility(View.VISIBLE);

        /**
         *  tempFile 사용 후 null 처리를 해줘야 합니다.
         *  (resultCode != RESULT_OK) 일 때 tempFile 을 삭제하기 때문에
         *  기존에 데이터가 남아 있게 되면 원치 않은 삭제가 이뤄집니다.
         */
    }


    /**
     * 이미지파일 이름, 파일 경로, 빈파일 생성
     */
    private File createImageFile() throws IOException {

        // 이미지 파일 이름
        String timeStamp = new SimpleDateFormat("HHmmss").format(new Date());
        String imageFileName = "/workaholic_" + timeStamp;

        // 이미지가 저장될 파일 경로
//        File storageDir = new File(Environment.getExternalStorageDirectory() + "/workaholic /");
        File storageDir = new File(Objects.requireNonNull(getExternalFilesDir(null)).toString() + "/workaholic/");
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }

        // 빈 파일 생성
        File image = new File(storageDir + imageFileName + ".jpg");
        Log.e(TAG, "createImageFile : " + image);

        return image;
    }


    public boolean getUploadImageUri() {
//        Uri uri;

//        if (uploadUri != null) uri = uploadUri;
//        else uri = null;

        return imageExist;

    }


    private void requestPermission() {
        boolean shouldProviceRationale =
                (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA));//사용자가 이전에 거절한적이 있어도 true 반환

        Log.e(TAG, "requestPermission: 퍼미션" );
        if (shouldProviceRationale) {
            Log.e(TAG, "requestPermission: 퍼미션 true" );
            //앱에 필요한 권한이 없어서 권한 요청
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE
                    , Manifest.permission.WRITE_EXTERNAL_STORAGE
                    , Manifest.permission.CAMERA}, PERMISSIONS_REQUEST_CODE);
        } else {
            Log.e(TAG, "requestPermission: 퍼미션 false" );
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE
                    , Manifest.permission.WRITE_EXTERNAL_STORAGE
                    , Manifest.permission.CAMERA}, PERMISSIONS_REQUEST_CODE);

        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    try {
                        tempFile = createImageFile();
                        Log.e(TAG, "onRequestPermissionsResult: tempFile 생성" + tempFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.e("파일 생성 실패", "failed : " + e );
                        Toast.makeText(this, "이미지 처리 오류! 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    //사용자가 권한 거절시
                    denialDialog();
                }
                return;
            }
        }
    }


    public void denialDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("알림")
                .setMessage("저장소 권한이 필요합니다. 환경 설정에서 저장소 권한을 허가해주세요.")
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package",
                                BuildConfig.APPLICATION_ID, null);
                        intent.setData(uri);
                        intent.setFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent); //확인버튼누르면 바로 어플리케이션 권한 설정 창으로 이동하도록
                    }
                })
                .create()
                .show();
    }


    /**
     * 비트맵 회전하는 메소드, 인자값으로 비트맵과,  회전 각도 보냄
     *
     * @param bitmap
     * @param rotate
     * @return
     */
    public Bitmap rotateBitmap(Bitmap bitmap, int rotate) {

        Matrix rotateMatrix = new Matrix();
        switch (rotate) {
            case 0:
                rotateMatrix.postRotate(0);
                break;
            case 90:
                rotateMatrix.postRotate(90);
                break;
            case 180:
                rotateMatrix.postRotate(180);
                break;
            case 270:
                rotateMatrix.postRotate(270);
                break;
            case 360:
                rotateMatrix.postRotate(360);
                degree = 0;
                break;
            case -90:
                rotateMatrix.postRotate(-90);
                break;
            case -180:
                rotateMatrix.postRotate(-180);
                break;
            case -270:
                rotateMatrix.postRotate(-270);
                break;
            case -360:
                rotateMatrix.postRotate(-360);
                degree = 0;
                break;
            default:
                degree = 0;
                break;
        }
        return rotateImage(bitmap, rotateMatrix);
    }

    public Bitmap rotateImage(Bitmap bitmap, Matrix matrix) {
        Bitmap sideInversionImg = Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, false);
        rotateBitmap = sideInversionImg;
        return sideInversionImg;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
