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

        //When the user clicks enter after typing in console
        final EditText edittext = (EditText) findViewById(R.id.editText);
        edittext.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                    process(edittext.getText().toString());
                    return true;
                }
                return false;
            }
        });
    }
        public void process(String args) {
            String result = "";

            if(args.equals("cat profiles.json")){
                getProfiles();
                return;
            }

            args.split(" ");
            switch(args)
            {
                case "":
                    result = "";
                    break;
                case "help":
                    result += "\n\t\t>>Type \"cat profiles.json\" to see profiles";
                    result += "\n\t\t>>Type \"ping <userid>\" to like a user";
                    result += "\n\t\t>>Type \"grep 'likesMe' profiles.json\" to see who you liked you back";
                    break;
            }
            updateUI(args,result);
        }

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
