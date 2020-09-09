package com.foodies.app.Models;

public class Users {
    private String email,password,username,mobile,uid,Address,profile_picture;
    private long time;


    public Users(String email, String password, String username, String mobile, String uid, String address, long time,String profile_picture) {
        this.email = email;
        this.password = password;
        this.username = username;
        this.mobile = mobile;
        this.uid = uid;
        Address = address;
        this.time = time;
        this.profile_picture = profile_picture;
    }

    public Users() {
    }

    public String getProfile_picture() {
        return profile_picture;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
