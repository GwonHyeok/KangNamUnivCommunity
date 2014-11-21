package com.yscn.knucommunity.Items;

/**
 * Created by GwonHyeok on 14. 11. 22..
 */
public class SchoolRestrauntItems {
    private String foodName, foodPrice;

    public SchoolRestrauntItems(String foodName, String foodPrice) {
        this.foodName = foodName;
        this.foodPrice = foodPrice;
    }

    public String getFoodPrice() {
        return foodPrice;
    }

    public String getFoodName() {
        return foodName;
    }
}
