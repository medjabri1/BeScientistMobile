package com.example.bescientist.Classes;

import java.util.HashMap;
import java.util.Map;

public class Article {

    //Attributes

    private int id;
    private int author_id;
    private String author_name;
    private String title;
    private String content;
    private String domain;
    private String status;
    private String created_at;

    private Map<String, String> realStatus = new HashMap<String, String>();

    //Contructors

    public Article() {
        realStatus.put("A", "Accepté");
        realStatus.put("AJ", "Ajouter dans le volume");
        realStatus.put("R", "Rejeté");
        realStatus.put("C", "Corrigé");
        realStatus.put("T", "à corriger");
        realStatus.put("W", "En attente");
        realStatus.put("RV", "Revue");
        realStatus.put("IR", "En vérification");
    }

    //Setters

    public void setId(int id) {
        this.id = id;
    }
    public void setAuthor_id(int author_id) {
        this.author_id = author_id;
    }
    public void setAuthor_name(String author_name) {
        this.author_name = author_name;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public void setDomain(String domain) {
        this.domain = domain;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    //Getters

    public int getId() {
        return id;
    }
    public int getAuthor_id() {
        return author_id;
    }
    public String getAuthor_name() {
        return author_name;
    }
    public String getTitle() {
        return title;
    }
    public String getContent() {
        return content;
    }
    public String getDomain() {
        return domain;
    }
    public String getStatus() {
        return status;
    }
    public String getCreated_at() {
        return created_at;
    }

    public String getRealStatus() {
        return realStatus.get(status.toUpperCase());
    }

    //Methods
}
