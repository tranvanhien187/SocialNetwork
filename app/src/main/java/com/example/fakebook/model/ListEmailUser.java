package com.example.fakebook.model;

import java.util.ArrayList;

public class ListEmailUser {
    ArrayList<String> email;

    public ListEmailUser() {
    }

    public ListEmailUser(ArrayList<String> email) {
        this.email = email;
    }

    public ArrayList<String> getEmail() {
        return email;
    }

    public void setEmail(ArrayList<String> email) {
        this.email = email;
    }
}
