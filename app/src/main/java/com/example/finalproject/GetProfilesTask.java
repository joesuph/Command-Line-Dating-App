package com.example.finalproject;

import android.os.AsyncTask;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;

public class GetProfilesTask extends AsyncTask<Void, Void, ArrayList<Map<String, Object>>> {

    private DatabaseItems dbit;
    private MainActivity ma;

    GetProfilesTask(DatabaseItems db, MainActivity ma1) {
        dbit = db;
        ma = ma1;
    }

    @Override
    protected ArrayList<Map<String, Object>> doInBackground(Void... params) {
        return dbit.getProfiles();
    }

    @Override
    protected void onPostExecute(final ArrayList<Map<String, Object>> profiles) {
        System.out.println("Her name is :" + profiles.get(0).get("name"));
        TextView tv = ma.findViewById(R.id.Name);
        tv.setText((String) profiles.get(0).get("name"));


    }

}