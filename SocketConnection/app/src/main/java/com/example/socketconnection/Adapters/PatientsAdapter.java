package com.example.socketconnection.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socketconnection.OpTapListener.OnTapListener;
import com.example.socketconnection.Parser.AssistantDoctor1;
import com.example.socketconnection.Parser.Patients;
import com.example.socketconnection.R;

import java.util.ArrayList;

public class PatientsAdapter extends RecyclerView.Adapter<PatientsAdapter.ViewHolder1> {
    private Context context;
    private ArrayList<Patients> doctors;
    OnTapListener onTapListener;

    public PatientsAdapter(Context context, ArrayList<Patients> doctors) {
        this.context = context;
        this.doctors = doctors;
    }

    @NonNull
    @Override
    public PatientsAdapter.ViewHolder1 onCreateViewHolder(@NonNull ViewGroup parent,
                                                                 int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.list_doctor, parent, false);
        PatientsAdapter.ViewHolder1 viewHolder1 = new PatientsAdapter.ViewHolder1(view);
        return viewHolder1;
    }

    @Override
    public void onBindViewHolder(@NonNull PatientsAdapter.ViewHolder1 holder, int position) {
        final int pos = position;
        holder.textView.setText(doctors.get(position).getName());
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onTapListener.onTapView(doctors.get(pos).getName());
            }
        });
    }

    public void setOnTapListener(OnTapListener onTapListener)
    {
        this.onTapListener = onTapListener;
    }

    @Override
    public int getItemCount() {
        return doctors.size();
    }

    public static class ViewHolder1 extends RecyclerView.ViewHolder
    {
        public TextView textView;
        public RelativeLayout relativeLayout;

        public ViewHolder1(@NonNull View itemView) {
            super(itemView);
            this.textView = itemView.findViewById(R.id.doctorText);
            relativeLayout = itemView.findViewById(R.id.doctorRelative);
        }
    }
}
