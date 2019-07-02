package com.tju.tank

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast

class ChoosePattern : Activity() {
    private lateinit var playModel: RadioGroup
    private lateinit var btn_main_ok: Button
    private lateinit var btn_back: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)//全屏

        setContentView(R.layout.pattern_layout)
        playModel = findViewById(R.id.pattern_group)
        btn_main_ok = findViewById(R.id.btn_pattern_ok)
        btn_main_ok.setOnClickListener {
            val checkedId = playModel.checkedRadioButtonId
            val radioButton = findViewById<RadioButton>(checkedId)
            val model = radioButton.text.toString()
            Toast.makeText(this@ChoosePattern, "" + model, Toast.LENGTH_LONG).show()
            val intent = Intent(this@ChoosePattern, ChooseTank::class.java)
            intent.putExtra("model_data", model)
            startActivity(intent)
        }
        btn_back = findViewById(R.id.btn_pattern_back)
        btn_back.setOnClickListener { this@ChoosePattern.finish() }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        return if (id == R.id.action_settings) {
            true
        } else super.onOptionsItemSelected(item)
    }
}
