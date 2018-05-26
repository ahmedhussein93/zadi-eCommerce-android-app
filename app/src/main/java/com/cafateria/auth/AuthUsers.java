package com.cafateria.auth;


import com.cafateria.model.Disease;
import com.google.gson.Gson;

import java.util.List;


public class AuthUsers {


    public static final int STUDENT = 0;
    public static final int STUFF = 1;
    public static final int ADMIN = 2;

    public static final int ACCEPTED = 1;
    public static final int REJECTED = -1;

    private int id;
    private String first_name;
    private String last_name;
    private String password;
    private String email;
    private int user_type, state;
    private List<Disease> diseases;

    public AuthUsers() {
    }

    public AuthUsers(int id, String first_name, String last_name, String password, String email, int user_type, int state, List<Disease> diseases) {
        this.id = id;
        this.first_name = first_name;
        this.last_name = last_name;
        this.password = password;
        this.email = email;
        this.user_type = user_type;
        this.diseases = diseases;
        this.state = state;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name == null ? " " : last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getPassword() {
        return password == null ? " " : password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    //############## parser methods ##############

    public String getEmail() {
        return email == null ? "" : email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getUser_type() {
        return user_type;
    }

    public void setUser_type(int user_type) {
        this.user_type = user_type;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this, AuthUsers.class);
    }

    public List<Disease> getDiseases() {
        return diseases;
    }

    public void setDiseases(List<Disease> diseases) {
        this.diseases = diseases;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}
