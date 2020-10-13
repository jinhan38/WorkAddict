package com.example.workaddict;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SaveSharedPreferences {
    static final String FIRST_VISIT_USER = "firstVisitUser";
    static final String PREF_PERMISSION = "permission";
    static final String PREF_AUTO_LOGIN = "autoLogin";
    static final String PREF_ID = "id";
    static final String PREF_PW = "pw";
    static final String PREF_NAME = "name";
    static final String PREF_IS_LOGIN = "isLogin";
    static final String MARKETING_AGREEMENT = "marketingAgreement";
    static final String NOTICE_DATE = "noticeDate";
    static final String LOGIN_PHOTO_URL = "loginPhotoUrl";
    static final String DELETE_LOGIN_PHOTO_URL = "deleteLoginPhotoUrl";




    public static SharedPreferences getSharedPreferences(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }



    public static void setDeleteLoginPhotoUrl(Context context, String deleteLoginPhotoUrl){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(DELETE_LOGIN_PHOTO_URL, deleteLoginPhotoUrl);
        editor.apply();
    }

    public static String getDeleteLoginPhotoUrl(Context context){
        return getSharedPreferences(context).getString(DELETE_LOGIN_PHOTO_URL, "n");
    }

    public static void setLoginPhotoUrl(Context context, String loginPhotoUrl){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(LOGIN_PHOTO_URL, loginPhotoUrl);
        editor.apply();
    }

    public static String getLoginPhotoUrl(Context context){
        return getSharedPreferences(context).getString(LOGIN_PHOTO_URL, "");
    }


    public static void setFirstVisitUser(Context context, String firstVisitUser){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(FIRST_VISIT_USER, firstVisitUser);
        editor.apply();
    }

    public static String getFirstVisitUser(Context context){
        return getSharedPreferences(context).getString(FIRST_VISIT_USER, "y");
    }

    public static void setNoticeDate(Context context, String noticeDate){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(NOTICE_DATE, noticeDate);
        editor.apply();
    }

    public static String getNoticeDate(Context context){
        return getSharedPreferences(context).getString(NOTICE_DATE, "n");
    }

    public static void setMarketingAgreement(Context context, String agreement){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(MARKETING_AGREEMENT, agreement);
        editor.apply();
    }

    public static String getMarketingAgreement(Context context){
        return getSharedPreferences(context).getString(MARKETING_AGREEMENT, "n");
    }

    public static void setPrefIsLogin(Context ctx, String isLogin) {
        android.content.SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_IS_LOGIN, isLogin);
        editor.apply();
    }

    public static String getPrefIsLogin(Context ctx) {
        return getSharedPreferences(ctx).getString(PREF_IS_LOGIN, "n");
    }


    public static void setPermission(Context ctx, String permission) {
        android.content.SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_PERMISSION, permission);
        editor.apply();
    }

    public static String getPermission(Context ctx) {
        return getSharedPreferences(ctx).getString(PREF_PERMISSION, "n");
    }


    public static void setPrefAutoLogin(Context ctx, String autoLogin) {
        android.content.SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_AUTO_LOGIN, autoLogin);
        editor.apply();
    }

    public static String getPrefAutoLogin(Context ctx) {
        return getSharedPreferences(ctx).getString(PREF_AUTO_LOGIN, "n");
    }

    public static void setPrefId(Context ctx, String id) {
        android.content.SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_ID, id);
        editor.apply();
    }

    public static String getPrefId(Context ctx) {
        return getSharedPreferences(ctx).getString(PREF_ID, "");
    }

    public static void setPrefName(Context ctx, String name) {
        android.content.SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_NAME, name);
        editor.apply();
    }

    public static String getPrefName(Context ctx) {
        return getSharedPreferences(ctx).getString(PREF_NAME, "");
    }

    public static void setPrefPw(Context ctx, String pw) {
        android.content.SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_PW, pw);
        editor.apply();
    }

    public static String getPrefPw(Context ctx) {
        return getSharedPreferences(ctx).getString(PREF_PW, "");
    }

}
