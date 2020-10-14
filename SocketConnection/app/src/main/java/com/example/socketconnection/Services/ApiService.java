package com.example.socketconnection.Services;

import com.example.socketconnection.JsonEncoder.AssistantDoctor;
import com.example.socketconnection.JsonEncoder.AssistantPatientReg;
import com.example.socketconnection.JsonEncoder.Contacts;
import com.example.socketconnection.JsonEncoder.Login;
import com.example.socketconnection.JsonEncoder.Patients;
import com.example.socketconnection.JsonEncoder.Register;
import com.example.socketconnection.JsonEncoder.SensedData;
import com.example.socketconnection.Parser.EnterDataMongoParserDemo;
import com.example.socketconnection.Parser.ListViewParserDemo;
import com.example.socketconnection.Parser.LoginParserDemo;
import com.example.socketconnection.Parser.MailerParserDemo;
import com.example.socketconnection.Parser.PatientParser;
import com.example.socketconnection.Parser.RegisterParserDemo;

import java.util.Calendar;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {
    @POST("registration")
    Call<RegisterParserDemo> registerDoctor(@Body Register body);

    @POST("Login")
    Call<LoginParserDemo> loginDoctor(@Body Login body);

    @POST("astDoctorReg")
    Call<RegisterParserDemo> registerAssistantDoctor(@Body AssistantDoctor assistantDoctor);

    @POST("patientReg")
    Call<RegisterParserDemo> registerPatient(@Body Patients patients);

    @POST("chatpanel")
    Call<ListViewParserDemo> getContacts(@Body Contacts contacts);

    @POST("assistantPatientReg")
    Call<RegisterParserDemo> registerPatientAstDoctor(@Body AssistantPatientReg assistantPatientReg);

    @GET("mailer.php")
    Call<MailerParserDemo> sendEmail(@Query("email") String email, @Query("pname") String pname,
                                     @Query("message") String dname,
                                     @Query("subject") String time,
                                     @Query("demail") String demail,
                                     @Query("dsubject") String dsubject,
                                     @Query("dmessage") String dmessage,
                                     @Query("dname") String name);



    @POST("sensedData")
    Call<EnterDataMongoParserDemo> insertSensedData(@Body SensedData sensedData);

    @POST("getpatient")
    Call<PatientParser> getPatient(@Body Contacts contacts);
}
