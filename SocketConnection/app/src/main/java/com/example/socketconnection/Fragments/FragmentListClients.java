package com.example.socketconnection.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socketconnection.Adapters.AssistantDoctorAdapter;
import com.example.socketconnection.Adapters.DoctorAdapter;
import com.example.socketconnection.Adapters.PatientsAdapter;
import com.example.socketconnection.JsonEncoder.Contacts;
import com.example.socketconnection.OpTapListener.OnTapListener;
import com.example.socketconnection.Parser.AssistantDoctor1;
import com.example.socketconnection.Parser.Doctor;
import com.example.socketconnection.Parser.ListViewParserDemo;
import com.example.socketconnection.Parser.Patients;
import com.example.socketconnection.R;
import com.example.socketconnection.ServerIps;
import com.example.socketconnection.Services.ApiService;
import com.example.socketconnection.Session.SessionManager;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FragmentListClients extends Fragment {
    View v;
    RecyclerView doctorRecyclerview, patientsRecyclerview;
    TextView textDoctor, textPatients;
    Retrofit retrofit;
    ApiService apiService;
    DoctorAdapter doctorAdapter;
    PatientsAdapter patientsAdapter;
    AssistantDoctorAdapter assistantDoctorAdapter;
    Fragment fragment;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_listclients, container, false);
        doctorRecyclerview = v.findViewById(R.id.doctorRecyclerview);
        patientsRecyclerview = v.findViewById(R.id.patientsRecyclerview);
        textDoctor = v.findViewById(R.id.inputDoctor);
        textPatients = v.findViewById(R.id.inputPatients);
        Toast.makeText(getContext(), "heyy", Toast.LENGTH_SHORT).show();
        ServerIps serverIps = new ServerIps();
        String url = serverIps.getREST_IP();
        retrofit =
                new Retrofit.Builder().baseUrl(url).addConverterFactory(GsonConverterFactory.create()).build();
        apiService = retrofit.create(ApiService.class);

        SessionManager sessionManager = new SessionManager(getContext());
        String oid = sessionManager.getOID();
        final String type = sessionManager.getType();
        Toast.makeText(getContext(), oid.toString(), Toast.LENGTH_SHORT).show();
        Contacts contacts = new Contacts(oid, type);
        Call<ListViewParserDemo> call = apiService.getContacts(contacts);
        call.enqueue(new Callback<ListViewParserDemo>() {
            @Override
            public void onResponse(Call<ListViewParserDemo> call, Response<ListViewParserDemo> response) {
                Log.i("resp", response.body().toString());
                if(type.equals("Doctor"))
                {
                    if(response.code() == 400)
                    {
                        Toast.makeText(getContext(), "No contacts found", Toast.LENGTH_SHORT).show();
                    }
                    else if(response.code() == 200)
                    {
                        ArrayList<AssistantDoctor1> doctors = new ArrayList<AssistantDoctor1>();
                        doctors = response.body().getAssistantDoctors();
                        ArrayList<com.example.socketconnection.Parser.Patients> patients =
                                new ArrayList<Patients>();
                        patients = response.body().getPatients();
                        if(doctors.size() > 0)
                        {
                            textDoctor.setText("List of Assistant Doctors");
                            textDoctor.setVisibility(View.VISIBLE);
                            doctorRecyclerview.setVisibility(View.VISIBLE);
                            assistantDoctorAdapter = new AssistantDoctorAdapter(getContext(), doctors);
                            assistantDoctorAdapter.setOnTapListener(new OnTapListener() {
                                @Override
                                public void onTapView(String name) {
                                    fragment = new FragmentChats();
                                    Bundle bundle = new Bundle();
                                    bundle.putString("NAME", name);
                                    fragment.setArguments(bundle);
                                    FragmentManager fragmentManager = getFragmentManager();
                                    fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit();
                                }
                            });
                            assistantDoctorAdapter.setOnTapListener(new OnTapListener() {
                                @Override
                                public void onTapView(String name) {
                                    fragment = new FragmentChats();
                                    Bundle bundle = new Bundle();
                                    bundle.putString("NAME", name);
                                    fragment.setArguments(bundle);
                                    FragmentManager fragmentManager = getFragmentManager();
                                    fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit();
                                }
                            });
                            Log.i("doctorsss", response.body().getAssistantDoctors().toString());
                            doctorRecyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
                            doctorRecyclerview.setAdapter(assistantDoctorAdapter);
                        } else  {
                            textDoctor.setText("No assistant doctor found.");
                            textDoctor.setVisibility(View.VISIBLE);
                        }
                        if(patients.size() > 0)
                        {
                            textPatients.setText("List of Patients");
                            textPatients.setVisibility(View.VISIBLE);
                            patientsRecyclerview.setVisibility(View.VISIBLE);
                            patientsAdapter = new PatientsAdapter(getContext(), patients);
                            patientsAdapter.setOnTapListener(new OnTapListener() {
                                @Override
                                public void onTapView(String name) {
                                    fragment = new FragmentChats();
                                    Bundle bundle = new Bundle();
                                    bundle.putString("NAME", name);
                                    fragment.setArguments(bundle);
                                    FragmentManager fragmentManager = getFragmentManager();
                                    fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit();
                                }
                            });
                            patientsRecyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
                            patientsRecyclerview.setAdapter(patientsAdapter);
                        } else {
                            textPatients.setText("No patients found.");
                            textPatients.setVisibility(View.VISIBLE);
                        }
                    }
                }
                else if(type.equals("Patients"))
                {
                    if(response.code() == 400)
                    {
                        Toast.makeText(getContext(), "No contacts found", Toast.LENGTH_SHORT).show();
                    }
                    else if(response.code() == 200)
                    {
                        ArrayList<Doctor> doctors = new ArrayList<Doctor>();
                        ArrayList<AssistantDoctor1> assistantDoctors1 =
                                new ArrayList<AssistantDoctor1>();
                        doctors = response.body().getDoctors();
                        assistantDoctors1 = response.body().getAssistantDoctors();
                        if(doctors.size() > 0)
                        {
                            textDoctor.setText("List of Doctors");
                            textDoctor.setVisibility(View.VISIBLE);
                            doctorRecyclerview.setVisibility(View.VISIBLE);
                            doctorAdapter = new DoctorAdapter(getContext(), doctors);
                            doctorAdapter.setOnTapListener(new OnTapListener() {
                                @Override
                                public void onTapView(String name) {
                                    fragment = new FragmentClientChat();
                                    Bundle bundle = new Bundle();
                                    bundle.putString("NAME", name);
                                    fragment.setArguments(bundle);
                                    FragmentManager fragmentManager = getFragmentManager();
                                    fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit();
                                }
                            });
                            doctorRecyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
                            doctorRecyclerview.setAdapter(doctorAdapter);
                        } else {
                            textDoctor.setText("No doctor found.");
                            textDoctor.setVisibility(View.VISIBLE);
                        }
                        if (assistantDoctors1.size() > 0) {
                            textPatients.setText("List of Assistant Doctors");
                            textPatients.setVisibility(View.VISIBLE);
                            patientsRecyclerview.setVisibility(View.VISIBLE);
                            assistantDoctorAdapter = new AssistantDoctorAdapter(getContext(), assistantDoctors1);
                            assistantDoctorAdapter.setOnTapListener(new OnTapListener() {
                                @Override
                                public void onTapView(String name) {
                                    fragment = new FragmentClientChat();
                                    Bundle bundle = new Bundle();
                                    bundle.putString("NAME", name);
                                    fragment.setArguments(bundle);
                                    FragmentManager fragmentManager = getFragmentManager();
                                    fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit();
                                }
                            });
                            patientsRecyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
                            patientsRecyclerview.setAdapter(assistantDoctorAdapter);
                        } else {
                            textPatients.setText("No assistant doctor found.");
                            textPatients.setVisibility(View.VISIBLE);
                        }
                    }
                }
                else if(type.equals("assistantDoctor"))
                {
                    if(response.code() == 400)
                    {
                        Toast.makeText(getContext(), "No contacts found", Toast.LENGTH_SHORT).show();
                    }
                    else if(response.code() == 200)
                    {
                        ArrayList<Doctor> doctors = new ArrayList<Doctor>();
                        doctors = response.body().getDoctors();
                        ArrayList<Patients> patients = new ArrayList<Patients>();
                        patients = response.body().getPatients();
                        if(doctors.size() > 0)
                        {
                            textDoctor.setText("List of Doctors");
                            textDoctor.setVisibility(View.VISIBLE);
                            doctorRecyclerview.setVisibility(View.VISIBLE);
                            doctorAdapter = new DoctorAdapter(getContext(), doctors);
                            doctorAdapter.setOnTapListener(new OnTapListener() {
                                @Override
                                public void onTapView(String name) {
                                    fragment = new FragmentClientChat();
                                    Bundle bundle = new Bundle();
                                    bundle.putString("NAME", name);
                                    fragment.setArguments(bundle);
                                    FragmentManager fragmentManager = getFragmentManager();
                                    fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit();
                                }
                            });
                            //Log.i("doctorsss", response.body().getAssistantDoctors().toString());
                            doctorRecyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
                            doctorRecyclerview.setAdapter(doctorAdapter);
                        } else {
                            textDoctor.setText("No doctor found.");
                            textDoctor.setVisibility(View.VISIBLE);
                        }
                        if(patients.size() > 0)
                        {
                            textPatients.setText("List of Patients");
                            textPatients.setVisibility(View.VISIBLE);
                            patientsRecyclerview.setVisibility(View.VISIBLE);
                            patientsAdapter = new PatientsAdapter(getContext(), patients);
                            patientsAdapter.setOnTapListener(new OnTapListener() {
                                @Override
                                public void onTapView(String name) {
                                    fragment = new FragmentChats();
                                    Bundle bundle = new Bundle();
                                    bundle.putString("NAME", name);
                                    fragment.setArguments(bundle);
                                    FragmentManager fragmentManager = getFragmentManager();
                                    fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit();
                                }
                            });
                            //Log.i("doctorsss", response.body().getAssistantDoctors().toString());
                            patientsRecyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
                            patientsRecyclerview.setAdapter(patientsAdapter);
                        } else {
                            textPatients.setText("No patients found.");
                            textPatients.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ListViewParserDemo> call, Throwable t) {

            }
        });

        return  v;
    }
}
