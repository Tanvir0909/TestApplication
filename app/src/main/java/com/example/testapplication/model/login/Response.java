
package com.example;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Response {

    @SerializedName("user")
    @Expose
    private com.example.User user;
    @SerializedName("message")
    @Expose
    private String message;

    public com.example.User getUser() {
        return user;
    }

    public void setUser(com.example.User user) {
        this.user = user;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
