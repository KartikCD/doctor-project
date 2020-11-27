package com.example.socketconnection.JsonEncoder;

public class Patients {
    final String doctorid;
    final String Name;
    final String date_of_birth;
    final String Disease;
    final String healthDescription;
    final String PhoneNumber;
    final String password;
    final String pemail;

    public Patients(String doctorid, String name, String date_of_birth, String disease,
                    String healthDescription, String phoneNumber, String password, String pemail) {
        this.doctorid = doctorid;
        this.Name = name;
        this.date_of_birth = date_of_birth;
        this.Disease = disease;
        this.healthDescription = healthDescription;
        this.PhoneNumber = phoneNumber;
        this.password = password;
        this.pemail = pemail;
    }
}
