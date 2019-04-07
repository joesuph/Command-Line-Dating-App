package com.example.finalproject;

import android.os.AsyncTask;

public class LikeProfileTask extends AsyncTask<Void, Void, Boolean> {
    MainActivity ma;
    String likedId;
    LikeProfileTask(MainActivity act,String profileId){
        ma = act;
        likedId = profileId;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        ma.dbit.addLike(Long.valueOf(likedId));
        return ma.dbit.checkLikes(Long.valueOf(likedId));
    }

    @Override
    protected void onPostExecute(final Boolean succ) {

        System.out.println("AccountInfo: " + Boolean.toString(succ));
        String result;
        if (succ) {
            result = "Successful";
            MatchProfileTask mpt = new MatchProfileTask(ma,likedId);
            mpt.execute((Void) null);
        }
        else
            result = "failed";
        ma.updateUI("ping " + likedId,"\n\tping " + result);
    }
}
