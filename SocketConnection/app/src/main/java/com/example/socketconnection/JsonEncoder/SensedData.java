package com.example.socketconnection.JsonEncoder;

public class SensedData {
    private String ecg;
    private String pulse;
    private String temperature;
    private String humidity;
    private String timestp;
    private String name;

    public SensedData(String ecg, String pulse, String temperature, String humidity,
                      String timestp, String name) {
        this.ecg = ecg;
        this.pulse = pulse;
        this.temperature = temperature;
        this.humidity = humidity;
        this.timestp = timestp;
        this.name = name;
    }
}
