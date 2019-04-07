package com.example.finalproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    DatabaseItems dbit = new DatabaseItems();
    ArrayList<Map<String, Object>> profiles;

    /****
     * Get's profiles ready to display when called
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);


        Intent intent = getIntent();
        String message = intent.getStringExtra(LoginActivity.EXTRA_MESSAGE);
        String[] arr = message.split(":", 2);
        String email = arr[0];
        String password = arr[1];

        GetAccountInfoTask task = new GetAccountInfoTask(email, password, dbit, this);
        task.execute((Void) null);

        //Event handler for when the user clicks enter.
        final EditText edittext = (EditText) findViewById(R.id.editText);
        edittext.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform process text on key press
                    process(edittext.getText().toString());
                    return true;
                }
                return false;
            }
        });
    }

    /**
     * @param args
     * Decide what text to display. depending on the arguments type
     */
    public void process(String args) {
            String result = "";

            if(args.equals("cat profiles.json")){
                getProfiles();
                return;
            }

            //Separate object from command to determine process
            String commands[] = args.split(" ");
            switch(commands[0])
            {
                case "help":
                    result += "\n\t\t>>Type \"cat profiles.json\" to view profiles ";
                    result += "\n\t\t>>Type \"ping <userid>\" to like a user (Test connection)";
                    result += "\n\t\t>>Type \"grep 'Ping Success' profiles.json\" to see who liked you back";
                    break;
                case "ping":
                    LikeProfileTask lpt = new LikeProfileTask(this,commands[1]);
                    lpt.execute((Void) null );
                    return;
                case "grep":
                    GetMatchesTask gmt = new GetMatchesTask(this);
                    gmt.execute((Void) null);
                    return;

            }
            updateUI(args,result);
        }
        //Manages getProfiles process
        public void getProfiles(){
            String result = "\n\t{";
            for (Map<String,Object> p : profiles){
                result += "\n\t\t{";
                result += "\n\t\t\tuserId: \"" + p.get("userId") + "\"";
                result += "\n\t\t\tname: \"" + p.get("name") + "\"";
                result += "\n\t\t\tdescription: \"" + p.get("description") + "\"";
                result += "\n\t\t}";
            }
            result += "\n\t}";
            updateUI("cat profiles.json",result);
        }

    /**
     *
     * @param input
     * @param result
     * Manages Updating console.
     */
    public void updateUI(String input, String result){
            LinearLayout llviews = (LinearLayout) findViewById(R.id.linear_layout);
            TextView tvText = new TextView(new ContextThemeWrapper(this, R.style.terminalTextView), null, 0);

            //Set margin of textview
            float dpRatio = this.getResources().getDisplayMetrics().density;
            LinearLayout.LayoutParams params= new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins((int)(20 * dpRatio), (int)(8 * dpRatio), 0, (int)(15 * dpRatio));
            tvText.setLayoutParams(params);

            tvText.setText(input + result);
            llviews.addView(tvText);
            EditText et = (EditText) findViewById(R.id.editText);
            et.setText("");
            llviews.removeView(et);
            llviews.addView(et);

            ScrollView sv = (ScrollView)findViewById(R.id.scroll_view);
            sv.scrollTo(0, sv.getBottom());
            et.setFocusableInTouchMode(true);
            et.requestFocus();
        }

}
