package com.example.finalproject;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    DatabaseItems dbit = new DatabaseItems();
    ArrayList<Map<String, Object>> profiles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
        String message = intent.getStringExtra(LoginActivity.EXTRA_MESSAGE);
        String[] arr = message.split(":", 2);
        String email = arr[0];
        String password = arr[1];

        GetAccountInfoTask task = new GetAccountInfoTask(email, password, dbit, this);
        task.execute((Void) null);
    }




}
