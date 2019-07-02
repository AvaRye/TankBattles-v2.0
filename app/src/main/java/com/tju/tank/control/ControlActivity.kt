package com.tju.tank.control

import android.app.Activity
import android.app.ProgressDialog
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import com.tju.tank.R
import com.tju.tank.support.Key
import com.tju.tank.view.LifeView
import com.tju.tank.view.RockerView
import java.io.*
import java.net.Socket
import java.nio.charset.Charset
import java.util.*

open class ControlActivity : Activity() {

    var life_n = 100
    var bullet_n = 100

    private lateinit var life: LifeView
    private lateinit var bullet: LifeView
    private lateinit var rockerView1: RockerView
    private lateinit var fireBtn: Button
    private lateinit var leftBtn: Button
    private lateinit var rightBtn: Button
    private lateinit var loadBtn: Button
    private lateinit var resetBtn: Button

    private lateinit var inputStream: InputStream
    private lateinit var printWriter: PrintWriter
    private lateinit var outputStream: PrintStream
    private var mSocket: Socket? = null

    var isConnected = false
    private var ip = "192.168.3.3"
    private val port = 8080

    private lateinit var rcvThread: Thread
    private var width: Int = 0
    private var height: Int = 0

    private lateinit var tankID: String
    private lateinit var waitID: String
    private var myID: Char = ' '
    private lateinit var waitDialog: ProgressDialog
    var cmd: String? = null

    private val colorLife = -0x6a1554
    private val colorBullet = -0xbe560a
    private val colorLife1 = -0x8e90
    private val colorBullet1 = -0xac9585

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN or WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_FULLSCREEN or WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)// 设置全屏
        setContentView(R.layout.activity_control)

        ip = intent.getStringExtra(Key.SERVER_IP_KEY)

        val displayMetrics = resources.displayMetrics
        width = displayMetrics.widthPixels
        height = displayMetrics.heightPixels
        initViews()
        val timer = Timer()

        rockerView1.setRockerChangeListener(object : RockerView.RockerChangeListener {
            override fun report(x: Float, y: Float) {
                // TODO Auto-generated method stub
                setDirection(rockerView1)
            }
        })
        connectThread()

        myID = intent.getStringExtra(Key.DATA).toInt().toChar()
        tankID = "A" + myID + "1"
        waitID = "W1$myID"
        waitDialog = ProgressDialog(this@ControlActivity)
        waitDialog.setTitle("匹配对手中")
        waitDialog.setMessage("请耐心等候")
        waitDialog.setCancelable(true)
        waitDialog.show()

        fireBtn.setOnClickListener {
            cmd = "C" + myID + "5"
        }

        rightBtn.setOnClickListener {
            cmd = "C" + myID + "7"
        }

        leftBtn.setOnClickListener {
            cmd = "C" + myID + "6"
        }

        loadBtn.setOnClickListener {
            bullet_n = 100
            bullet.setColor(colorBullet)
            bullet.setLife(bullet_n)
            bullet.invalidate()
        }

        resetBtn.setOnClickListener {
            cmd = "C" + myID + "9"
            life.setColor(colorLife)
            life.setLife(100)
            life.invalidate()
            bullet.setColor(colorBullet)
            bullet.setLife(100)
            bullet.invalidate()
        }

        val timerTask = object : TimerTask() {

            override fun run() {
                if (cmd == null) {
                    send("C" + myID + "0")
                    return
                }

                if (cmd!![2] == '5') {
                    bullet_n = if (bullet_n - 10 >= 0) bullet_n - 10 else 0
                    bullet.post {
                        if (bullet_n <= 40) {
                            bullet.setColor(colorBullet1)
                        }
                        bullet.setLife(bullet_n)
                        bullet.invalidate()
                    }
                }

                send(cmd)
                cmd = null
            }
        }
        timer.schedule(timerTask, 1000, 100)//周期时间100ms*/
    }

    fun initViews() {
        rockerView1 = findViewById<View>(R.id.rockerView1) as RockerView
        life = findViewById<View>(R.id.life) as LifeView
        bullet = findViewById<View>(R.id.bullet) as LifeView
        fireBtn = findViewById<View>(R.id.Cannon_Fire) as Button
        leftBtn = findViewById<View>(R.id.Cannon_Right) as Button
        rightBtn = findViewById<View>(R.id.Cannon_Left) as Button
        loadBtn = findViewById<View>(R.id.Cannon_Load) as Button
        resetBtn = findViewById<View>(R.id.Reset) as Button
        life.setColor(colorLife)
        bullet.setColor(colorBullet)
        life.setLife(life_n)
        life.setSize(width / 3, height / 20)
        bullet.setLife(bullet_n)
        bullet.setSize(width / 3, height / 20)
    }

    fun setDirection(v: RockerView?) {
        var a = v!!.a
        when (a) {
            1 -> cmd = "C" + myID + "4"
            2 ->
                //a=0x29;
                cmd = "C" + myID + "2"
            3 ->
                //a=0x3d;
                cmd = "C" + myID + "3"
            4 ->
                //a=0x46;
                cmd = "C" + myID + "1"
        }
        a = 0
    }

    private inner class MyReceiverRunnable : Runnable {
        override fun run() {
            while (true) {
                if (isConnected) {
                    if (mSocket != null && mSocket!!.isConnected) {
                        val result = readFromInputStream(inputStream)
                        if (result == "OK") {
                            isConnected = true
                            waitDialog.dismiss()
                        } else if (result == "G") {
                        } else if (result == "D") {
                            life_n = if (life_n - 10 >= 0) life_n - 10 else 0
                            life.post {
                                if (life_n <= 40) {
                                    life.setColor(colorLife1)
                                }
                                life.setLife(life_n)
                                life.invalidate()
                            }
                        }
                    }
                }
                try {
                    Thread.sleep(100L)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }

            }
        }
    }


    fun readFromInputStream(`in`: InputStream): String? {
        var count = 0
        var inData: ByteArray? = null
        try {
            while (count == 0) {
                count = `in`.available()
            }
            inData = ByteArray(count)
            `in`.read(inData)
            return String(inData, Charset.forName("gb2312"))
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

    protected fun receiverData() {
        // mTask = new ReceiverTask();
        rcvThread = Thread(MyReceiverRunnable())
        rcvThread.start()
        isConnected = true
    }

    fun send(str: String?) {
        val len = str!!.length.toChar()

        try {
            outputStream = PrintStream(mSocket!!.getOutputStream())
            outputStream.print(len)
            outputStream.print(str)
            outputStream.flush()
        } catch (e: Exception) {
            Log.e("error",e.toString())
        }

    }

    private fun connectThread() {
        if (!isConnected) {
            Thread(Runnable {
                Looper.prepare()
                connectServer()
            }).start()
        } else {
            try {
                if (mSocket != null) {
                    mSocket!!.close()
                    mSocket = null
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }

            isConnected = false
        }
    }

    private fun connectServer() {
        try {
            if (mSocket == null) {
                mSocket = Socket(ip, port)
            }

            val outputStream = mSocket!!.getOutputStream()
            printWriter = PrintWriter(BufferedWriter(
                    OutputStreamWriter(outputStream,
                            Charset.forName("gb2312"))))
            inputStream = mSocket!!.getInputStream()
            send("player")
            send("playerX")
            receiverData()
        } catch (e: Exception) {
            //isConnected = false;
            e.printStackTrace()
        }

    }

}
