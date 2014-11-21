package com.yscn.knucommunity.Items;

/**
 * Created by GwonHyeok on 14. 11. 7..
 */
public class MajorDetailItems {

    private String name, major, phone, email;

    public MajorDetailItems(String name, String major, String phone, String email) {
        this.name = name;
        this.major = major;
        this.phone = phone;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public String getMajor() {
        return major;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }
}
