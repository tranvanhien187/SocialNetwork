package com.example.fakebook.model;

import java.util.Date;

public class AddressPostNewFeed {
    String emailPost;
    String filePath;
    Date time;


    public AddressPostNewFeed() {
    }

    public String getEmailPost() {
        return emailPost;
    }

    public void setEmailPost(String emailPost) {
        this.emailPost = emailPost;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public AddressPostNewFeed(String emailPost, String filePath, Date time) {
        this.emailPost = emailPost;
        this.filePath = filePath;
        this.time = time;
    }
}
