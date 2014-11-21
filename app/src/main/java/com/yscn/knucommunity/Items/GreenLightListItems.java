package com.yscn.knucommunity.Items;

/**
 * Created by GwonHyeok on 14. 11. 3..
 */
public class GreenLightListItems {

    private String title, time;
    private int replyCount;

    public GreenLightListItems(String title, String time, int replyCount) {
        this.time = time;
        this.title = title;
        this.replyCount = replyCount;
    }

    public String getTitle() {
        return title;
    }

    public String getTime() {
        return time;
    }

    public int getReplyCount() {
        return replyCount;
    }
}
