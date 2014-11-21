package com.yscn.knucommunity.Items;

/**
 * Created by GwonHyeok on 14. 11. 3..
 */
public class MarketDetailListItems {
    private String title, name, time;
    private int replyCount;

    public MarketDetailListItems(String title, String name, String time, int replyCount) {
        this.title = title;
        this.name = name;
        this.time = time;
        this.replyCount = replyCount;
    }

    public String getTitle() {
        return title;
    }

    public String getName() {
        return name;
    }

    public String getTime() {
        return time;
    }

    public int getReplyCount() {
        return replyCount;
    }
}
