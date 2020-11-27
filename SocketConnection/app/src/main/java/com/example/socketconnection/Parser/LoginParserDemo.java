package com.example.socketconnection.Parser;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LoginParserDemo {
    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("id")
    @Expose
    private String oid;

    @SerializedName("name")
    @Expose
    private String username;

    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("phone")
    @Expose
    private String phone;

    @SerializedName("doctid")
    @Expose
    private String doctid;

    @SerializedName("email")
    @Expose
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDoctid() {
        return doctid;
    }

    public void setDoctid(String doctid) {
        this.doctid = doctid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
