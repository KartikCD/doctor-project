package com.example.socketconnection.Fragments;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.example.socketconnection.JsonEncoder.AssistantPatientReg;
import com.example.socketconnection.JsonEncoder.Patients;
import com.example.socketconnection.Parser.RegisterParserDemo;
import com.example.socketconnection.R;
import com.example.socketconnection.ServerIps;
import com.example.socketconnection.Services.ApiService;
import com.example.socketconnection.Session.SessionManager;

import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FragmentPatients extends Fragment{

    View v;
    EditText txtUsername, txtPassword, txtPhone, txtDisease, txtHealth, txtPEmail;
    TextView txtDob;
    Button register;
    String name, password, phone, disease, health, dob="", pemail;
    Calendar c;
    DatePickerDialog datePickerDialog;
    ImageButton datePicker;
    SessionManager sessionManager;
    String url;

    ApiService apiService;
    AwesomeValidation awesomeValidation;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_patients,container,false);
        ServerIps serverIps = new ServerIps();
        url = serverIps.getREST_IP();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);
        txtUsername = v.findViewById(R.id.txtUsername);
        txtPassword = v.findViewById(R.id.txtPassword);
        txtPhone = v.findViewById(R.id.txtAadhar);
        txtDisease = v.findViewById(R.id.txtLicense);
        txtHealth = v.findViewById(R.id.healthDescription);
        txtDob = v.findViewById(R.id.txtDob);
        txtPEmail = v.findViewById(R.id.txtPEmail);
        register = v.findViewById(R.id.register);
        datePicker = v.findViewById(R.id.datePicker);
        sessionManager = new SessionManager(getContext());
        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
        addValidation();

        datePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                c = Calendar.getInstance();
                int day = c.get(Calendar.DAY_OF_MONTH);
                int month = c.get(Calendar.MONTH);
                int year = c.get(Calendar.YEAR);

                datePickerDialog = new DatePickerDialog(getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker1, int myear, int mmonth, int mday) {
                                txtDob.setText(mday+"/"+(mmonth + 1)+"/"+myear);
                                dob = mday+"/"+(mmonth + 1)+"/"+myear;
                            }
                        }, day, month, year);
                datePickerDialog.show();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (awesomeValidation.validate()) {
                    name = txtUsername.getText().toString().trim();
                    health = txtHealth.getText().toString().trim();
                    phone = txtPhone.getText().toString().trim();
                    password = txtPassword.getText().toString().trim();
                    pemail = txtPEmail.getText().toString().trim();
                    disease = txtDisease.getText().toString().trim();
                    Call<RegisterParserDemo> call = null;
                    if (sessionManager.getType().equals("assistantDoctor")) {
                        AssistantPatientReg patients =
                                new AssistantPatientReg(sessionManager.getOID(),name,dob,disease,
                                        health, phone,password, pemail, sessionManager.getDoctid());
                        call = apiService.registerPatientAstDoctor(patients);
                    } else if (sessionManager.getType().equals("Doctor")) {
                        Patients patients = new Patients(sessionManager.getOID(),name,dob,disease,health,
                                phone,password, pemail);
                        call = apiService.registerPatient(patients);
                    }
                    final Call<RegisterParserDemo> call1 = call;
//                    Call<RegisterParserDemo> call = apiService.registerPatient(patients);
                    call1.enqueue(new Callback<RegisterParserDemo>() {
                        @Override
                        public void onResponse(Call<RegisterParserDemo> call, Response<RegisterParserDemo> response) {
                            String status = response.body().getStatus();
                            Log.d("dataSaved", status);
                            if(status.equals("success"))
                            {
                                AlertDialog.Builder builder1 =
                                        new AlertDialog.Builder(getContext());
                                builder1.setTitle("Message");
                                builder1.setMessage("Patient registered successfully.");
                                builder1.setCancelable(true);
                                builder1.setNeutralButton(android.R.string.ok,
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                txtUsername.setText("");
                                                txtPassword.setText("");
                                                txtDob.setText("");
                                                txtPhone.setText("");
                                                txtDisease.setText("");
                                                txtHealth.setText("");
                                                txtPEmail.setText("");
                                                dialogInterface.cancel();
                                            }
                                        });

                                AlertDialog alertDialog = builder1.create();
                                alertDialog.show();
                            }
                            else
                            {
                                AlertDialog.Builder builder1 =
                                        new AlertDialog.Builder(getContext());
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
                            Log.d("regFailure", t.toString());
                        }
                    });
                }
            }
        });

        return v;
    }
    private void addValidation() {
        awesomeValidation.addValidation(txtUsername, "^.{1,50}$", "Username should not be more " +
                "than 50 characters.");
        awesomeValidation.addValidation(txtPassword, "^.{1,50}$", "Please enter " +
                "password.");
        awesomeValidation.addValidation(txtPhone, "\\d{10}", "Please enter valid " +
                "mobile number");
        awesomeValidation.addValidation(txtDisease, RegexTemplate.NOT_EMPTY, "Please enter " +
                "disease");
        awesomeValidation.addValidation(txtHealth, RegexTemplate.NOT_EMPTY, "Please enter health " +
                "description.");
        awesomeValidation.addValidation(txtPEmail, Patterns.EMAIL_ADDRESS, "Please enter valid " +
                "email " + "address");
    }
}
