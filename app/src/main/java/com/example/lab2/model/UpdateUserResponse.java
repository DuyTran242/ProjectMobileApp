package com.example.lab2.model;

import com.google.gson.annotations.SerializedName;

public class UpdateUserResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    @SerializedName("url")
    private String url;

    @SerializedName("user")
    private User user;

    public boolean isSuccess() { return success; }
    public String getMessage()  { return message; }
    public String getUrl()      { return url; }
    public User getUser()       { return user; }

    public void setSuccess(boolean success) { this.success = success; }
    public void setMessage(String message)  { this.message = message; }
    public void setUrl(String url)          { this.url = url; }
    public void setUser(User user)          { this.user = user; }
}
