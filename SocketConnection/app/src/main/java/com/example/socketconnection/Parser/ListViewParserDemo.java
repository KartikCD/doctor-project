package com.example.socketconnection.Parser;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ListViewParserDemo {

    @SerializedName("Doctors")
    @Expose
    private ArrayList<Doctor> doctors = null;
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("Name")
    @Expose
    private String name;
    @SerializedName("Patients")
    @Expose
    private ArrayList<Patients> patients = null;
    @SerializedName("assistantDoctor")
    @Expose
    private ArrayList<AssistantDoctor1> assistantDoctors = null;

    public ArrayList<AssistantDoctor1> getAssistantDoctors() {
        return assistantDoctors;
    }

    public void setAssistantDoctors(ArrayList<AssistantDoctor1> assistantDoctors) {
        this.assistantDoctors = assistantDoctors;
    }

    public ArrayList<Patients> getPatients() {
        return patients;
    }

    public void setPatients(ArrayList<Patients> patients) {
        this.patients = patients;
    }

    public ArrayList<Doctor> getDoctors() {
        return doctors;
    }

    public void setDoctors(ArrayList<Doctor> doctors) {
        this.doctors = doctors;
    }

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
