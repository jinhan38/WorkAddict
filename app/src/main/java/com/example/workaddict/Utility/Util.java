package com.example.workaddict.Utility;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.example.workaddict.BottomFragment.ListFragment;
import com.example.workaddict.BottomFragment.MapFragment;
import com.example.workaddict.BottomFragment.MyPageFragment;
import com.example.workaddict.BottomFragment.TimeLinePage;
import com.example.workaddict.BottomNavi;
import com.example.workaddict.CategoryList;
import com.example.workaddict.DataClass.CategoryData;
import com.example.workaddict.DataClass.FollowerData;
import com.example.workaddict.DataClass.FollowingData;
import com.example.workaddict.DataClass.PlaceData;
import com.example.workaddict.DataClass.TimeLine;
import com.example.workaddict.DataClass.Version;
import com.example.workaddict.DataSort.SortByDateDateItem;
import com.example.workaddict.FollowInfo.Follower;
import com.example.workaddict.R;
import com.example.workaddict.TimeLineClass.AddTimeLineContent;
import com.example.workaddict.TimeLineClass.TimeLineAdapter;
import com.example.workaddict.TimeLineDateClass.DateItem;
import com.example.workaddict.TimeLineDateClass.MyDateTime;
import com.example.workaddict.TimeLineDateClass.TimeClassification;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kakao.kakaonavi.Destination;
import com.kakao.kakaonavi.KakaoNaviParams;
import com.kakao.kakaonavi.KakaoNaviService;
import com.kakao.kakaonavi.NaviOptions;
import com.kakao.kakaonavi.options.CoordType;
import com.kakao.kakaonavi.options.RpOption;
import com.kakao.kakaonavi.options.VehicleType;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util {
    private static final String TAG = "Util";
    public static Context CURRENT_CONTEXT = null;
    public static String TOKEN = "";
    public static String addressURL = "https://dapi.kakao.com";
    public static String APIKEY = "KakaoAK b1a6f71144cc1015bd7a447bbd8468db";
    public final static String TMAP_APP_KEY = "l7xxd14081d291cb443bbf5de1636f3e9809";
    public final static String DYNAMIC_LINK_ADDRESS = "https://workaddict.page.link/eNh4";
    public static FirebaseDatabase database;
    public static boolean isFollowTimeline = false;
    public static final String CATEGORY_COLOR_RED = "RED";
    public static final String CATEGORY_COLOR_ORANGE = "ORANGE";
    public static final String CATEGORY_COLOR_YELLOW = "YELLOW";
    public static final String CATEGORY_COLOR_GREEN = "GREEN";
    public static final String CATEGORY_COLOR_BLUE = "BLUE";
    public static final String CATEGORY_COLOR_INDIGO = "INDIGO";
    public static final String CATEGORY_COLOR_PURPLE = "PURPLE";
    public static final String CATEGORY_COLOR_BLACK = "BLACK";
    public static final String CATEGORY_COLOR_GRAY = "GRAY";

    public static FirebaseDatabase getFirebaseDatabase() {
        return Util.database;
    }

    public static void setFirebaseDatabase(FirebaseDatabase database) {
        Util.database = database;
    }

    public static final String[] REQUIRED_PERMISSIONS = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    public static int PERMISSIONS_REQUEST_CODE = 1004;


    public static void setCategorySpinner(Context context,
                                          Spinner spinner,
                                          ArrayList<CategoryData> categoryData,
                                          ArrayList<PlaceData> placeData) {

        ArrayList<String> stringCategory = new ArrayList<String>();
        stringCategory.add("전체");


        int arraySize = categoryData.size();
        int allCount = 0;

        for (int i = 0; i < arraySize; i++) {
            int count = 0;
            String categoryName = categoryData.get(i).getCD();

            if (placeData.size() > 0) {
                for (int c = 0; c < placeData.size(); c++) {
                    if (placeData.get(c).getCategoryName().equals(categoryName)) {
                        count++;
                    }
                }
                allCount += count;
            }
            stringCategory.add("(" + count + ")" + categoryData.get(i).getCD());
        }
//        stringCategory.add(0, "(" + allCount + ")전체");
        stringCategory.set(0, "(" + allCount + ")전체");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
                android.R.layout.simple_spinner_dropdown_item, stringCategory);

        spinner.setAdapter(adapter);

    }

    public static void setCategoryTimeLineSpinner(Context context,
                                                  Spinner spinner,
                                                  ArrayList<CategoryData> categoryData) {
        ArrayList<String> stringCategory = new ArrayList<String>();
        stringCategory.add("전체");
        int arraySize = categoryData.size();
        for (int i = 0; i < arraySize; i++) {
            stringCategory.add(categoryData.get(i).getCD());
        }
        stringCategory.add("기타");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
                R.layout.spinner_text, stringCategory);
        spinner.setAdapter(adapter);
    }

    public static void setPlaceTimeLineSpinner(Context context,
                                               Spinner spinner,
                                               ArrayList<PlaceData> placeData) {
        ArrayList<String> stringPlace = new ArrayList<String>();
        stringPlace.add("전체");
        int arraySize = placeData.size();
        for (int i = 0; i < arraySize; i++) {
            stringPlace.add(placeData.get(i).getPlaceName());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
                R.layout.spinner_text, stringPlace);
        spinner.setAdapter(adapter);

    }


    public static void saveContext(Context context) {
        CURRENT_CONTEXT = context;
    }


    public static void showToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

    public static void showToast(String msg) {
        Toast.makeText(CURRENT_CONTEXT, msg, Toast.LENGTH_LONG).show();
    }


    public static String getCurrentTime() {
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a");
        String currentTime = simpleDateFormat.format(date);
        return currentTime;
    }


    public static boolean isValidEmail(String email) {

        boolean rValue = false;

        String regex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(email);
        if (m.matches()) {
            rValue = true;
        }
        return rValue;
    }


    public static void hideKeyboard(Activity activity) {
        Log.e(TAG, "hideKeyboard: 진입");
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void showKeyboard(Activity activity) {
        Log.e(TAG, "showKeyboard: ");
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view != null) {
            imm.toggleSoftInput(0, InputMethodManager.HIDE_IMPLICIT_ONLY);
        }
    }


