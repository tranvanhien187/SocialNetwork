package com.example.fakebook.model;

import java.util.Date;

public class Notification {
    String email;
    String content;
    String filePath;
    Date time;

    public Notification() {
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Notification(String email, String content, String filePath, Date time) {
        this.email = email;
        this.content = content;
        this.filePath = filePath;
        this.time = time;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}