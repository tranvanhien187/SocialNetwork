package com.example.fakebook.model;

import java.util.ArrayList;
import java.util.Date;

public class Post {
    private String emailUser,content,image,filePath;
    private Date time;
    private ArrayList<String> listLike;


    public ArrayList<String> getListLike() {
        return listLike;
    }

    public void setListLike(ArrayList<String> listLike) {
        this.listLike = listLike;
    }

    public String getEmailUser() {
        return emailUser;
    }

    public void setEmailUser(String emailUser) {
        this.emailUser = emailUser;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }


    public String getImage() {
        return image;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Post(String emailUser, String content, String image, String filePath, Date time) {
        this.emailUser = emailUser;
        this.content = content;
        this.image = image;
        this.filePath = filePath;
        this.time = time;
        this.listLike=new ArrayList<>();
    }

    public Post() {
    }

    public void deleteLikeUser(String emailCurrenUser){
        this.listLike.remove(emailCurrenUser);
    }
    public void addLikeUser(String emailCurrenUser){
        this.listLike.add(emailCurrenUser);
    }

}
