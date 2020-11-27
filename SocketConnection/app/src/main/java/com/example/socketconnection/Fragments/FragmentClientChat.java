package com.example.socketconnection.Fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.socketconnection.R;
import com.example.socketconnection.ServerIps;
import com.example.socketconnection.Session.SessionManager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FragmentClientChat extends Fragment {

    View v;
    public static final int SERVERPORT = 3003;
    public String SERVER_IP = "";
    private Thread thread;
    private LinearLayout msgList;
    private Handler handler;
    private ClientThread clientThread;
    private int clientTextColor;
    EditText editChats;
    Button send;
    String messages;
    SessionManager sessionManager;
    String patient="", doctor="";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_clientchat, container, false);
        ServerIps serverIps = new ServerIps();
        SERVER_IP = serverIps.getCLIENT_IP();
        editChats = v.findViewById(R.id.editChats);
        send = v.findViewById(R.id.sendmessage);
        sessionManager = new SessionManager(v.getContext());
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            patient = bundle.getString("NAME");
            doctor = sessionManager.getUsername();
        }
        connectToSocket();
        editChats.addTextChangedListener(textWatcher);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sessionManager.getType().equals("assistantDoctor")) {
                    messages = doctor + ": " + editChats.getText().toString().trim();
                } else if(sessionManager.getType().equals("Patients")) {
                    messages = doctor + ": " + editChats.getText().toString().trim();
                }
                showMessage(messages, Color.BLUE);
                if (null != clientThread) {
                    clientThread.sendMessage(messages);
                    editChats.setText("");
                }
            }
        });
        clientTextColor = ContextCompat.getColor(getContext(), R.color.green);
        handler = new Handler();
        msgList = v.findViewById(R.id.msgList);
        return v;
    }

    public void connectToSocket()
    {
        clientThread = new ClientThread();
        thread = new Thread(clientThread);
        thread.start();
        return;
    }

    public TextView textView(String message, int color)
    {
        if (null == message || message.trim().isEmpty())
        {
            message = "<Empty Message>";
        }
        TextView tv = new TextView(getContext());
        tv.setTextColor(color);
        tv.setText(message + " [" + getTime() + "]");
        tv.setTextSize(14);
        tv.setPadding(0,0,2,0);
        return tv;
    }

    public void showMessage(final String message, final int color) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                msgList.addView(textView(message, color));
            }
        });
    }

    class ClientThread implements Runnable
    {
        private Socket socket;
        private BufferedReader reader;

        @Override
        public void run() {
            try {
                InetAddress servAddr = InetAddress.getByName(SERVER_IP);
                socket = new Socket(servAddr, SERVERPORT);
                while (!Thread.currentThread().isInterrupted())
                {
                    this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String message = reader.readLine();
                    if (null == message || "Disconnect".contentEquals(message)) {
                        Thread.interrupted();
                        //message = "Server Disconnected.";
                        //showMessage(message, Color.RED);
                        break;
                    }
                    showMessage(message, clientTextColor);
                }
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        void sendMessage(final String message) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (null != socket) {
                            PrintWriter out = new PrintWriter(new BufferedWriter(
                                    new OutputStreamWriter(socket.getOutputStream())),
                                    true);
                            out.println(message);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    String getTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        return simpleDateFormat.format(new Date());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != clientThread) {
            clientThread.sendMessage("Disconnect");
            clientThread = null;
        }
    }

    public TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            messages = editChats.getText().toString().trim();
            send.setEnabled(!messages.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
}
