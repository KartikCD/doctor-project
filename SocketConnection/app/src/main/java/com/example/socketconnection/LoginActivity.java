package com.example.socketconnection;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.socketconnection.JsonEncoder.Login;
import com.example.socketconnection.Parser.LoginParserDemo;
import com.example.socketconnection.Services.ApiService;
import com.example.socketconnection.Session.SessionManager;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {

    EditText editUsername, editPassword;
    Button btnLogin;
    TextView register;
    String username, password, type="Doctor";
    Spinner spinner;
    String url;

    SessionManager sessionManager;
    ApiService apiService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ServerIps serverIps = new ServerIps();
        url = serverIps.getREST_IP();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);
        Toast.makeText(this, url, Toast.LENGTH_SHORT).show();
        editUsername = findViewById(R.id.txtUsername);
        spinner = findViewById(R.id.txtSpinner);
        List<String> categories = new ArrayList<String>();
        categories.add("Doctor");
        categories.add("assistantDoctor");
        categories.add("Patients");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, categories);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
        editPassword = findViewById(R.id.txtPassword);
        sessionManager = new SessionManager(getApplicationContext());
        btnLogin = findViewById(R.id.checkLogin);
        register = findViewById(R.id.register);
        //sessionManager.createLoginSession("Kartik","kartik","Doctor");
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(LoginActivity.this, "Yet to make layout.", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(i);
            }
        });
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                type = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        editUsername.addTextChangedListener(textWatcher);
        editPassword.addTextChangedListener(textWatcher);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(LoginActivity.this, type, Toast.LENGTH_SHORT).show();
                Toast.makeText(LoginActivity.this, username+"  "+password, Toast.LENGTH_SHORT).show();
                Login login = new Login(username, password, type);

                Call<LoginParserDemo> call = apiService.loginDoctor(login);
                call.enqueue(new Callback<LoginParserDemo>() {
                    @Override
                    public void onResponse(Call<LoginParserDemo> call, Response<LoginParserDemo> response) {
                        String status = response.body().getStatus();
                        if(status.equals("SUCCESS"))
                        {
                            sessionManager.sessionManagerClearLoginSessions();
                            Log.i("MyTag", response.body().getOid() + " \n" + response.body().getUsername() + " \n" + response.body().getPhone());
                            if (type.equals("assistantDoctor")) {
                                sessionManager.createLoginSession(response.body().getUsername().toString(),response.body().getOid().toString(), type, response.body().getDoctid(), password, response.body().getEmail());
                            } else {
                                sessionManager.createLoginSession(response.body().getUsername().toString(),response.body().getOid().toString(), type, "", password, response.body().getEmail());
                            }
                            Toast.makeText(LoginActivity.this, "Login Successfull.", Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(LoginActivity.this, SegregatedActivity.class);
                            startActivity(i);
                            Log.d("onLoggedIn",response.body().getOid().toString());
                        }
                        else if (status.equals("FAIL")) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                            builder.setTitle("Message");
                            builder.setMessage("Please verify your account.");
                            builder.setCancelable(true);
                            builder.setNeutralButton(android.R.string.ok,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    });

                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();
                        }
                        else
                        {
                            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                            builder.setTitle("Message");
                            builder.setMessage("Incorrect Username or password.");
                            builder.setCancelable(true);
                            builder.setNeutralButton(android.R.string.ok,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    });

                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();
                        }
                    }

                    @Override
                    public void onFailure(Call<LoginParserDemo> call, Throwable t) {
                        Log.d("errormc",t.toString());
                    }
                });
            }
        });
    }

    //TextWatcher to check whether the fields are empty or filled.
    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            username = editUsername.getText().toString().trim();
            password = editPassword.getText().toString().trim();
            btnLogin.setEnabled(!username.isEmpty() && !password.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
}