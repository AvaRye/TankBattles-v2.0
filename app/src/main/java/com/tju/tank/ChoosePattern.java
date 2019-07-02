package com.tju.tank;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class ChoosePattern extends Activity {
    RadioGroup playModel;
    Button btn_main_ok;
    Button btn_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);//全屏

        setContentView(R.layout.pattern_layout);
        playModel = findViewById(R.id.pattern_group);
        btn_main_ok = findViewById(R.id.btn_pattern_ok);
        btn_main_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int checkedId = playModel.getCheckedRadioButtonId();
                RadioButton radioButton = findViewById(checkedId);
                String model = radioButton.getText().toString();
                Toast.makeText(ChoosePattern.this, "" + model, Toast.LENGTH_LONG).show();
                Intent intent = new Intent(ChoosePattern.this, ChooseTank.class);
                intent.putExtra("model_data", model);
                startActivity(intent);
            }
        });
        btn_back = findViewById(R.id.btn_pattern_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChoosePattern.this.finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
