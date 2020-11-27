package com.example.socketconnection.JsonEncoder;

public class Register {
    final String Name;
    final String date_of_birth;
    final String doctor_license_number;
    final String PhoneNumber;
    final String password;
    final String email;

    public Register(String name, String date_of_birth, String doctor_license_number,
                    String phoneNumber, String password, String email) {
        Name = name;
        this.date_of_birth = date_of_birth;
        this.doctor_license_number = doctor_license_number;
        PhoneNumber = phoneNumber;
        this.password = password;
        this.email = email;
    }
}
