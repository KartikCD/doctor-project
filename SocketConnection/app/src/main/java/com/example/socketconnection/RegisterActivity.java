package com.example.socketconnection;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.example.socketconnection.JsonEncoder.Register;
import com.example.socketconnection.Parser.RegisterParserDemo;
import com.example.socketconnection.Services.ApiService;
import com.google.gson.Gson;

import java.util.Calendar;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegisterActivity extends AppCompatActivity {

    EditText txtUsername, txtPassword, txtAadhar, txtLicense, txtEmailAddress;
    TextView txtDob, loginButton;
    Button register;
    String username, password, aadhar, license, dob="", email;
    Calendar c;
    DatePickerDialog datePickerDialog;
    ImageButton datePicker;
    View v;
    String url;

    ApiService apiService;
    AwesomeValidation awesomeValidation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ServerIps serverIps = new ServerIps();
        url = serverIps.getREST_IP();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);
        txtUsername = findViewById(R.id.txtUsername);
        txtPassword = findViewById(R.id.txtPassword);
        txtAadhar = findViewById(R.id.txtAadhar);
        txtLicense = findViewById(R.id.txtLicense);
        txtDob = findViewById(R.id.txtDob);
        txtEmailAddress = findViewById(R.id.txtEmailAddress);
        loginButton = findViewById(R.id.onLogin);
        register = findViewById(R.id.register);
        datePicker = findViewById(R.id.datePicker);
        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
        addValidation();

        datePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                c = Calendar.getInstance();
                int day = c.get(Calendar.DAY_OF_MONTH);
                int month = c.get(Calendar.MONTH);
                int year = c.get(Calendar.YEAR);

                datePickerDialog = new DatePickerDialog(RegisterActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker1, int myear, int mmonth, int mday) {
                        txtDob.setText(mday+"/"+(mmonth + 1)+"/"+myear);
                        dob = mday+"/"+(mmonth + 1)+"/"+myear;
                    }
                }, day, month, year);
                datePickerDialog.show();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(i);
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (awesomeValidation.validate()) {
                    username = txtUsername.getText().toString().trim();
                    license = txtLicense.getText().toString().trim();
                    aadhar = txtAadhar.getText().toString().trim();
                    password = txtPassword.getText().toString().trim();
                    email = txtEmailAddress.getText().toString().trim();
                    Register register = new Register(username,dob,license,aadhar,password, email);

                    Call<RegisterParserDemo> call1 = apiService.registerDoctor(register);
                    call1.enqueue(new Callback<RegisterParserDemo>() {
                        @Override
                        public void onResponse(Call<RegisterParserDemo> call, Response<RegisterParserDemo> response) {
                            String status = response.body().getStatus();
                            Log.d("dataSaved", status);
                            if(status.equals("success"))
                            {
                                AlertDialog.Builder builder1 =
                                        new AlertDialog.Builder(RegisterActivity.this);
                                builder1.setTitle("Message");
                                builder1.setMessage("Doctor registered successfully.");
                                builder1.setCancelable(true);
                                builder1.setNeutralButton(android.R.string.ok,
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                txtUsername.setText("");
                                                txtPassword.setText("");
                                                txtDob.setText("");
                                                txtLicense.setText("");
                                                txtAadhar.setText("");
                                                txtEmailAddress.setText("");
                                                dialogInterface.cancel();
                                            }
                                        });

                                AlertDialog alertDialog = builder1.create();
                                alertDialog.show();
                            }
                            else
                            {
                                AlertDialog.Builder builder1 =
                                        new AlertDialog.Builder(RegisterActivity.this);
                                builder1.setTitle("Message");
                                builder1.setMessage("Something went wrong.");
                                builder1.setCancelable(true);
                                builder1.setNeutralButton(android.R.string.ok,
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.cancel();
                                            }
                                        });
                                AlertDialog alertDialog = builder1.create();
                                alertDialog.show();
                            }
                        }

                        @Override
                        public void onFailure(Call<RegisterParserDemo> call, Throwable t) {
                            Log.d("regFailure",t.toString());
                        }
                    });
                }
            }
        });
    }
    private void addValidation() {
        awesomeValidation.addValidation(txtUsername, "^.{1,50}$", "Username should not be more " +
                "than 50 characters.");
        awesomeValidation.addValidation(txtPassword, "^.{1,50}$", "Please enter " +
                "password.");
        awesomeValidation.addValidation(txtAadhar, "\\d{10}", "Please enter valid " +
                "mobile number");
        awesomeValidation.addValidation(txtLicense, RegexTemplate.NOT_EMPTY, "Please enter " +
                "license number.");
        awesomeValidation.addValidation(txtEmailAddress, Patterns.EMAIL_ADDRESS, "Please enter valid " +
                "email " + "address");
    }
}
