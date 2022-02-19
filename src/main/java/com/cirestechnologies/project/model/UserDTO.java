package com.cirestechnologies.project.model;

// this class is DTO (Data Transfer Object) of the principal class User
public class UserDTO {

    private String username;
    private String password;

    public UserDTO(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "UserDTO{" + "username='" + username + '\'' + ", password='" + password + '\'' + '}';
    }
}
