package com.example.socketconnection.Parser;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AssistantDoctor1 {
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("Name")
    @Expose
    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
