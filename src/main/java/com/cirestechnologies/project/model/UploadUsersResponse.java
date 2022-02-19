package com.cirestechnologies.project.model;

import java.util.ArrayList;
import java.util.List;

public class UploadUsersResponse {

    private int numberOfUsersSaved = 0;
    private List<User> userSaved = new ArrayList<>();

    private int numberOfUsersNotSaved = 0;
    private List<User> userNotSaved = new ArrayList<>();

    public UploadUsersResponse() {
    }

    public int getNumberOfUsersSaved() {
        return numberOfUsersSaved;
    }

    public void setNumberOfUsersSaved(int numberOfUsersSaved) {
        this.numberOfUsersSaved = numberOfUsersSaved;
    }

    public int getNumberOfUsersNotSaved() {
        return numberOfUsersNotSaved;
    }

    public void setNumberOfUsersNotSaved(int numberOfUsersNotSaved) {
        this.numberOfUsersNotSaved = numberOfUsersNotSaved;
    }

    public List<User> getUserSaved() {
        return userSaved;
    }

    public void setUserSaved(List<User> userSaved) {
        this.userSaved = userSaved;
    }

    public List<User> getUserNotSaved() {
        return userNotSaved;
    }

    public void setUserNotSaved(List<User> userNotSaved) {
        this.userNotSaved = userNotSaved;
    }
}
