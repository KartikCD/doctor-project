package com.example.socketconnection.Parser;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PatientParser {
    @SerializedName("pname")
    @Expose
    private String pname;
    @SerializedName("pemail")
    @Expose
    private String pemail;
    @SerializedName("dname")
    @Expose
    private String dname;
    @SerializedName("demail")
    @Expose
    private String demail;
    @SerializedName("message")
    @Expose
    private String message;

    public String getPname() {
        return pname;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }

    public String getPemail() {
        return pemail;
    }

    public String getDname() {
        return dname;
    }

    public void setDname(String dname) {
        this.dname = dname;
    }

    public String getDemail() {
        return demail;
    }

    public void setDemail(String demail) {
        this.demail = demail;
    }

    public void setPemail(String pemail) {
        this.pemail = pemail;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
