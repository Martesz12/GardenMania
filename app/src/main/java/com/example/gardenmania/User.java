package com.example.gardenmania;

public class User {
    private String username;
    private String password;
    private String phone;
    private String email;
    private String picture;
    private String id;

    public User(String username, String password, String phone, String email, String picture) {
        this.username = username;
        this.password = password;
        this.phone = phone;
        this.email = email;
        this.picture = picture;
    }

    public User() {}

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getPicture() {
        return picture;
    }

    public String _getId(){
        return id;
    }

    public void setId(String id){
        this.id = id;
    }
}
