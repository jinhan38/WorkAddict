package kr.co.workaddict.Utility;

import android.app.Activity;

public class UserInfo extends Activity {
    private static String ID = "";
    private static String PW = "";
    private static String name = "";
    private static String dob = "";
    private static String phone = "";
    private static String version = "";
    private static String noticeDate = "";
    private static String loginPhotoUrl = "";




    public static void logoutReset(){
        ID = "";
        PW = "";
        name = "";
        dob = "";
        phone = "";
        loginPhotoUrl = "";
    }


    public static String getLoginPhotoUrl() {
        return loginPhotoUrl;
    }

    public static void setLoginPhotoUrl(String loginPhotoUrl) {
        UserInfo.loginPhotoUrl = loginPhotoUrl;
    }

    public static String getNoticeDate() {
        return noticeDate;
    }

    public static void setNoticeDate(String noticeDate) {
        UserInfo.noticeDate = noticeDate;
    }

    public static String getVersion() {
        return version;
    }

    public static void setVersion(String version) {
        UserInfo.version = version;
    }

    public static String getID() {
        return ID;
    }

    public static void setID(String ID) {
        UserInfo.ID = ID;
    }

    public static String getPW() {
        return PW;
    }

    public static void setPW(String PW) {
        UserInfo.PW = PW;
    }

    public static String getName() {
        return name;
    }

    public static void setName(String name) {
        UserInfo.name = name;
    }

    public static String getDob() {
        return dob;
    }

    public static void setDob(String dob) {
        UserInfo.dob = dob;
    }

    public static String getPhone() {
        return phone;
    }

    public static void setPhone(String phone) {
        UserInfo.phone = phone;
    }


}
