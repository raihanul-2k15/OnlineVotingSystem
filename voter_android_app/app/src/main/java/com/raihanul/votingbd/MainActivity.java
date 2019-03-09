package com.raihanul.votingbd;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;


public class MainActivity extends AppCompatActivity {

    public static final String TAG = "TEST";
    public static final String LOG_TAG = "TEST";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // unset the Splash screen and set the actual theme
        setTheme(R.style.AppTheme);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button proceedBtn = (Button) findViewById(R.id.proceed_to_login);
        proceedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(i);
            }
        });
    }
}
