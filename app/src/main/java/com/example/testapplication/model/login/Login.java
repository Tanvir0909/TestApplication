
package com.example;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Login {

    @SerializedName("meta")
    @Expose
    private com.example.Meta meta;
    @SerializedName("response")
    @Expose
    private com.example.Response response;

    public com.example.Meta getMeta() {
        return meta;
    }

    public void setMeta(com.example.Meta meta) {
        this.meta = meta;
    }

    public com.example.Response getResponse() {
        return response;
    }

    public void setResponse(com.example.Response response) {
        this.response = response;
    }

}
