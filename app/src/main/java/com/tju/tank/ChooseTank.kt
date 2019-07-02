package com.tju.tank

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.widget.*
import com.tju.tank.control.ControlActivity
import com.tju.tank.support.Key

class ChooseTank : Activity() {
    private lateinit var tank: RadioGroup
    private lateinit var btnMainOk: Button
    private lateinit var btnBack: Button
    private var tankN: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)//全屏？

        setContentView(R.layout.choosetank_layout)
        tank = findViewById(R.id.tank_group)
        btnMainOk = findViewById(R.id.btn_tank_ok)
        btnMainOk.setOnClickListener {
            val checkedId = tank.checkedRadioButtonId
            val radioButton = findViewById<RadioButton>(checkedId)
            tankN = radioButton.text.toString().substring(0 until radioButton.text.toString().indexOf(' '))
            val intent = intent
            val pModel = intent.getStringExtra("model_data")
            Toast.makeText(this@ChooseTank, "Model: $pModel You have chosen $tankN", Toast.LENGTH_LONG).show()
            alert_edit()
        }

        btnBack = findViewById(R.id.btn_tank_back)
        btnBack.setOnClickListener { this@ChooseTank.finish() }
    }

    private fun alert_edit() {
        val et = EditText(this)
        AlertDialog.Builder(this).setTitle("设置IP号ַ")
                .setIcon(android.R.drawable.sym_def_app_icon)
                .setView(et)
                .setPositiveButton("确定") { dialogInterface, i ->
                    val IPAddress = et.text.toString()
                    val intent2 = Intent(this@ChooseTank, ControlActivity::class.java)
                    intent2.putExtra(Key.DATA, tankN)
                    intent2.putExtra(Key.SERVER_IP_KEY, IPAddress)
                    startActivity(intent2)
                }.setNegativeButton("取消", null).show()
    }
}
