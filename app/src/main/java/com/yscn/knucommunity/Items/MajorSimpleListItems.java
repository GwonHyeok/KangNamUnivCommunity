package com.yscn.knucommunity.Items;

/**
 * Created by GwonHyeok on 14. 11. 19..
 */
public class MajorSimpleListItems {
    private String majorName;
    private String majorHomepage;
    private String majorType;

    public MajorSimpleListItems(String majorName, String majorHomepage, String majorType) {
        this.majorHomepage = majorHomepage;
        this.majorName = majorName;
        this.majorType = majorType;
    }

    public String getMajorName() {
        return majorName;
    }

    public String getMajorHomepage() {
        return majorHomepage;
    }

    public String getMajorType() {
        return majorType;
    }
}
