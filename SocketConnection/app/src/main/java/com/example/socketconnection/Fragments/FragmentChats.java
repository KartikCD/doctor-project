package com.example.socketconnection.Fragments;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.socketconnection.R;
import com.example.socketconnection.RegisterActivity;
import com.example.socketconnection.Session.SessionManager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class FragmentChats extends Fragment {

    View v;
    private ServerSocket serverSocket;
    private Socket tempClientSocket;
    Thread serverThread =null;
    public static final int SERVER_PORT =3003;
    private LinearLayout msgList;
    private Handler handler;
    private EditText edMessage, editMessage;
    private Button sendMessage;
    private int greenColor;
    String mainMessage;
    SessionManager sessionManager;
    String patient="", doctor="";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_chats,container,false);
        sendMessage = v.findViewById(R.id.sendmessage);
        editMessage = v.findViewById(R.id.editChats);
        editMessage.addTextChangedListener(textWatcher);
        sessionManager = new SessionManager(v.getContext());
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            patient = bundle.getString("NAME");
            doctor = sessionManager.getUsername();
        }
        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainMessage = "";
                if (sessionManager.getType().equals("Doctor")) {
                    mainMessage = doctor + ": " + editMessage.getText().toString().trim();
                } else {
                    mainMessage = doctor + ": " + editMessage.getText().toString().trim();
                }
                showMessage(mainMessage, Color.BLUE);
                sendMessage(mainMessage);

            }
        });
        connectToSocket();
        greenColor = ContextCompat.getColor(getContext(), R.color.green);
        handler = new Handler();
        msgList = v.findViewById(R.id.msgList);
        return v;
    }
    public void connectToSocket()
    {
        Toast.makeText(getContext(), "Server Started.", Toast.LENGTH_SHORT).show();
        Log.d("onServerStart","ServerStarted");
        serverThread = new Thread(new ServerThread());
        serverThread.start();
    }
    public TextView textView(String message, int color)
    {
        if (null == message || message.trim().isEmpty()) {
            message = "<Empty Message>";
        }
        TextView tv = new TextView(getContext());
        tv.setTextColor(color);
        tv.setText(message + " [" + getTime() + "]");
        tv.setTextSize(14);
        tv.setPadding(0,0,2,0);
        return tv;
    }

    public void showMessage(final String message, final int color)
    {
        handler.post(new Runnable() {
            @Override
            public void run() {
                msgList.addView(textView(message, color));
            }
        });
    }

    public void sendMessage(final String message)
    {
        try {
            if (null != tempClientSocket)
            {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        PrintWriter out = null;
                        try {
                            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(tempClientSocket.getOutputStream())),true);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        out.println(message);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                editMessage.setText("");
                            }
                        });
                    }
                }).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class ServerThread implements Runnable
    {

        @Override
        public void run() {
            Socket socket;
            try {
                serverSocket = new ServerSocket(SERVER_PORT);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(null != serverSocket)
            {
                while (!Thread.currentThread().isInterrupted())
                {
                    try {
                        socket = serverSocket.accept();
                        CommunicationThread communicationThread = new CommunicationThread(socket);
                        new Thread(communicationThread).start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    class CommunicationThread implements Runnable
    {
        private Socket clientSocket;
        private BufferedReader input;
        public CommunicationThread(Socket clientSocket)
        {
            this.clientSocket = clientSocket;
            tempClientSocket = clientSocket;
            try {
                this.input = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
            } catch (IOException e) {
                e.printStackTrace();
                showMessage("Error Connecting to client", Color.RED);
            }
            //Toast.makeText(getContext(), "Connected to client!", Toast.LENGTH_SHORT).show();
        }
        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted())
            {
                String read = null;
                try {
                    read = input.readLine();
                    if(null == read || "Disconnect".contentEquals(read))
                    {
                        Thread.interrupted();
                        Toast.makeText(getContext(), "Client Disconnected", Toast.LENGTH_SHORT).show();
                    }
                    showMessage(read, Color.RED);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    String getTime()
    {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        return simpleDateFormat.format(new Date());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != serverThread) {
            sendMessage("Disconnect");
            serverThread.interrupt();
            serverThread = null;
        }
    }

    public TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            mainMessage = editMessage.getText().toString().trim();
            sendMessage.setEnabled(!mainMessage.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };
}
