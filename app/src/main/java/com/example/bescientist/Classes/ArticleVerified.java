package com.example.bescientist.Classes;

public class ArticleVerified extends Article {

    //Attributes
    private int reviewer_id;
    private String reviewer_name;
    private String observation;
    private String reviewed_at;

    public ArticleVerified() {
        super();
    }

    //Setters


    public void setReviewer_id(int reviewer_id) {
        this.reviewer_id = reviewer_id;
    }

    public void setReviewer_name(String reviewer_name) {
        this.reviewer_name = reviewer_name;
    }

    public void setObservation(String observation) {
        this.observation = observation;
    }

    public void setReviewed_at(String reviewed_at) {
        this.reviewed_at = reviewed_at;
    }

    //Getters

    public int getReviewer_id() {
        return reviewer_id;
    }

    public String getReviewer_name() {
        return reviewer_name;
    }

    public String getObservation() {
        return observation;
    }

    public String getReviewed_at() {
        return reviewed_at;
    }
}
