package com.tju.tank;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

public class InitialActivity extends Activity {

    Button btn_ok;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);//全屏？

        setContentView(R.layout.activity_initial);
        btn_ok = findViewById(R.id.button_begin);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(InitialActivity.this, "Welcome", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(InitialActivity.this, ChoosePattern.class);
                startActivity(intent);
            }
        });

    }
}
