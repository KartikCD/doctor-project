package com.example.socketconnection.JsonEncoder;

public class Login {
    final String Name;
    final String password;
    final String type;

    public Login(String name, String password, String type) {
        Name = name;
        this.password = password;
        this.type = type;
    }
}
