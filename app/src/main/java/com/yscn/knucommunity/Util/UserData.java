package com.yscn.knucommunity.Util;

import android.content.Context;
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
        UserDataPreference userDataPreference = getUserDataPreference();
        if (studentNumber == null) {
            studentNumber = userDataPreference.getStudentNumber();
        }
        return studentNumber;
    }

    public void setStudentNumber(String studentNumber) {
        this.studentNumber = studentNumber;
    }

    public String getStudentName() {
        UserDataPreference userDataPreference = getUserDataPreference();
        if (studentName == null) {
            studentName = userDataPreference.getStudentName();
        }
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getUserToken() {
        UserDataPreference userDataPreference = getUserDataPreference();
        if (userToken == null) {
            userToken = userDataPreference.getToken();
        }
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
        UserDataPreference userDataPreference = getUserDataPreference();
        if (userRating == null) {
            userRating = userDataPreference.getStudentRating();
        }
        return userRating;
    }

    public void setUserRating(String userRating) {
        this.userRating = userRating;
    }

    private UserDataPreference getUserDataPreference() {
        Context mContext = ApplicationContextProvider.getContext();
        return new UserDataPreference(mContext);
    }
}
