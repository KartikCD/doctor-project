package com.example.socketconnection.JsonEncoder;

public class AssistantPatientReg {
    final String doctorid;
    final String Name;
    final String date_of_birth;
    final String Disease;
    final String healthDescription;
    final String PhoneNumber;
    final String password;
    final String pemail;
    final String maindoctid;

    public AssistantPatientReg(String doctorid, String name, String date_of_birth, String disease, String healthDescription, String phoneNumber, String password, String pemail, String maindoctid) {
        this.doctorid = doctorid;
        Name = name;
        this.date_of_birth = date_of_birth;
        Disease = disease;
        this.healthDescription = healthDescription;
        PhoneNumber = phoneNumber;
        this.password = password;
        this.pemail = pemail;
        this.maindoctid = maindoctid;
    }
}
