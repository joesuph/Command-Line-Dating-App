package com.example.finalproject;

import android.os.AsyncTask;

public class GetAccountInfoTask extends AsyncTask<Void, Void, Boolean> {

    private String mEmail;
    private String mPassword;
    private DatabaseItems dbit;
    private MainActivity ma;

    GetAccountInfoTask(String email, String password, DatabaseItems db, MainActivity ma1) {
        mEmail = email;
        mPassword = password;
        dbit = db;
        ma = ma1;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        return dbit.getAccountInfo(mEmail,mPassword);
    }

    @Override
    protected void onPostExecute(final Boolean succ) {
        System.out.println("AccountInfo: " + Boolean.toString(succ));
        GetProfilesTask gpt = new GetProfilesTask(dbit, ma);
        gpt.execute((Void) null);

    }

}