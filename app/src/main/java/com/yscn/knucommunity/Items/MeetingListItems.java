package com.yscn.knucommunity.Items;

/**
 * Created by GwonHyeok on 14. 11. 3..
 */
public class MeetingListItems {
    private String title, time, summary;
    private int replyCount, peopleCount;
    private TYPE type;

    public MeetingListItems(TYPE type, String title, String summary, String time, int replyCount, int peopleCount) {
        this.time = time;
        this.title = title;
        this.replyCount = replyCount;
        this.summary = summary;
        this.peopleCount = peopleCount;
        this.type = type;
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

    public String getSummary() {
        return summary;
    }

    public TYPE getType() {
        return type;
    }

    public int getPeopleCount() {
        return peopleCount;
    }

    public static enum TYPE {BOY_GROUP, GIRL_GROUP, SUCCESS_GROUP}
}
