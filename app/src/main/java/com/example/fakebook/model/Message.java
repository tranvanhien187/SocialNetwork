package com.example.fakebook.model;

import java.util.Date;

public class  Message {
    String emailSender;
    String emailReceiver;
    String message;
    String emailFriend;
    Date date;
    Boolean isSeen;
    public Message() {
    }


    public Message(String emailSender, String emailReceiver, String message, String emailFriend, Date date) {
        this.emailSender = emailSender;
        this.emailReceiver = emailReceiver;
        this.message = message;
        this.emailFriend = emailFriend;
        this.date = date;
        this.isSeen=false;
    }

    public Boolean isSeen() {
        return isSeen;
    }

    public void setSeen(Boolean seen) {
        isSeen = seen;
    }

    public String getEmailFriend() {
        return emailFriend;
    }

    public void setEmailFriend(String emailFriend) {
        this.emailFriend = emailFriend;
    }

    public String getEmailSender() {
        return emailSender;
    }

    public void setEmailSender(String emailSender) {
        this.emailSender = emailSender;
    }

    public String getEmailReceiver() {
        return emailReceiver;
    }

    public void setEmailReceiver(String emailReceiver) {
        this.emailReceiver = emailReceiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }


}