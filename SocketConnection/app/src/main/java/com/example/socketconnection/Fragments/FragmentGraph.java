package com.example.socketconnection.Fragments;

import android.media.ToneGenerator;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.example.socketconnection.JsonEncoder.Contacts;
import com.example.socketconnection.JsonEncoder.Login;
import com.example.socketconnection.JsonEncoder.SensedData;
import com.example.socketconnection.Parser.EnterDataMongoParserDemo;
import com.example.socketconnection.Parser.MailerParserDemo;
import com.example.socketconnection.Parser.PatientParser;
import com.example.socketconnection.R;
import com.example.socketconnection.ServerIps;
import com.example.socketconnection.Services.ApiService;
import com.example.socketconnection.Session.SessionManager;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FragmentGraph extends Fragment {
    int SERVER_PORT = 6000;
    String SERVER_IP = "";
    Thread thread1 = null;
    private PrintWriter output;
    private int flag = 0, tempFlag = 0;
    private int ecgFlag, bpFlag;
    private BufferedReader input;
    Socket socket;
    View v;
    LineGraphSeries<DataPoint> lineGraphSeries;
    GraphView graphView;
    int lastX = 0;
    TextView textView, showTemp, showHumidity, nameText;
    Retrofit retrofit1;
    ApiService apiService1;
    String emails="", subject="", messages="", namess="", demail="", dname="", dmessage="",
            dsubject="";
    String pname = "";
    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://random6.xyz/cprograms/Mails/").addConverterFactory(GsonConverterFactory.create()).build();
    ApiService apiService = retrofit.create(ApiService.class);
    SessionManager sessionManager;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_graph,container,false);
        ServerIps serverIps = new ServerIps();
        graphView = v.findViewById(R.id.androidGraph);
        textView = v.findViewById(R.id.showBp);
        nameText = v.findViewById(R.id.nameText);
        showTemp = v.findViewById(R.id.showTemperature);
        showHumidity = v.findViewById(R.id.showHumidity);
        lineGraphSeries  =  new LineGraphSeries<DataPoint>();
        graphView.addSeries(lineGraphSeries);
        String url = serverIps.getREST_IP();
        retrofit1 =
                new Retrofit.Builder().baseUrl(url).addConverterFactory(GsonConverterFactory.create()).build();
        apiService1 = retrofit1.create(ApiService.class);
        graphView.getViewport().setMinX(0);
        graphView.getViewport().setMaxX(100);
        graphView.getViewport().setMinY(0);
        graphView.getViewport().setMaxY(1000);
        graphView.getViewport().setXAxisBoundsManual(true);
        graphView.getViewport().setYAxisBoundsManual(true);
        graphView.getViewport().setScrollable(true);
        SERVER_IP = serverIps.getGRAPH_IP();
        sessionManager =
                new SessionManager(getContext());
        String type = sessionManager.getType();
        if(type.equals("Doctor"))
        {
            emails = "dpkprjpti@gmail.com";
            messages = "Hello Sir/Madam patient Kartik's " +
                    "pulse rate is high. Please contact " +
                    "doctor immediately.";
            subject = "Emergency mail from doctor";
            namess = "Deepak";
        }
        else if(type.equals("assistantDoctor"))
        {
            namess = "Deepak";
            emails = "dpkprjpti@gmail.com";
            messages = "Hello Sir/Madam patient Kartik's " +
                    "pulse rate is high. Please contact " +
                    "doctor immediately.";
            subject = "Health Alert";
        }
        else if(type.equals("Patients"))
        {
            namess = "Pooja";
            emails = "poojasprajapati1996@gmail.com";
            messages = "Hello Sir/Madam patient Kartik's " +
                    "pulse rate is high. Please contact " +
                    "doctor immediately.";
            subject = "Emergency mail from doctor";
        }
        getPatientName();
        thread1 = new Thread(new Thread1());
        thread1.start();
        return v;
    }

    public void addEntry(int number)
    {
        lineGraphSeries.appendData(new DataPoint(lastX++, number), false, 1000);
    }

    class Thread1 implements Runnable {
        @Override
        public void run() {
            try {
                socket = new Socket(SERVER_IP, SERVER_PORT);
                output = new PrintWriter(socket.getOutputStream());
                input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("conns", "Connection successful.");
                        new Thread(new Thread2()).start();
                    }
                });
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    class Thread2 implements Runnable {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void run() {
            while (true)
            {
                try
                {
                    output.write("a");
                    output.flush();
                    final String message = input.readLine();
                    Log.i("GRAPH_FRAGMENT", "run: "+message);
                    if(message != null)
                    {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try
                                {
                                    JSONObject jsonObject = new JSONObject(message);
                                    int ecg = jsonObject.getInt("ecg");
                                    int bp = jsonObject.getInt("pulse");
                                    int temps = jsonObject.getInt("temperature");
                                    int humidity = jsonObject.getInt("humidity");
                                    if(bp == 0)
                                    {
                                        bpFlag = 1;
                                    }
                                    else if(bp > 0)
                                    {
                                        if(bpFlag == 1)
                                        {
                                            Calendar c = Calendar.getInstance();
                                            SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss a");
                                            String formattedDate = df.format(c.getTime());
                                            SensedData sensedData = new SensedData("0",
                                                    String.valueOf(bp),String.valueOf(temps),
                                                    String.valueOf(humidity), formattedDate, pname);
                                            Call<EnterDataMongoParserDemo> bpCall =
                                                    apiService1.insertSensedData(sensedData);
                                            bpCall.enqueue(new Callback<EnterDataMongoParserDemo>() {
                                                @Override
                                                public void onResponse(Call<EnterDataMongoParserDemo> call, Response<EnterDataMongoParserDemo> response) {
                                                    Toast.makeText(getContext(), "Data inserted " +
                                                            "successfully.", Toast.LENGTH_SHORT).show();
                                                }
                                                @Override
                                                public void onFailure(Call<EnterDataMongoParserDemo> call, Throwable t) {
                                                    Toast.makeText(getContext(), "Something went " +
                                                                    "wrong.",
                                                            Toast.LENGTH_SHORT).show();
                                                    Log.i("bpCall", t.toString());
                                                }
                                            });
                                            bpFlag = 0;
                                        }
                                        else
                                        {
                                        }
                                        if(bp > 100)
                                        {
                                            Toast.makeText(getContext(), "Your Pulse rate is high.",
                                                    Toast.LENGTH_SHORT).show();
                                            if (flag == 0)
                                            {
//                                                Log.i("emailFlag", "send mail");
                                                Calendar c = Calendar.getInstance();
                                                SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss a");
                                                String formattedDate = df.format(c.getTime());

                                                //This message is for patient. This will send to
                                                // patient;
                                                subject = "Emergency mail from doctor.";
                                                messages = "Hello your relative " + pname +
                                                        "'s pulse rate is high. Please contact " +
                                                        "doctor immediately.";

                                                //This message is for doctor. This will send to
                                                // doctor;
                                                dsubject = "Emergency mail from patient";
                                                dmessage =
                                                        "Hello Doctor "+dname+ " your patient's "+pname+
                                                                " pulse rate is high.";

                                                Call<MailerParserDemo> call = apiService.sendEmail(
                                                        emails, namess, messages,
                                                        subject, demail, dsubject, dmessage, dname);
                                                call.enqueue(new Callback<MailerParserDemo>() {
                                                    @Override
                                                    public void onResponse(Call<MailerParserDemo> call, Response<MailerParserDemo> response) {
                                                        if (response.code() == 200) {
                                                            Toast.makeText(getContext(),
                                                                    response.body().getMessage().toString(),
                                                                    Toast.LENGTH_SHORT).show();
                                                        } else if (response.code() == 404) {
                                                            Toast.makeText(getContext(),
                                                                    response.body().getMessage().toString(),
                                                                    Toast.LENGTH_SHORT).show();
                                                        }
                                                        flag = 1;
                                                    }
                                                    @Override
                                                    public void onFailure(Call<MailerParserDemo> call, Throwable t) {
                                                        Log.i("wrongss", t.toString());
                                                    }
                                                });
                                                flag = 1;
                                            }
                                            else if(flag == 1)
                                            {
                                                Log.i("emailFlag", "nothing todo.");
                                            }
                                        }
                                    }
                                    if(temps == 0)
                                    {
                                        showTemp.setText("Temperature: "+temps+" celcius");
                                        showHumidity.setText("Humidity: " + humidity + " %");
                                    }
                                    else
                                    {
                                        showTemp.setText("Temperature: "+temps+" celcius");
                                        showHumidity.setText("Humidity: " + humidity + " %");
                                        if (humidity > 84 || humidity < 75) {
                                            if (tempFlag == 0) {
                                                Calendar c = Calendar.getInstance();
                                                SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss a");
                                                String formattedDate = df.format(c.getTime());
                                                //This message is for patient. This will send to
                                                // patient;
                                                subject = "Emergency mail from doctor.";
                                                messages = "Hello Guardian your relative " + pname +
                                                        "'s temperature is high.";

                                                //This message is for doctor. This will send to
                                                // doctor;
                                                dsubject = "Emergency mail from patient";
                                                dmessage =
                                                        "Hello Doctor "+dname+ " your patient's "+pname+
                                                                " temperature is high.";
                                                Call<MailerParserDemo> call = apiService.sendEmail(
                                                        emails, namess, messages,
                                                        subject, demail, dsubject, dmessage, dname);
                                                call.enqueue(new Callback<MailerParserDemo>() {
                                                    @Override
                                                    public void onResponse(Call<MailerParserDemo> call, Response<MailerParserDemo> response) {
                                                        if (response.code() == 200) {
                                                            Toast.makeText(getContext(),
                                                                    response.body().getMessage().toString(),
                                                                    Toast.LENGTH_SHORT).show();
                                                        } else if (response.code() == 404) {
                                                            Toast.makeText(getContext(),
                                                                    response.body().getMessage().toString(),
                                                                    Toast.LENGTH_SHORT).show();
                                                        }
                                                        tempFlag = 1;
                                                    }
                                                    @Override
                                                    public void onFailure(Call<MailerParserDemo> call, Throwable t) {
                                                        Log.i("wrongss", t.toString());
                                                    }
                                                });
                                                tempFlag = 1;
                                            } else {
                                            }
                                        } else {
                                            tempFlag = 0;
                                        }
                                    }
                                    textView.setText("Pulse rate: " + String.valueOf(bp) + " PRA");
                                    Log.i("bps", String.valueOf(bp));
                                    Log.i("tempss", String.valueOf(temps));
                                    if(ecg == 0)
                                    {
                                        ecgFlag = 1;
                                    }
                                    else if(ecg > 0)
                                    {
                                        if(ecgFlag == 1)
                                        {
                                            Calendar c = Calendar.getInstance();
                                            SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss a");
                                            String formattedDate = df.format(c.getTime());
                                            SensedData sensedData =
                                                    new SensedData(String.valueOf(ecg), "0",
                                                            String.valueOf(temps),
                                                            String.valueOf(humidity),
                                                            formattedDate, pname);
                                            Call<EnterDataMongoParserDemo> ecgCall =
                                                    apiService1.insertSensedData(sensedData);
                                            ecgCall.enqueue(new Callback<EnterDataMongoParserDemo>() {
                                                @Override
                                                public void onResponse(Call<EnterDataMongoParserDemo> call, Response<EnterDataMongoParserDemo> response) {
                                                    Toast.makeText(getContext(), "Data inserted " +
                                                                    "successfully.",
                                                            Toast.LENGTH_SHORT).show();
                                                }
                                                @Override
                                                public void onFailure(Call<EnterDataMongoParserDemo> call, Throwable t) {
                                                    Toast.makeText(getContext(), "something went " +
                                                                    "wrong.",
                                                            Toast.LENGTH_SHORT).show();
                                                    Log.i("ecgCall", t.toString());
                                                }
                                            });
                                            ecgFlag = 0;
                                        }
                                        else
                                        {
                                        }
                                        addEntry(ecg);
                                    }
                                }
                                catch (JSONException js)
                                {
                                    Log.i("jsonExpections", js.toString());
                                }
                            }
                        });
                    }
                    else
                    {
                        thread1 = new Thread(new Thread1());
                        thread1.start();
                        return;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private void getPatientName() {

        Contacts contacts = new Contacts(sessionManager.getOID(), sessionManager.getType());
        Call<PatientParser> call = apiService1.getPatient(contacts);
        call.enqueue(new Callback<PatientParser>() {
            @Override
            public void onResponse(Call<PatientParser> call, Response<PatientParser> response) {
                if (response.code() == 200) {
                    pname = response.body().getPname().toString();
                    emails = response.body().getPemail().toString();
                    dname = response.body().getDname().toString();
                    demail = response.body().getDemail().toString();
                    nameText.setText("Patient Name: "+pname+"\nDoctor Name: "+dname);
                } else if (response.code() == 400) {
                    pname = "Kartik's";

                }
                Toast.makeText(v.getContext(), pname, Toast.LENGTH_SHORT).show();
                Log.i("FRAGMENTGRAPH", "onResponse: "+pname);
                Log.i("FRAGMENTGRAPH", "onResponse: "+emails);
                Log.i("FRAGMENTGRAPH", "onResponse: "+dname);
                Log.i("FRAGMENTGRAPH", "onResponse: "+demail);
            }

            @Override
            public void onFailure(Call<PatientParser> call, Throwable t) {
                Log.i("FRAGMENTGRAPH", "onFailure: "+t.getLocalizedMessage());
            }
        });
    }
}