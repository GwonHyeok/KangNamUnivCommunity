package com.yscn.knucommunity.Items;

/**
 * Created by GwonHyeok on 15. 1. 8..
 */
public class CommentListItems {

    private String name, comment, studentnumber, time;

    public CommentListItems(String name, String comment, String studentnumber, String time) {
        this.name = name;
        this.comment = comment;
        this.studentnumber = studentnumber;
        this.time = time;
    }

    public String getStudentnumber() {
        return studentnumber;
    }

    public String getComment() {
        return comment;
    }

    public String getName() {
        return name;
    }

    public String getTime() {
        return time;
    }
}
