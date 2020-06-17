package com.example.bescientist.Classes;

public class Reviewer {

    private int id;
    private String name;
    private String email;
    private int toReviewNbr;

    //Constructors

    public Reviewer() {
    }

    //Setters

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setToReviewNbr(int toReviewNbr) {
        this.toReviewNbr = toReviewNbr;
    }

    //Getters

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public int getToReviewNbr() {
        return toReviewNbr;
    }
}