//    public static void addPrivacy() {
//
//        FirebaseAuth mAuth = FirebaseAuth.getInstance();
//        FirebaseUser firebaseUser = mAuth.getCurrentUser();
//        DatabaseReference myRef = database.getReference("users")
//                .child(firebaseUser.getEmail().replaceAll("\\.", ""))
//                .child("Privacy").push();
//
//        Calendar cl = Calendar.getInstance();
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd:hh:mm:ss a");
//        String dateTime = dateFormat.format(cl.getTime());
//
//        Hashtable<String, String> sendText = new Hashtable<String, String>();
//
//        if (firebaseUser != null) {
//
//            sendText.put("id", firebaseUser.getEmail());
//
//            if (firebaseUser.getDisplayName() == null) sendText.put("name", "");
//            else sendText.put("name", firebaseUser.getDisplayName());
//
//            if (firebaseUser.getPhoneNumber() == null) sendText.put("phone", "");
//            else sendText.put("phone", firebaseUser.getPhoneNumber());
//
//        }
//        //구글 아이디에 phone이 저장된게 없는지 phone 부분에서 에러난다...
//
//        sendText.put("dateOfBirth", "");
//        sendText.put("date", dateTime);
//        myRef.setValue(sendText)
//                .addOnSuccessListener(aVoid -> {
//
//                    Log.e(TAG, "addPrivacy: 입력 성공");
//                    Log.e(TAG, "회원가입 성공 ");
//
//                }).addOnFailureListener(e -> {
//
//            Log.e(TAG, "addPrivacy: 입력 실패 ");
//
//        });
//
//    }


    public static void addPrivacy() {

        DatabaseReference myRef = database.getReference("users").child("Privacy").push();

        Calendar cl = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd:hh:mm:ss a");
        String dateTime = dateFormat.format(cl.getTime());

        Hashtable<String, String> sendText = new Hashtable<String, String>();


        sendText.put("id", UserInfo.getID());
        sendText.put("name", UserInfo.getName());
        sendText.put("phone", "");
        sendText.put("dateOfBirth", "");
        sendText.put("date", dateTime);
        myRef.setValue(sendText)
                .addOnSuccessListener(aVoid -> {

                    Log.e(TAG, "addPrivacy: 입력 성공");
                    Log.e(TAG, "회원가입 성공 ");

                }).addOnFailureListener(e -> {

            Log.e(TAG, "addPrivacy: 입력 실패 ");

        });
    }

    public static void userAddPrivacy() {

        DatabaseReference myRef = database.getReference("users")
                .child(UserInfo.getID().replaceAll("\\.", ""))
                .child("Privacy").push();

        Calendar cl = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd:hh:mm:ss a");
        String dateTime = dateFormat.format(cl.getTime());

        Hashtable<String, String> sendText = new Hashtable<String, String>();


        sendText.put("id", UserInfo.getID());
        sendText.put("name", UserInfo.getName());
        sendText.put("phone", "");
        sendText.put("dateOfBirth", "");
        sendText.put("date", dateTime);
        myRef.setValue(sendText)
                .addOnSuccessListener(aVoid -> {

                    Log.e(TAG, "addPrivacy: 입력 성공");
                    Log.e(TAG, "회원가입 성공 ");

                }).addOnFailureListener(e -> {

            Log.e(TAG, "addPrivacy: 입력 실패 ");

        });
    }

    public static void developingMessage(Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage("현재 개발중인 기능입니다.\n빠른 시일내에 더 편리한 기능을 제공할 수 있도록 노력하겠습니다.");
        builder.setPositiveButton("확인", (dialog13, which) -> dialog13.dismiss());

        final AlertDialog dialog1 = builder.create();
        dialog1.show();
    }

    public static void getVersion() {
        DatabaseReference myRef = database.getReference("version");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    Version version = dataSnapshot.getValue(Version.class);
                    UserInfo.setVersion(version.getVersion());
                    UserInfo.setNoticeDate(version.getNoticeDate());
                    Log.e(TAG, "onDataChange: 데이터 버전 받음");
                    if (MyPageFragment.singleton != null) {
                        MyPageFragment.singleton.versionCheck();
                        MyPageFragment.singleton.noticeDateCheck();
                    }

//                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
//                        Version version = dataSnapshot1.getValue(Version.class);
//                        UserInfo.setVersion(version.getVersion());
//                        UserInfo.setNoticeDate(version.getNoticeDate());
//                        Log.e(TAG, "onDataChange: 데이터 버전 받음");
//                        if (MyPageFragment.singleton != null) {
//                            MyPageFragment.singleton.versionCheck();
//                            MyPageFragment.singleton.noticeDateCheck();
//                        }
//                        break;
//                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    public static void addFollower(String ID, String name, Activity activity) {

        DatabaseReference myRef = database.getReference("users").child(UserInfo.getID().replaceAll("\\.", "")).child("Follower").push();
        Hashtable<String, String> sendText = new Hashtable<String, String>();
        sendText.put("id", ID);
        sendText.put("name", name);
        sendText.put("date", currentDate());
        sendText.put("follower", "follower");

        myRef.setValue(sendText)
                .addOnSuccessListener(aVoid -> {
                    Log.e(TAG, "onSuccess: 팔로워 추가 : " + ID);
//                    Follower.follower.adapterNotifyDataSetChanged();
                    Follower.follower.setRecyclerView();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(activity, "네트워크를 확인해주세요.", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "onSuccess: 팔로워 추가 실패 발생: " + ID);
                });

    }


    public static void addFollowing(String ID, String name, Activity activity) {

        DatabaseReference myRef = database.getReference("users").child(ID.replaceAll("\\.", "")).child("Following").push();
        Hashtable<String, String> sendText = new Hashtable<String, String>();

        sendText.put("id", UserInfo.getID());
        sendText.put("name", UserInfo.getName());
        sendText.put("date", currentDate());
        sendText.put("following", "following");

        myRef.setValue(sendText)
                .addOnSuccessListener(aVoid -> {
                    Log.e(TAG, "onSuccess: 팔로잉 추가 : " + ID);
//                    Following.following.setRecyclerView();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(activity, "네트워크를 확인해주세요.", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "onSuccess: 팔로워 추가 실패 발생: " + ID);

                });

    }


    public static void addPlaceData(FirebaseDatabase database, String categoryName, String categoryGroupName, String placeName, String address, String roadAddress
            , String latitude, String longitude, String phone, String comment, String someThing, String favorites) {
        Calendar cl = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd:hh:mm:ss a");
        String dateTime = dateFormat.format(cl.getTime());

        DatabaseReference myRef = database.getReference("users").child(UserInfo.getID().replaceAll("\\.", "")).child("PlaceData").push();
        Hashtable<String, String> sendText = new Hashtable<String, String>();
        sendText.put("address", address);
        sendText.put("categoryGroupName", categoryGroupName);
        sendText.put("categoryName", categoryName);
        sendText.put("comment", comment);
        sendText.put("dateTime", dateTime);
        sendText.put("favorites", "n");
        sendText.put("id", UserInfo.getID());
        sendText.put("latitude", latitude);
        sendText.put("longitude", longitude);
        sendText.put("phone", phone);
        sendText.put("placeName", placeName);
        sendText.put("roadAddress", roadAddress);
        sendText.put("someThing", someThing);
        myRef.setValue(sendText).addOnSuccessListener(aVoid -> {

            ListFragment.singlton.setCategoryTabLayout();
//            ListFragment.singlton.setAdapter(0);
//            TimeLinePage.singlton.setAdapter(BottomNavi.timeLines);
//            TimeLinePage.singlton.setTimeLineSpinner();
        });

    }


    public static int getTimelineCategoryColor(ArrayList<CategoryData> categoryDataArrayList, TimeLine timeLine) {

        int drawable = 0;

        for (CategoryData categoryData : categoryDataArrayList) {
            if (timeLine.getCategoryName().equals(categoryData.getCD())) {
                switch (categoryData.getColor()) {
                    case Util.CATEGORY_COLOR_RED:
                        drawable = R.drawable.back_red_round_3dp;
                        break;
                    case Util.CATEGORY_COLOR_ORANGE:
                        drawable = R.drawable.back_orange_round_3dp;
                        break;
                    case Util.CATEGORY_COLOR_YELLOW:
                        drawable = R.drawable.back_yellow_round_3dp;
                        break;
                    case Util.CATEGORY_COLOR_GREEN:
                        drawable = R.drawable.back_green_round_3dp;
                        break;
                    case Util.CATEGORY_COLOR_BLUE:
                        drawable = R.drawable.back_blue_round_3dp;
                        break;
                    case Util.CATEGORY_COLOR_INDIGO:
                        drawable = R.drawable.back_indigo_round_3dp;
                        break;
                    case Util.CATEGORY_COLOR_PURPLE:
                        drawable = R.drawable.back_purple_round_3dp;
                        break;
                }

            }

        }

        if (drawable == 0) {
            drawable = R.drawable.back_black_round_3dp;

        }

        return drawable;
    }


    public static Bitmap getMarkerDrawableType(Context context, PlaceData placeData, ArrayList<CategoryData> categoryDataArrayList) {

        Drawable drawable = null;

        for (CategoryData categoryData : categoryDataArrayList) {
            Log.e(TAG, "getMarkerDrawableType: 포문 진입" );

            if (placeData.getCategoryName().equals(categoryData.getCD())) {
                switch (categoryData.getColor()) {
                    case Util.CATEGORY_COLOR_RED:
                        drawable = context.getResources().getDrawable(R.drawable.marker_light_red, null);
                        break;
                    case Util.CATEGORY_COLOR_ORANGE:
                        drawable = context.getResources().getDrawable(R.drawable.marker_orange, null);
                        break;
                    case Util.CATEGORY_COLOR_YELLOW:
                        drawable = context.getResources().getDrawable(R.drawable.marker_yellow, null);
                        break;
                    case Util.CATEGORY_COLOR_GREEN:
                        drawable = context.getResources().getDrawable(R.drawable.marker_green, null);
                        break;
                    case Util.CATEGORY_COLOR_BLUE:
                        drawable = context.getResources().getDrawable(R.drawable.marker_blue, null);
                        break;
                    case Util.CATEGORY_COLOR_INDIGO:
                        drawable = context.getResources().getDrawable(R.drawable.marker_indigo, null);
                        break;
                    case Util.CATEGORY_COLOR_PURPLE:
                        drawable = context.getResources().getDrawable(R.drawable.marker_purple, null);
                        break;
                }
                break;
            }

        }

        if (drawable == null) {
            drawable = context.getResources().getDrawable(R.drawable.marker_light_red, null);
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;

    }


    public static Bitmap getBitmapFromVectorDrawable(Context context, int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = (DrawableCompat.wrap(drawable)).mutate();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }



    public static void addCategoryData(String categoryName, Activity activity) {

        if (BottomNavi.categoryData.size() == 7) {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setMessage("카테고리는 최대 7개까지 등록할 수 있습니다.");
            builder.setPositiveButton("확인", (dialog, which) -> dialog.dismiss());
            AlertDialog dialog = builder.create();
            dialog.show();

        } else {

            DatabaseReference myRef = database.getReference("users").child(UserInfo.getID().replaceAll("\\.", "")).child("CategoryData").push();
            Hashtable<String, String> sendText = new Hashtable<String, String>();

            String color = "";

            switch (BottomNavi.categoryData.size()) {
                case 0:
                    color = CATEGORY_COLOR_RED;
                    break;
                case 1:
                    color = CATEGORY_COLOR_ORANGE;
                    break;
                case 2:
                    color = CATEGORY_COLOR_YELLOW;
                    break;
                case 3:
                    color = CATEGORY_COLOR_GREEN;
                    break;
                case 4:
                    color = CATEGORY_COLOR_BLUE;
                    break;
                case 5:
                    color = CATEGORY_COLOR_INDIGO;
                    break;
                case 6:
                    color = CATEGORY_COLOR_PURPLE;
                    break;
                default:
                    break;
            }

            sendText.put("CD", categoryName);
            sendText.put("color", color);
            myRef.setValue(sendText).addOnSuccessListener(aVoid -> {

                Toast.makeText(activity, "카테고리를 추가했습니다.", Toast.LENGTH_SHORT).show();
                if (CategoryList.categoryList != null) {
                    CategoryList.categoryList.categoryAdapter.notifyDataSetChanged();
                }

                if (TimeLinePage.singlton != null && BottomNavi.timeLines.size() > 0) {
                    TimeLinePage.singlton.setTimeLineSpinner();
                    TimeLinePage.singlton.setAdapter(BottomNavi.timeLines);
                }

                if (ListFragment.singlton != null) {
                    ListFragment.singlton.listSizeZeroCheck();
                    ListFragment.singlton.setCategoryTabLayout();
                    ListFragment.singlton.setAdapter(0);
                }

                MapFragment.singlton.categoryFilterButtonSetting();

            });
        }

    }

    public static void addTimeLine(String categoryName, String PlaceName, String date, String someThing, String title, Context context, TimeLineAdapter adapter) {
        ProgressDialog dialog = new ProgressDialog(context);
        dialog.setMessage("타임라인 추가중...");
        dialog.show();

        DatabaseReference myRef = database.getReference("users").child(UserInfo.getID().replaceAll("\\.", "")).child("TimeLine").push();
        String dateTime = "";
        if (date.length() == 0) {
            Calendar cl = Calendar.getInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss a");
            dateTime = dateFormat.format(cl.getTime());
        } else {
            dateTime = date;
        }


        String stringUri = "";
        if (AddTimeLineContent.addTimeLineContent != null
                && AddTimeLineContent.addTimeLineContent.getUploadImageUri()
                && AddTimeLineContent.addTimeLineContent.b.timeLineInputSomeThing.getVisibility() == View.VISIBLE) {

            stringUri = myRef.getKey();
            AddTimeLineContent.addTimeLineContent.imageUpload(stringUri, myRef, categoryName,
                    PlaceName, dateTime, someThing, title, context, dialog, adapter);

        } else {


            Hashtable<String, String> sendText = new Hashtable<String, String>();
            Log.e(TAG, "addTimeLine: categoryName : " + categoryName);
            sendText.put("categoryName", categoryName);
            sendText.put("PlaceName", PlaceName);
            sendText.put("date", dateTime);
            sendText.put("someThing", someThing);
            sendText.put("title", title);
            sendText.put("action", "n");
            sendText.put("imageKey", stringUri);

            myRef.setValue(sendText)
                    .addOnSuccessListener(aVoid -> {
                        Log.e(TAG, "addTimeLine: 타임라인 추가 성공");

                        if (TimeLinePage.singlton != null) {
                            Log.e(TAG, "onDataChange: 셋어댑터");
                            TimeLinePage.singlton.setAdapter(BottomNavi.timeLines);
                        }
//                        LoadingDataMove.addTimeline = true;
                        if (AddTimeLineContent.addTimeLineContent != null) {
                            if (AddTimeLineContent.addTimeLineContent.b.timeLineInputSomeThing.getVisibility() == View.VISIBLE) {
                                AddTimeLineContent.addTimeLineContent.finish();

                            }
                        }

                        TimeLinePage.singlton.timelineSizeCheck();

                        Log.e(TAG, "addTimeLine: dismiss 직전");
                        dialog.dismiss();
                    }).addOnFailureListener(e -> {
                dialog.dismiss();
                Log.e(TAG, "addTimeLine: 타임라인 추가 실패 ");
            });
        }

    }

    public static void updateTimeLineAction(int keyPosition, ArrayList<String> timeLineDataKeyList,
                                            String action) {


        DatabaseReference myRef = database.getReference("users").child(UserInfo.getID().replaceAll("\\.", ""));
        Map<String, Object> taskMap = new HashMap<String, Object>();

        taskMap.put(timeLineDataKeyList.get(keyPosition) + "/action", action);

        myRef.child("TimeLine").updateChildren(taskMap).addOnCompleteListener(task -> {

            Log.e(TAG, "updateTimeLineAction: 타임라인 업데이트 완료");
            if (BottomNavi.bottomNavi.b.viewPager.getCurrentItem() == 2) {
                Log.e(TAG, "updateTimeLineAction: 갱신, position : " + keyPosition + ", action : " + action);
            }

        }).addOnFailureListener(e -> Log.e(TAG, "onFailure: 타임라인 업데이트 실패 : " + e));

    }


    public static String currentDate() {
        Calendar cl = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd:hh:mm:ss a");
        String dateTime = dateFormat.format(cl.getTime());

        return dateTime;
    }


    public static void timeLineCategoryNameUpdate(ArrayList<String> clickedPlaceName, String afterCategoryName, ProgressDialog dialog) {

        if (BottomNavi.timeLines != null && BottomNavi.timeLines.size() > 0) {

            DatabaseReference myRef = database.getReference("users").child(UserInfo.getID().replaceAll("\\.", "")).child("TimeLine");

            Map<String, Object> taskMap = null;

            taskMap = new HashMap<String, Object>();
            taskMap.put("/categoryName", afterCategoryName);

            for (int i = 0; i < BottomNavi.timeLines.size(); i++) {

                for (int k = 0; k < clickedPlaceName.size(); k++) {

                    if (BottomNavi.timeLines.get(i).getPlaceName().equals(clickedPlaceName.get(k))) {

                        myRef.child(BottomNavi.timeLineDataKeyList.get(i)).updateChildren(taskMap)
                                .addOnCompleteListener(task -> {
                                    if (TimeLinePage.singlton != null) {
                                        TimeLinePage.singlton.setTimeLineSpinner();
                                        TimeLinePage.singlton.setAdapter(BottomNavi.timeLines);
                                    }
                                    dialog.dismiss();
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e(TAG, "onFailure: timeLineCategoryNameUpdate 실패 : " + e);
                            }
                        });

                    } else {
                        Log.e(TAG, "timeLineCategoryNameUpdate: 일치하는 타임라인 없음");
                        dialog.dismiss();
                    }

                }
            }

        }
    }


    public static ArrayList<MyDateTime> changeTimeLineData(ArrayList<TimeLine> timeLines) {
        ArrayList<MyDateTime> myDateTimes = new ArrayList<MyDateTime>();

        for (TimeLine timeLine : timeLines) {
            int year = Integer.parseInt(timeLine.getDate().substring(0, 4));
            int month = Integer.parseInt(timeLine.getDate().substring(5, 7));
            int dayOfMonth = Integer.parseInt(timeLine.getDate().substring(8, 10));
            int hour = Integer.parseInt(timeLine.getDate().substring(11, 13));
//            int hour = Integer.parseInt(timeLine.getDate().substring(11, 13));
            int minute = Integer.parseInt(timeLine.getDate().substring(14, 16));
            String placeName = timeLine.getPlaceName();
            String someThing = timeLine.getSomeThing();
            String action = timeLine.getAction();
            String imageKey = timeLine.getImageKey();
            myDateTimes.add(new MyDateTime(year, month, dayOfMonth, hour, minute, placeName, someThing, action, imageKey));
        }

        return myDateTimes;
    }


    public static ArrayList<DateItem> initItemList(ArrayList<MyDateTime> dateItems) {
        ArrayList<DateItem> result = new ArrayList<>();

        int year = 0, month = 0, dayOfMonth = 0;
        for (MyDateTime data : dateItems) {

            if (year != data.getYear() || month != data.getMonth() || dayOfMonth != data.getDayOfMonth()) {
                result.add(new TimeClassification(data.getYear(), data.getMonth(), data.getDayOfMonth(),
                        data.getHour(), data.getMinute()));
                year = data.getYear();
                month = data.getMonth();
                dayOfMonth = data.getDayOfMonth();
            }
            result.add(data);
        }
        return result;
    }

    public static ArrayList<MyDateTime> orderByTimeDesc(ArrayList<MyDateTime> dataset) {
        ArrayList<MyDateTime> result = dataset;
        for (int i = 0; i < result.size() - 1; i++) {
            for (int j = 0; j < result.size() - i - 1; j++) {
                if (result.get(j).getTime() < result.get(j + 1).getTime()) {
                    MyDateTime temp2 = result.remove(j + 1);
                    MyDateTime temp1 = result.remove(j);
                    result.add(j, temp2);
                    result.add(j + 1, temp1);
                }
            }
        }
        Collections.sort(result, new SortByDateDateItem());
        return result;
    }


    public static Bitmap resize(Context context, Uri uri, int resize) {
        Bitmap resizeBitmap = null;

        try {


            //절대경로 획득**
            Cursor c = context.getContentResolver().query(uri, null, null, null, null);
            c.moveToNext();

            Bitmap bitmapImage = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
            int width = bitmapImage.getWidth();
            int height = bitmapImage.getHeight();

            Bitmap src = BitmapFactory.decodeFile(c.getString(c.getColumnIndex(MediaStore.MediaColumns.DATA)));
            Bitmap resized = Bitmap.createScaledBitmap(src, width / 4, height / 4, true);
            resizeBitmap = resized;
        } catch (IOException e) {
            e.printStackTrace();
        }


//        BitmapFactory.Options options = new BitmapFactory.Options();
//        try {
//
//            int width = uri.getwi;
//            int height = options.outHeight;
//            int samplesize = 6;
//            BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri), null, options); // 1번
//
//
//
////            while (true) {//2번
////                if (width / 2 < resize || height / 2 < resize)
////                    break;
////                width /= 2;
////                height /= 2;
////                samplesize *= 2;
////            }
//
//            options.inSampleSize = 6;
//
//            Bitmap bitmap = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri), null, options); //3번
//
//            Bitmap src = BitmapFactory.decodeFile("/image.jpg", options);
//            bitmap.createScaledBitmap(src, width, height, true );
//            resizeBitmap = bitmap;
//
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
        return resizeBitmap;
    }

