package com.eot_app.nav_menu.quote.quotes_list_pkg.qoute_model_pkg;

import com.eot_app.nav_menu.quote.quotes_list_pkg.QuotesFilter;
import com.eot_app.utility.App_preference;

import java.util.List;

public class Quote_ReQ {
     String compId;
     int index;
     int limit;
     String usrId;
     String search;
     List<String> status;
     String dtf;
     String dtt;

//    private String forMobile;

    public Quote_ReQ(int index, int limit) {
        this.index = index;
        this.limit = limit;
        compId = App_preference.getSharedprefInstance().getLoginRes().getCompId();
        //forMobile="1";
        usrId = App_preference.getSharedprefInstance().getLoginRes().getUsrId();
    }

    public void addFilters(QuotesFilter quotesFilter) {
        this.search = quotesFilter.getSearch();
        this.dtf = quotesFilter.getDtf();
        this.dtt = quotesFilter.getDtt();
        this.status = quotesFilter.getStatus();
    }
}
