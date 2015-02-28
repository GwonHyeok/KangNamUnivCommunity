package com.yscn.knucommunity.Items;

/**
 * Created by GwonHyeok on 14. 11. 3..
 */
public class StudentCouncilListItems {
    private String id;
    private String title;
    private String summary;

    public StudentCouncilListItems(String id, String title, String summary) {
        this.id = id;
        this.title = title;
        this.summary = summary;
    }

    public String getTitle() {
        return title;
    }

    public String getSummary() {
        return summary;
    }

    public String getId() {
        return id;
    }
}
