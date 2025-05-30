package com.example.lab2.model;

import com.google.gson.annotations.SerializedName;
import java.util.Date;
public class User {
    @SerializedName("id")
    private int id;
    @SerializedName("email")
    private String email;
    @SerializedName("pass")
    private String pass;
    @SerializedName("username")
    private String username;
    @SerializedName("mobile")
    private String mobile;
    @SerializedName("name")
    private String name;
    // ánh xạ đúng với cột avatar_url trong DB / JSON trả về
    @SerializedName("avatar_url")
    private String avatarUrl;
    @SerializedName("gender")
    private String gender;
    @SerializedName("birthdate")
    private Date birthdate;
    // getters/setters...
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPass() { return pass; }
    public void setPass(String pass) { this.pass = pass; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public Date getBirthdate() { return birthdate; }
    public void setBirthdate(Date birthdate) { this.birthdate = birthdate; }
}
