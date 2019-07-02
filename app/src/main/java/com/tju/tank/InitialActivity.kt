package com.tju.tank

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.RadioButton
import android.widget.Toast

class InitialActivity : Activity() {

    private lateinit var btnOk: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)//全屏？

        setContentView(R.layout.activity_initial)
        btnOk = findViewById(R.id.button_begin)
        btnOk.setOnClickListener {
            Toast.makeText(this@InitialActivity, "Welcome", Toast.LENGTH_LONG).show()
            val intent = Intent(this@InitialActivity, ChoosePattern::class.java)
            startActivity(intent)
        }

    }
}
