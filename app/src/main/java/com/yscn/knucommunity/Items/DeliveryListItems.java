package com.yscn.knucommunity.Items;

/**
 * Created by GwonHyeok on 14. 11. 4..
 */
public class DeliveryListItems {
    private String name, telnum, imagepath;

    public DeliveryListItems(String name, String telnum, String imagepath) {
        this.name = name;
        this.telnum = telnum;
        this.imagepath = imagepath;
    }

    public String getTelnum() {
        return telnum;
    }

    public String getName() {
        return name;
    }

    public String getImagepath() {
        return imagepath;
    }
}
