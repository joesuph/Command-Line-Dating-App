package com.example.finalproject;

import android.os.AsyncTask;

public class MatchProfileTask extends AsyncTask<Void, Void, Void> {

    MainActivity ma;
    String matchedId;

    MatchProfileTask(MainActivity act,String profileId){
        ma = act;
        matchedId = profileId;
    }

    @Override
    protected Void doInBackground(Void... params) {
        ma.dbit.match(Long.valueOf(matchedId));
        return null;
    }

    @Override
    protected void onPostExecute(final Void succ) {


    }
}
