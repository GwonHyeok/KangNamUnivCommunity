package com.yscn.knucommunity.Items;

/**
 * Created by GwonHyeok on 15. 1. 20..
 */
public class NoticeItems {
    private String id, title, url, time, readcount;

    public NoticeItems(String id, String title, String url, String time, String readcount) {
        this.id = id;
        this.time = time;
        this.title = title;
        this.url = url;
        this.readcount = readcount;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public String getTime() {
        return time;
    }

    public String getReadcount() {
        return readcount;
    }
}
