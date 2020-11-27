package com.example.socketconnection.Fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.socketconnection.R;
import com.example.socketconnection.ServerIps;
import com.example.socketconnection.Session.SessionManager;

public class FragmentInfo extends Fragment {
    View v;
    TextView infoText;
    ProgressDialog progressDialog;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_info, container, false);

        infoText = v.findViewById(R.id.informationText);
        progressDialog = new ProgressDialog(v.getContext());
        WebView webView =(WebView) v.findViewById(R.id.webView);
        showProgressBar();
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                stopProgressBar();
                super.onPageFinished(view, url);
            }

            @Override
            public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
                super.doUpdateVisitedHistory(view, url, isReload);
            }
        });
        ServerIps serverIps = new ServerIps();
        SessionManager sessionManager = new SessionManager(v.getContext());
        webView.loadUrl(serverIps.getREST_IP()+"detailsmobile?id="+sessionManager.getOID()+"&type="+sessionManager.getType());
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setDisplayZoomControls(true);
        webSettings.setSupportZoom(true);
        return v;
    }

    private void showProgressBar() {
        progressDialog.setMessage("Retreiving details. Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);
        progressDialog.show();
    }
    private void stopProgressBar() {
        progressDialog.dismiss();
    }
}
