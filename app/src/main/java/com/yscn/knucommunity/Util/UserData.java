package com.yscn.knucommunity.Util;

import android.graphics.Bitmap;

/**
 * Created by GwonHyeok on 14. 11. 26..
 */
public class UserData {
    private static UserData instance = null;
    private String studentNumber;
    private String studentName;
    private String userToken;
    private String userRating;
    private Bitmap userProfile;

    private UserData() {

    }

    public static UserData getInstance() {
        if (instance == null) {
            instance = new UserData();
        }
        return instance;
    }

    public String getStudentNumber() {
        return studentNumber;
    }

    public void setStudentNumber(String studentNumber) {
        this.studentNumber = studentNumber;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }

    public Bitmap getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(Bitmap userProfile) {
        this.userProfile = userProfile;
    }

    public String getUserRating() {
        return userRating;
    }

    public void setUserRating(String userRating) {
        this.userRating = userRating;
    }
}
