package com.example.socketconnection.JsonEncoder;

public class Login {
    final String email;
    final String password;
    final String type;

    public Login(String name, String password, String type) {
        email = name;
        this.password = password;
        this.type = type;
    }
}
