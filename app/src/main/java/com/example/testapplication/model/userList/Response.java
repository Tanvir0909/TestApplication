
package com.example.testapplication.model.userList;

import java.util.List;

import com.example.User;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Response {

    @SerializedName("user_list")
    @Expose
    private List<com.example.User> userList = null;
    @SerializedName("message")
    @Expose
    private String message;

    public List<User> getUserList() {
        return userList;
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