//    1번
//    uri는 선택한 이미지의 uri입니다.
//    1번을 수행함으로써 options의 이미지의 넓이,높이,샘플사이즈 값이 입력되는데, 여기서 샘플사이즈는 한 픽셀당 표현하는 픽셀수입니다.
//    해당 포스팅의 핵심 키워드로 샘플사이즈의 숫자를 높일수록 한 픽셀안에 표현하는 픽셀수가 많아짐으로써 이미지의 크기가 작아집니다.
//    예를 들어 샘플사이즈가 '4' 인 경우 4개의 픽셀을 1개의 픽셀에 표현하므로 이미지 크기는 1/4로 작아집니다.


    //2번
    //인자값으로 받은 resize는 원하는 이미지 리사이즈 크기 입니다. '500' 을 인자값으로 받았다면 이미지가 500에 가장 가깝게 줄입니다.
    //예를들어 원본 이미지의 넓이가 3000, 높이가 1000일 경우 2번을 수행하면 넓이는 1500, 높이는 1000, 샘플사이즈는 2입니다.
    //원하는 값으로 무조건 리사이즈하는게 아니라, 근사치로 줄입니다. 그 과정에서 샘플사이즈는 2배씩 커지게 되는데 그 이유는 샘플사이즈가 2배수만을 인식하기 때문입니다.

