package com.yscn.knucommunity.Items;

/**
 * Created by GwonHyeok on 14. 11. 4..
 */
public class DeliveryListItems {
    private String name, telnum;

    public DeliveryListItems(String name, String telnum) {
        this.name = name;
        this.telnum = telnum;
    }

    public String getTelnum() {
        return telnum;
    }

    public String getName() {
        return name;
    }
}
