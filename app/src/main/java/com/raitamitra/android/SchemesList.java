package com.raitamitra.android;

public class SchemesList {
    private final String scheme;
    private final String link;
    private final String income;
    private final String feed;
    private final String land;

    public SchemesList(String scheme, String link, String income, String feed, String land) {
        this.scheme = scheme;
        this.link = link;
        this.income = income;
        this.feed = feed;
        this.land = land;
    }

    public String getScheme() {
        return scheme;
    }

    public String getLink() {
        return link;
    }

    public String getIncome() {
        return income;
    }

    public String getFeed() {return feed;}

    public String getLand() {return land;}
}
