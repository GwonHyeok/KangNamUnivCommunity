package com.yscn.knucommunity.Util;

import android.content.Context;

/**
 * Created by GwonHyeok on 15. 2. 1..
 */
public class SessionDataPreference extends BaseSharedPreference {

    public SessionDataPreference(Context mContext) {
        super(mContext, "SessionData");
    }

    protected void putSession(String value) {
        putValue("ci_session", value);
    }

    protected void putDomain(String value) {
        putValue("domain", value);
    }

    protected void putPath(String value) {
        putValue("path", value);
    }

    protected String getSession() {
        return getValue("ci_session", null);
    }

    protected String getDomain() {
        return getValue("domain", null);
    }

    protected String getPath() {
        return getValue("path", null);
    }
}
