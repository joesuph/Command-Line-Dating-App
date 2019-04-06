package com.example.finalproject;

import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.Map;

public class GetMatchesTask extends AsyncTask<Void, Void, ArrayList<Map<String,Object>>> {

    MainActivity ma;

    GetMatchesTask(MainActivity man ){
    ma = man;
    }

    @Override
    protected ArrayList<Map<String,Object>> doInBackground(Void... params) {
        return ma.dbit.getMatches();
    }

    @Override
    protected void onPostExecute(final ArrayList<Map<String,Object>> profiles) {
        String result = "\n\t{";
        for (Map<String, Object> m : profiles){
            result += "\n\t\t{";

            result += "\n\t\t\tname: " + m.get("name");
            result += "\n\t\t\tdescription: " + m.get("description");
            result += "\n\t\t\tphone_number: " + m.get("contactInfo");

            result += "\n\t\t}";
        }
        result += "\n\t}";
        ma.updateUI("grep 'ping success' profiles.json",result);
    }
}
