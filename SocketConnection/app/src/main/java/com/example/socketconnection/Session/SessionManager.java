package com.example.socketconnection.Session;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.socketconnection.AssistantAcitivity;
import com.example.socketconnection.JsonEncoder.Login;
import com.example.socketconnection.LoginActivity;
import com.example.socketconnection.MainActivity;
import com.example.socketconnection.Parser.LoginParserDemo;
import com.example.socketconnection.PatientsActivity;
import com.example.socketconnection.SegregatedActivity;
import com.example.socketconnection.ServerIps;
import com.example.socketconnection.Services.ApiService;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SessionManager {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Context context;

    private static final String IS_LOGIN = "IsLoggedIn";

    public static final String MYPREFERENCES = "DOCTORPREFERENCE";
    public static final String USERNAME = "loginUsername";
    public static final String OID = "loginPassword";
    public static final String TYPE = "loginType";
    public static final String DOCTID = "loginDoctorId";
    public static final String DOCTPASSWORD = "loginDoctorPassword";
    public static final String DOCTEMAIL = "loginDoctorEmail";

    public SessionManager(Context context)
    {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(MYPREFERENCES,Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void createLoginSession(String username, String oid, String type, String doctid,
                                   String password, String email)
    {
        editor.putBoolean(IS_LOGIN,true);
        editor.putString(USERNAME, username);
        editor.putString(OID,oid);
        editor.putString(TYPE,type);
        editor.putString(DOCTID, doctid);
        editor.putString(DOCTPASSWORD,password);
        editor.putString(DOCTEMAIL, email);
        editor.commit();
    }

    public void checkLogin()
    {
        if(this.isLoggedIn() == true)
        {
            ServerIps serverIps = new ServerIps();
            String url = serverIps.getREST_IP();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            ApiService apiService = retrofit.create(ApiService.class);
            Login login = new Login(getUsername(), getName(), getType());
            Call<LoginParserDemo> call = apiService.loginDoctor(login);
            call.enqueue(new Callback<LoginParserDemo>() {
                @Override
                public void onResponse(Call<LoginParserDemo> call, Response<LoginParserDemo> response) {
                    String status = response.body().getStatus();
                    if (status.equals("SUCCESS")) {
                        String type = getType();
                        if(type.equals("Doctor"))
                        {
                            Intent i = new Intent(context, MainActivity.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(i);
                        }
                        else if(type.equals("Patients"))
                        {
                            Log.i("SEGREGATED_ACTIVITY", "onCreate: I am here...");
                            Intent i1 = new Intent(context, PatientsActivity.class);
                            i1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            i1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(i1);
//                    Toast.makeText(this, "Patients is going to be opened", Toast.LENGTH_SHORT).show();
                        }
                        else if(type.equals("assistantDoctor"))
                        {
                            Intent i2 = new Intent(context,
                                    AssistantAcitivity.class);
                            i2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            i2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(i2);
//                    Toast.makeText(this, "Assistant doctor is going to be opened.", Toast.LENGTH_SHORT).show();
                        }
                    } else  {
                        Intent i3 = new Intent(context, LoginActivity.class);
                        i3.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        i3.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(i3);
                    }
                }

                @Override
                public void onFailure(Call<LoginParserDemo> call, Throwable t) {
                    Intent i4 = new Intent(context, LoginActivity.class);
                    i4.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    i4.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(i4);
                }
            });
        } else  {
            Intent i5 = new Intent(context, LoginActivity.class);
            i5.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i5.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i5);
        }
    }

    public String getDoctid()
    {
        String oid = sharedPreferences.getString(DOCTID,null);
        return  oid;
    }

    public String getUsername()
    {
        String username = sharedPreferences.getString(USERNAME,null);
        return  username;
    }

    public String getOID()
    {
        String oid = sharedPreferences.getString(OID,null);
        return  oid;
    }

    public void logoutUser()
    {
        editor.clear();
        editor.commit();

        Intent i = new Intent(context, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }

    public String getName() {
        return sharedPreferences.getString(DOCTPASSWORD, null);
    }

    public String getEmail() {
        return sharedPreferences.getString(DOCTEMAIL, null);
    }

    public boolean isLoggedIn()
    {
        return sharedPreferences.getBoolean(IS_LOGIN, false);
    }

    public String getType()
    {
        String type = sharedPreferences.getString(TYPE, null);
        return type;
    }

    public void sessionManagerClearLoginSessions()
    {
        editor.clear();
        editor.commit();
    }
}
