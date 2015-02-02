package com.yscn.knucommunity.Items;

/**
 * Created by GwonHyeok on 15. 2. 2..
 */
public class LibrarySearchListItems {
    private String bookThumbnail, title, callno, year, holding, lendtitle, author;

    public LibrarySearchListItems(String bookThumbnail, String title, String callno, String author,
                                  String year, String holding, String lendtitle) {
        this.bookThumbnail = bookThumbnail;
        this.title = title;
        this.callno = callno;
        this.author = author;
        this.year = year;
        this.holding = holding;
        this.lendtitle = lendtitle;
    }

    public String getBookThumbnail() {
        return bookThumbnail;
    }

    public String getTitle() {
        return title;
    }

    public String getCallno() {
        return callno;
    }

    public String getYear() {
        return year;
    }

    public String getLendtitle() {
        return lendtitle;
    }

    public String getHolding() {
        return holding;
    }

    public String getAuthor() {
        return author;
    }
}
