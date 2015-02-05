package com.yscn.knucommunity.Util;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;

import com.yscn.knucommunity.Activity.Splash;

/**
 * Created by GwonHyeok on 14. 11. 26..
 */
public class UserData {
    private static UserData instance = null;
    private String studentNumber;
    private String studentName;
    private String userToken;
    private String userRating;
    private String phoneNumber;
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

    public void logoutUser() {
        UserDataPreference userDataPreference = getUserDataPreference();
        SessionDataPreference sessionDataPreference = getSessionDataPreference();
        SharedPreferences sharedPreferences = getSharedPreference();

        userDataPreference.removeAll();
        sessionDataPreference.removeAll();
        sharedPreferences.edit().clear().apply();
        Context context = ApplicationContextProvider.getContext();
        Intent intent = new Intent(context, Splash.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
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

    public boolean checkToken(String serverToken) {
        return serverToken.equals(getUserToken());
    }

    private UserDataPreference getUserDataPreference() {
        Context mContext = ApplicationContextProvider.getContext();
        return new UserDataPreference(mContext);
    }

    private SessionDataPreference getSessionDataPreference() {
        Context mContext = ApplicationContextProvider.getContext();
        return new SessionDataPreference(mContext);
    }

    private SharedPreferences getSharedPreference() {
        Context mContext = ApplicationContextProvider.getContext();
        return PreferenceManager.getDefaultSharedPreferences(mContext);
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
