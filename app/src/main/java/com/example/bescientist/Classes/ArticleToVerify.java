package com.example.bescientist.Classes;

public class ArticleToVerify extends Article {

    //Attributes
    private int reviewer_id;
    private String reviewer_name;
    private String sent_at;

    public ArticleToVerify() {
        super();
    }

    //Setters


    public void setReviewer_id(int reviewer_id) {
        this.reviewer_id = reviewer_id;
    }

    public void setReviewer_name(String reviewer_name) {
        this.reviewer_name = reviewer_name;
    }

    public void setSent_at(String sent_at) {
        this.sent_at = sent_at;
    }

    //Getters


    public int getReviewer_id() {
        return reviewer_id;
    }

    public String getReviewer_name() {
        return reviewer_name;
    }

    public String getSent_at() {
        return sent_at;
    }
}
