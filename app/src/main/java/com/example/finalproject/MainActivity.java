package com.example.finalproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    ArrayList<Map<String, Object>> profiles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DatabaseItems dbit = new DatabaseItems();
        Intent intent = getIntent();
        String message = intent.getStringExtra(LoginActivity.EXTRA_MESSAGE);
        String[] arr = message.split("_", 2);
        String email = arr[0];
        String password = arr[1];
        //Works until here! Crashes due to threading issue
        dbit.getAccountInfo(email, password);
        System.out.println("8");
        profiles = dbit.getProfiles();
        System.out.println("9");
        Map<String, Object> person = profiles.get(0);
        System.out.println("10");
        Object name = person.get("name");
        System.out.println("11");
        TextView textview = findViewById(R.id.Name);
        System.out.println("12");
        textview.setText((String) name);
    }
}
