package com.example.fakebook.model;

import java.util.ArrayList;
import java.util.Date;

public class Comment {
    private String content;
    private Date time;
    private String emailUser;

    public Comment(String content, Date time, String emailUser) {
        this.content = content;
        this.time = time;
        this.emailUser = emailUser;
    }

    public Comment() {
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

    public String getEmailUser() {
        return emailUser;
    }

    public void setEmailUser(String emailUser) {
        this.emailUser = emailUser;
    }
}