//    3번
//    2번으로 인해 정해진 샘플 사이즈로 리사이즈해서 비트맵으로 만듭니다.
//    해당 메소드로 리사이즈된 이미지의 넓이와 높이를 보면 핸드폰을 가로로 놓고 찍은 사진은 넓이가 넓고, 세로로 놓고 찍은 사진은 높이가 높기때문에 같은 이미지로 2번에서 넓이와 높이가 원하는 사이즈보다 작아지기 전까지 수행합니다.
//    그래야 이미지의 원본 화질을 최대한 보존할 수 있습니다. ( 본래 크기보다 억지로 늘릴려고 한다면 사진은 깨집니다. )


    public static void updateTimeLineSomeThing(int keyPosition, String someThing,
                                               ArrayList<String> timeLineDataKeyList, TextView textView) {

        DatabaseReference myRef = database.getReference("users").child(UserInfo.getID().replaceAll("\\.", ""));
        Map<String, Object> taskMap = new HashMap<String, Object>();

        taskMap.put(timeLineDataKeyList.get(keyPosition) + "/someThing", someThing);

        myRef.child("TimeLine").updateChildren(taskMap).addOnCompleteListener(task -> {
            Log.e(TAG, "onComplete:  완료됨");
            Toast.makeText(TimeLinePage.singlton.getActivity(), "변경 완료", Toast.LENGTH_SHORT).show();
            textView.setText(someThing);
            Log.e(TAG, "updateTimeLineSomeThing: 변경된 something : " + someThing);
        });

    }


    public static void deleteFollower(String followerKey) {
        DatabaseReference myRef = database.getReference("users").child(UserInfo.getID().replaceAll("\\.", ""));

        myRef.child("Follower").child(followerKey)
                .removeValue().addOnSuccessListener(aVoid -> {
            Log.e(TAG, "deleteFollower: 삭제 성공");
//            Follower.follower.adapterNotifyDataSetChanged();
        }).addOnFailureListener(e -> {
            Log.e(TAG, "deleteFollower: 삭제 실패 : " + e);
        });
    }

    public static void deleteOtherFollower(String ID) {
        DatabaseReference myRef = database.getReference("users").child(ID.replaceAll("\\.", ""));

        myRef.child("Follower").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                FollowerData followerData;
                if (dataSnapshot.exists()) {
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        followerData = dataSnapshot1.getValue(FollowerData.class);
                        if (followerData.getId().equals(UserInfo.getID())) {

                            myRef.child("Follower").child(dataSnapshot1.getKey())
                                    .removeValue().addOnSuccessListener(aVoid -> {
                                Log.e(TAG, "deleteOtherFollowing: 삭제 성공");
                            }).addOnFailureListener(e -> {
                                Log.e(TAG, "deleteOtherFollowing: 삭제 실패");
                            });

                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    public static void deleteOtherFollowing(String ID) {
        DatabaseReference myRef = database.getReference("users").child(ID.replaceAll("\\.", ""));

        myRef.child("Following").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                FollowingData following;
                if (dataSnapshot.exists()) {
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        following = dataSnapshot1.getValue(FollowingData.class);
                        if (following.getId().equals(UserInfo.getID())) {

                            myRef.child("Following").child(dataSnapshot1.getKey())
                                    .removeValue().addOnSuccessListener(aVoid -> {
                                Log.e(TAG, "deleteOtherFollowing: 삭제 성공");
                            }).addOnFailureListener(e -> {
                                Log.e(TAG, "deleteOtherFollowing: 삭제 실패");
                            });

                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


    public static void deleteFollowing(String followingKey) {
        DatabaseReference myRef = database.getReference("users").child(UserInfo.getID().replaceAll("\\.", ""));

        myRef.child("Following").child(followingKey)
                .removeValue().addOnSuccessListener(aVoid -> {
//            Following.following.adapterNotifyDataSetChanged();
            Log.e(TAG, "deleteFollowing: 삭제 성공");
        }).addOnFailureListener(e -> {
            Log.e(TAG, "deleteFollowing: 삭제 실패 : " + e);
        });
    }

    public static void updateInviteAccept(String userID, String findID) {
        DatabaseReference myRef = Util.getFirebaseDatabase().getReference("users").child(userID.replaceAll("\\.", ""));

        myRef.child("InviteData").orderByChild("inviteID").equalTo(findID).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                myRef.child("InviteData").child(dataSnapshot.getKey()).removeValue();

//                Map<String, Object> taskMap = null;
//
//                taskMap = new HashMap<String, Object>();
//                taskMap.put(dataSnapshot.getKey() + "/accept", "y");
//
//                myRef.child("InviteData").updateChildren(taskMap).addOnCompleteListener(task -> {
//                    Log.e(TAG, "InviteDataAccept onComplete:  완료됨");
//                });

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    public static void rejectInvite(FirebaseDatabase database, String userID, String findID) {
        DatabaseReference myRef = database.getReference("users").child(userID.replaceAll("\\.", ""));

        myRef.child("InviteData").orderByChild("inviteID").equalTo(findID).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                myRef.child("InviteData").child(dataSnapshot.getKey()).removeValue();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    public static void removeTimeLine(ArrayList<Integer> positions, ArrayList<String> timelineKeyList, Activity activity) {
        ProgressDialog dialog = new ProgressDialog(activity);
        dialog.setMessage("삭제중...");

        if (positions.size() > 0) {
            for (Integer i : positions) {

                DatabaseReference myRef = database.getReference("users")
                        .child(UserInfo.getID().replaceAll("\\.", "")).child("TimeLine").child(timelineKeyList.get(i));
                myRef.removeValue()
                        .addOnSuccessListener(aVoid -> {
                            Log.e(TAG, "delete 타임라인 : 데이터 삭제 성공");
//                            Toast.makeText(activity, "삭제 완료", Toast.LENGTH_SHORT).show();
                            if (TimeLinePage.singlton != null) {
                                TimeLinePage.singlton.checkBoxTaskFinish();
                                dialog.dismiss();
                            }

                        }).addOnFailureListener(e -> {
                    Log.e(TAG, "delete 타임라인 : 데이터 삭제 실패");
                });
            }
        }
    }


    public static void removeOneTimeLine(int position, TimeLineAdapter adapter) {
        ArrayList<Integer> selectedTimeLinesPosition = new ArrayList<Integer>();
        String key = BottomNavi.timeLineDataKeyList.get(position);

        DatabaseReference myRef = database.getReference("users")
                .child(UserInfo.getID().replaceAll("\\.", "")).child("TimeLine").child(key);
        myRef.removeValue()
                .addOnSuccessListener(aVoid -> {
                    Log.e(TAG, "delete 타임라인 : 데이터 삭제 성공");

//                    adapter.notifyDataSetChanged();

                }).addOnFailureListener(e -> {
            Log.e(TAG, "delete 타임라인 : 데이터 삭제 실패");
        });

    }


    public static void updateFirebase(String key, PlaceData placeData, String afterPlaceName) {
        placeData.setPlaceName(afterPlaceName);
        Log.e(TAG, "updateFirebase: key = " + key);
        Log.e(TAG, "updateFirebase: afterPlaceName = " + afterPlaceName);
        DatabaseReference myRef = database.getReference();
        Map<String, Object> postValues = placeData.toMap(afterPlaceName);
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(key, postValues);
        myRef.updateChildren(childUpdates);
    }

    public static String replaceTel(String num) {

        String removeDash = num.replaceAll("-", "");
        Log.e(TAG, "replaceTel: removeDash : " + removeDash);

        String reTelNum = "";
        if (removeDash.length() == 8) {
            reTelNum = removeDash.substring(0, 4) + "-" + removeDash.substring(4, 8);
        } else if (removeDash.length() == 9) {
            reTelNum = removeDash.substring(0, 2) + "-" + removeDash.substring(2, 5) + "-" + removeDash.substring(5, 9);
        } else if (removeDash.length() == 10) {
            if (num.substring(0, 2).equals("02")) {
                reTelNum = removeDash.substring(0, 2) + "-" + removeDash.substring(2, 6) + "-" + removeDash.substring(6, 10);
            } else {
                reTelNum = removeDash.substring(0, 3) + "-" + removeDash.substring(3, 6) + "-" + removeDash.substring(6, 10);
            }
        } else if (removeDash.length() == 11) {
            reTelNum = removeDash.substring(0, 3) + "-" + removeDash.substring(3, 7) + "-" + removeDash.substring(7, 11);
        }
        return reTelNum;
    }

    public static void showNaviNaver(String destination, Double longitude, Double latitude, Context context) {
        // packagemanager로 특정 어플의 packgeName으로 어플이 깔려있는지 안깔려있는지 확일 할 수 있다.
        PackageManager pm = context.getPackageManager();
        String dName = "";
        try {
            dName = URLEncoder.encode(destination, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        try {
            if (pm.getPackageInfo("com.nhn.android.nmap", PackageManager.GET_SIGNATURES) != null) {
                String url = "nmap://route/car_gray?dlat=" + latitude + "&dlng=" + longitude + "&dname=" + dName + "&appname=com.example.workaddict";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                context.startActivity(intent);
            } else {
                Intent intent = new Intent(Intent.ACTION_VIEW,
//                        Uri.parse("https://play.google.com/store/apps/details?id=com.nhn.android.nmap"));
                        Uri.parse("market://details?id=com.nhn.android.nmap"));

                context.startActivity(intent);
                Toast.makeText(context, context.getString(R.string.navi_install), Toast.LENGTH_LONG).show();
            }

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void showNaviKakao(String destination, Double longitude, Double latitude, Context context) {
        KakaoNaviService.getInstance();
        try {
            //카카오 내비가 설치 되어 있는지 확인.
            //KakaoNaviService.getInstance()을 이용해 싱글톤을 사용하지 않으면 isKakaoNaviInstalled가 컨텍스트를 참조하지 못한다.

            if (KakaoNaviService.getInstance().isKakaoNaviInstalled(context)) {
                //카카오 내비가 설치 되어 있다면 KakaoNaviParams 빌더를 통해 도착지 정보와 출발지 정보를 넘겨준다.
                com.kakao.kakaonavi.Location kakao = Destination.newBuilder(destination, longitude, latitude).build();
                KakaoNaviParams params = KakaoNaviParams.newBuilder(kakao)
                        .setNaviOptions(NaviOptions.newBuilder()
                                .setCoordType(CoordType.WGS84) // WGS84로 설정해야 경위도 좌표 사용 가능.
                                .setRpOption(RpOption.NO_AUTO)
                                .setStartAngle(200) //시작 앵글 크기 설정.
                                .setVehicleType(VehicleType.FIRST).build()).build(); //길 안내 차종 타입 설정
                //카카오 내비앱 실행.
                KakaoNaviService.getInstance().shareDestination(context, params);
            } else {
                //카카오 내비가 설치되어 있지 않다면, 카카오 내비의 설치를 요청한다.
                Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=com.locnall.KimGiSa"));
//                        Uri.parse("market://details?id=com.locnall.KimGiSa"));
                context.startActivity(intent);
                Toast.makeText(context, context.getString(R.string.navi_install), Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Log.e("네비연동 에러", e + "");
        }
    }


    public static void setTextColor(Context context, int strAddress, TextView textView, String colorString, int start, int end) {
        String a = context.getString(strAddress);
        SpannableStringBuilder ssb = new SpannableStringBuilder(a);
        ssb.setSpan(new ForegroundColorSpan(Color.parseColor(colorString)), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(ssb);
    }

    public static void setTextColor(Context context, int strAddress, TextView textView, int colorString, int start, int end) {
        String a = context.getString(strAddress);
        SpannableStringBuilder ssb = new SpannableStringBuilder(a);
        ssb.setSpan(new ForegroundColorSpan(colorString), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(ssb);
    }

    public static void fileDownload(String url, String fileName, Activity activity) {
        Toast.makeText(activity, "이미지를 다운로드 중입니다", Toast.LENGTH_SHORT).show();

        Log.e(TAG, "fileDownload: url : " + url);
        Log.e(TAG, "fileDownload: fileName : " + fileName);
        //아래 줄은 외부저장소의 경로를 가져오는 코드  : 경로 =/mnt/sdcard/Download
        File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        String strDir = file.getAbsolutePath();

        storagePermissionCheck(activity, url, strDir, fileName);

    }


    public static void storagePermissionCheck(Activity activity, String url, String dir, String fileName) {

        boolean result = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_DENIED) {
                //거부했을 때 다시 요청
                ActivityCompat.requestPermissions(activity, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            } else {
                //동의 했을 때
                result = true;
            }

        } else {
            //버전 낮은경우
            result = true;
        }

        if (result) Util.startDownload(activity, url, dir, fileName);

    }


    public static void startDownload(Activity activity, String url, String dir, String fileName) {

        File file = new File(Util.getUrlDecode(dir));


        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
        request.setTitle(fileName);
        request.setDescription("Downloading file...");
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationUri(Uri.fromFile(file));
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "" + System.currentTimeMillis());
        request.setAllowedOverMetered(true);
        request.setAllowedOverRoaming(true);
        DownloadManager manager = (DownloadManager) activity.getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);


//            final Timer timer = new Timer();

//            new TimerTask() {
//                @Override
//                public void run() {
//
//                    Log.e(TAG, "run: status : " + status);
//                    if (status == DownloadManager.STATUS_SUCCESSFUL) {
//                        activity.runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                Log.e(TAG, "run: ");
//                                Toast.makeText(activity, "이미지 다운로드가 완료되었습니다.", Toast.LENGTH_SHORT).show();
//                                timer.cancel();
//                                cursor.close();
//                            }
//                        });
//                    }
//
//                    timer.schedule(this, 100, 100);
//                }
//            };


    }

    public static String getUrlDecode(String _strFileName) {
        String strRet = null;
        try {
            strRet = URLDecoder.decode(_strFileName, "UTF-8");
        } catch (UnsupportedEncodingException e) {

            strRet = "";
        }

        return strRet;
    }

}
