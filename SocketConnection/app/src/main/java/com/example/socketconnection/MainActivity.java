package com.example.socketconnection;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.socketconnection.Fragments.FragmentAssistant;
import com.example.socketconnection.Fragments.FragmentChats;
import com.example.socketconnection.Fragments.FragmentClientChat;
import com.example.socketconnection.Fragments.FragmentGraph;
import com.example.socketconnection.Fragments.FragmentInfo;
import com.example.socketconnection.Fragments.FragmentListClients;
import com.example.socketconnection.Fragments.FragmentPatients;
import com.example.socketconnection.Session.SessionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity {
    SessionManager sessionManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sessionManager = new SessionManager(getApplicationContext());
        //sessionManager.checkLogin();

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new FragmentInfo()).commit();
        Log.i("MyTag", "onCreate: I am here...123");
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);
        Toast.makeText(this, sessionManager.getOID(), Toast.LENGTH_SHORT).show();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment selected = null;
        switch (item.getItemId())
        {
            case R.id.nav_graph:
                selected = new FragmentGraph();
                break;
            case R.id.nav_astdoctor:
                selected = new FragmentAssistant();
                break;
            case R.id.nav_patient:
                selected = new FragmentPatients();
                break;
            case R.id.nav_chats:
                selected = new FragmentListClients();
                break;
            case R.id.nav_info:
                selected = new FragmentInfo();
                break;
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,selected).commit();
        return true;
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.threedotsmenu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id)
        {
            case R.id.logout:
                sessionManager.logoutUser();
                break;
        }
        return true;
    }
}
