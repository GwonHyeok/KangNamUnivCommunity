package com.yscn.knucommunity.Items;

/**
 * Created by GwonHyeok on 15. 1. 28..
 */
public class ShareTaxiListItems {
    private String departuretime, destination, departure, writer, isLeave;
    private String[] shareperson;

    public ShareTaxiListItems(String writer, String isLeave, String departuretime, String destination, String departure, String[] shareperson) {
        this.writer = writer;
        this.isLeave = isLeave;
        this.departuretime = departuretime;
        this.destination = destination;
        this.departure = departure;
        this.shareperson = shareperson;
    }

    public String getDeparture() {
        return departure;
    }

    public void setDeparture(String departure) {
        this.departure = departure;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getDeparturetime() {
        return departuretime;
    }

    public void setDeparturetime(String departuretime) {
        this.departuretime = departuretime;
    }

    public String[] getShareperson() {
        return shareperson;
    }

    public void setShareperson(String[] shareperson) {
        this.shareperson = shareperson;
    }

    public String getWriter() {
        return writer;
    }

    public void setWriter(String writer) {
        this.writer = writer;
    }

    public String getIsLeave() {
        return isLeave;
    }

    public void setIsLeave(String isLeave) {
        this.isLeave = isLeave;
    }
}
