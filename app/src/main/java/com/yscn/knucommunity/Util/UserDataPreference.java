package com.yscn.knucommunity.Util;

import android.content.Context;

/**
 * Created by GwonHyeok on 15. 1. 6..
 */
public class UserDataPreference extends BaseSharedPreference {

    private final String mStudentNumber = "studentnumber";
    private final String mStudentToken = "studenttoken";
    private final String mStudentName = "studentname";

    public UserDataPreference(Context mContext) {
        super(mContext, "UserData");
    }

    public void setStudentNumber(String studentNumber) {
        putValue(this.mStudentNumber, studentNumber);
    }

    public void setToken(String token) {
        putValue(this.mStudentToken, token);
    }

    public void setStudentName(String studentName) {
        putValue(this.mStudentName, studentName);
    }

    public String getStudentNumber() {
        return getValue(mStudentNumber, null);
    }

    public String getToken() {
        return getValue(mStudentToken, null);
    }

    public String getStudentName() {
        return getValue(mStudentName, null);
    }
}
