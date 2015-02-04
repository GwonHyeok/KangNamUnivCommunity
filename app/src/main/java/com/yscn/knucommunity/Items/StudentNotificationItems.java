package com.yscn.knucommunity.Items;

/**
 * Created by GwonHyeok on 15. 2. 4..
 */
public class StudentNotificationItems {
    private String title, boardid, contentid, time, writer;
    private Type type;

    public StudentNotificationItems(Type type, String writer, String title, String boardid, String contentid, String time) {
        this.type = type;
        this.writer = writer;
        this.time = time;
        this.title = title;
        this.contentid = contentid;
        this.boardid = boardid;
    }

    public String getTime() {
        return time;
    }

    public String getContentid() {
        return contentid;
    }

    public String getBoardid() {
        return boardid;
    }

    public String getTitle() {
        return title;
    }

    public Type getType() {
        return type;
    }

    public String getWriter() {
        return writer;
    }

    public enum Type {Notify, Myboard}
}
