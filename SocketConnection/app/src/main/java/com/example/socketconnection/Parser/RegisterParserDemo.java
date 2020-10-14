package com.example.socketconnection.Parser;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RegisterParserDemo {
    @SerializedName("datasaved")
    @Expose
    private String datasaved;
    @SerializedName("status")
    @Expose
    private String status;

    public String getDatasaved() {
        return datasaved;
    }

    public void setDatasaved(String datasaved) {
        this.datasaved = datasaved;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
