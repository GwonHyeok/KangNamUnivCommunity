package com.yscn.knucommunity.Util;

import android.content.Context;

/**
 * Created by GwonHyeok on 15. 1. 6..
 */
public class UserDataPreference extends BaseSharedPreference {

    private final String mStudentNumber = "studentnumber";
    private final String mStudentToken = "studenttoken";
    private final String mStudentName = "studentname";
    private final String mStudentNickname = "studentnickname";
    private final String mStudentRating = "studentrating";

    public UserDataPreference(Context mContext) {
        super(mContext, "UserData");
    }

    public String getStudentNumber() {
        return getValue(mStudentNumber, null);
    }

    public void setStudentNumber(String studentNumber) {
        putValue(this.mStudentNumber, studentNumber);
    }

    public String getStudentRating() {
        return getValue(this.mStudentRating, null);
    }

    public void setStudentRating(String studentRating) {
        putValue(this.mStudentRating, studentRating);
    }

    public String getToken() {
        return getValue(mStudentToken, null);
    }

    public void setToken(String token) {
        putValue(this.mStudentToken, token);
    }

    public String getStudentName() {
        return getValue(mStudentName, null);
    }

    public void setStudentName(String studentName) {
        putValue(this.mStudentName, studentName);
    }

    public String getStudentNickname() {
        return getValue(mStudentNickname, null);
    }

    public void setStudentNickname(String studentNickname) {
        putValue(this.mStudentNickname, studentNickname);
    }
}