package com.example.socketconnection;

public class ServerIps {
    private String REST_IP = "http://192.168.1.105:4000/apiv1/medical/";
    private String CLIENT_IP = "192.168.1.101";
    private String GRAPH_IP = "192.168.1.105";

    public String getREST_IP() {
        return REST_IP;
    }

    public String getCLIENT_IP() {
        return CLIENT_IP;
    }

    public String getGRAPH_IP() {
        return GRAPH_IP;
    }
}