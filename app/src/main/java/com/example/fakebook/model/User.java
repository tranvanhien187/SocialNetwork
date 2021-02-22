package com.example.fakebook.model;

import java.util.ArrayList;
import java.util.HashMap;

public class User {
    private String avatar;
    private String name;
    private String email;
    private String dateOfBirth;
    private String city;
    private String education;
    private Boolean isMarriage;
    private Boolean isMale;
    private Boolean isOnline;
    private String lowercaseName;
    private ArrayList<String> friendList=new ArrayList<>();
    private HashMap<String, Integer> sentimentalRatings = new HashMap<>();  // key : uId , value : points
    public User() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public User(String avatar, String name, String email, String dateOfBirth, String city, String education, Boolean isMarriage, Boolean isMale) {
        this.avatar = avatar;
        this.name = name;
        this.email = email;
        this.dateOfBirth = dateOfBirth;
        this.city = city;
        this.education = education;
        this.isMarriage = isMarriage;
        this.isMale = isMale;
        this.lowercaseName = name.toLowerCase();
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public Boolean getIsMarriage() {
        return isMarriage;
    }

    public void setIsMarriage(Boolean single) {
        isMarriage = single;
    }

    public String getLowercaseName() {
        return lowercaseName;
    }

    public void setLowercaseName(String lowercaseName) {
        this.lowercaseName = lowercaseName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Boolean getMale() {
        return isMale;
    }

    public void setMale(Boolean male) {
        isMale = male;
    }


    public ArrayList<String> getFriendList() {
        return friendList;
    }

    public void setFriendList(ArrayList<String> friendList) {
        this.friendList = friendList;
    }

    public HashMap<String, Integer> getSentimentalRatings() {
        return sentimentalRatings;
    }

    public void setSentimentalRatings(HashMap<String, Integer> sentimentalRatings) {
        this.sentimentalRatings = sentimentalRatings;
    }

    public Boolean getOnline() {
        return isOnline;
    }

    public void setOnline(Boolean online) {
        isOnline = online;
    }
}
