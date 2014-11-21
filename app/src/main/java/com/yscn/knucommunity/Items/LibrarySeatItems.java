package com.yscn.knucommunity.Items;

/**
 * Created by GwonHyeok on 14. 11. 21..
 */
public class LibrarySeatItems {
    private int totalSeat;
    private int emptySeat;
    private int useSeat;

    public LibrarySeatItems(int totalSeat, int useSeat, int emptySeat) {
        this.totalSeat = totalSeat;
        this.emptySeat = emptySeat;
        this.useSeat = useSeat;
    }

    public int getTotalSeat() {
        return totalSeat;
    }

    public int getEmptySeat() {
        return emptySeat;
    }

    public int getUseSeat() {
        return useSeat;
    }
}
