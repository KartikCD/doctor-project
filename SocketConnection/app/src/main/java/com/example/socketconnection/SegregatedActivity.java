package com.example.socketconnection;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.socketconnection.JsonEncoder.Login;
import com.example.socketconnection.Parser.LoginParserDemo;
import com.example.socketconnection.Services.ApiService;
import com.example.socketconnection.Session.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SegregatedActivity extends AppCompatActivity {

    SessionManager sessionManager;
    String type = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_segregated);
        sessionManager = new SessionManager(SegregatedActivity.this);
        ServerIps serverIps = new ServerIps();
        String url = serverIps.getREST_IP();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiService apiService = retrofit.create(ApiService.class);
        Log.i("MyTag", "onCreate: I am here...");

        if (sessionManager.isLoggedIn()) {
            Login login = new Login(sessionManager.getEmail(), sessionManager.getName(),
                    sessionManager.getType());
            Call<LoginParserDemo> call = apiService.loginDoctor(login);
            call.enqueue(new Callback<LoginParserDemo>() {
                @Override
                public void onResponse(Call<LoginParserDemo> call, Response<LoginParserDemo> response) {
                    String status = response.body().getStatus();
                    if (status.equals("SUCCESS")) {
                        String type = sessionManager.getType();
                        Intent i;
                        if(type.equals("Doctor"))
                        {
                            i = new Intent(SegregatedActivity.this, MainActivity.class);
                            startActivity(i);
                        }
                        else if(type.equals("Patients"))
                        {
                            i = new Intent(SegregatedActivity.this, PatientsActivity.class);
                            startActivity(i);
                        }
                        else if(type.equals("assistantDoctor"))
                        {
                            i = new Intent(SegregatedActivity.this,
                                    AssistantAcitivity.class);
                            startActivity(i);
                        }
                    } else  {
                        startActivity(new Intent(SegregatedActivity.this, LoginActivity.class));
                    }
                }

                @Override
                public void onFailure(Call<LoginParserDemo> call, Throwable t) {
                    startActivity(new Intent(SegregatedActivity.this, LoginActivity.class));
                }
            });
        } else  {
            startActivity(new Intent(SegregatedActivity.this, LoginActivity.class));
        }
    }
}
