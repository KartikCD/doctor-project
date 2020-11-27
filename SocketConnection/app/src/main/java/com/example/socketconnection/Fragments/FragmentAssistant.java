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
import com.example.socketconnection.JsonEncoder.AssistantDoctor;
import com.example.socketconnection.Parser.RegisterParserDemo;
import com.example.socketconnection.R;
import com.example.socketconnection.RegisterActivity;
import com.example.socketconnection.ServerIps;
import com.example.socketconnection.Services.ApiService;
import com.example.socketconnection.Session.SessionManager;

import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FragmentAssistant extends Fragment {

    View v;
    EditText txtUsername, txtPassword, txtAadhar, txtLicense, txtEmailAddress;
    TextView txtDob;
    Button register;
    String username, password, aadhar, license, dob="", email;
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
        v = inflater.inflate(R.layout.fragment_assistant,container,false);
        ServerIps serverIps = new ServerIps();
        url = serverIps.getREST_IP();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);
        txtUsername = v.findViewById(R.id.txtUsername);
        txtPassword = v.findViewById(R.id.txtPassword);
        txtAadhar = v.findViewById(R.id.txtAadhar);
        txtLicense = v.findViewById(R.id.txtLicense);
        txtEmailAddress = v.findViewById(R.id.txtEmailAddress);
        txtDob = v.findViewById(R.id.txtDob);
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
                    username = txtUsername.getText().toString().trim();
                    license = txtLicense.getText().toString().trim();
                    aadhar = txtAadhar.getText().toString().trim();
                    password = txtPassword.getText().toString().trim();
                    email = txtEmailAddress.getText().toString().trim();
                    AssistantDoctor assistantDoctor =
                            new AssistantDoctor(sessionManager.getOID().toString(),username,dob,
                                    license,aadhar,password, email);

                    Call<RegisterParserDemo> call = apiService.registerAssistantDoctor(assistantDoctor);
                    call.enqueue(new Callback<RegisterParserDemo>() {
                        @Override
                        public void onResponse(Call<RegisterParserDemo> call, Response<RegisterParserDemo> response) {
                            String status = response.body().getStatus();
                            Log.d("dataSaved", status);
                            if(status.equals("success"))
                            {
                                AlertDialog.Builder builder1 =
                                        new AlertDialog.Builder(getContext());
                                builder1.setTitle("Message");
                                builder1.setMessage("Assistant Doctor registered successfully.");
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
                            else if (status.equals("fail")) {
                                AlertDialog.Builder builder1 =
                                        new AlertDialog.Builder(getContext());
                                builder1.setTitle("Message");
                                builder1.setMessage("Doctor Already Exists.");
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
                            Log.d("regFailure",t.toString());
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
        awesomeValidation.addValidation(txtAadhar, "\\d{10}", "Please enter valid " +
                "mobile number");
        awesomeValidation.addValidation(txtLicense, RegexTemplate.NOT_EMPTY, "Please enter " +
                "license number.");
        awesomeValidation.addValidation(txtEmailAddress, Patterns.EMAIL_ADDRESS, "Please enter valid " +
                "email " + "address");
    }
}
