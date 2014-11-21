package com.yscn.knucommunity.Items;

/**
 * Created by GwonHyeok on 14. 11. 3..
 */
public class FreeBoardListItems {

    private String title, time, name;
    private int replyCount;

    public FreeBoardListItems(String title, String name, String time, int replyCount) {
        this.time = time;
        this.title = title;
        this.replyCount = replyCount;
        this.name = name;
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

    public String getName() {
        return name;
    }
}
