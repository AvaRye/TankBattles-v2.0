package com.tju.tank.view

import com.tju.tank.R

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.PointF
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver

class RockerView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    //固定摇杆背景圆形的X,Y坐标以及半径
    var mRockerBg_X: Float = 0.toFloat()
    var mRockerBg_Y: Float = 0.toFloat()
    var mRockerBg_R: Float = 0.toFloat()
    //摇杆的X,Y坐标以及摇杆的半径
    var mRockerBtn_X: Float = 0.toFloat()
    var mRockerBtn_Y: Float = 0.toFloat()
    var a: Int = 0
    private var mRockerBtn_R: Float = 0.toFloat()
    private val mBmpRockerBg: Bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.rocker_back1)
    private val mBmpRockerBtn: Bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.rocker_btn1)

    private var mCenterPoint: PointF? = null

    private var mRockerChangeListener: RockerChangeListener? = null

    init {
        // TODO Auto-generated constructor stub
        // 获取bitmap

        viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {

            // 调用该方法时可以获取view实际的宽getWidth()和高getHeight()
            override fun onPreDraw(): Boolean {
                // TODO Auto-generated method stub
                viewTreeObserver.removeOnPreDrawListener(this)

                Log.e("RockerView", "$width/$height")
                mCenterPoint = PointF((width / 2).toFloat(), (height / 2).toFloat())
                mRockerBg_X = mCenterPoint!!.x
                mRockerBg_Y = mCenterPoint!!.y

                mRockerBtn_X = mCenterPoint!!.x
                mRockerBtn_Y = mCenterPoint!!.y

                val tempF = mBmpRockerBg.width / (mBmpRockerBg.width + mBmpRockerBtn.width).toFloat()
                mRockerBg_R = tempF * width / 2
                mRockerBtn_R = (1.0f - tempF) * width / 2

                return true
            }
        })


        Thread(Runnable {
            while (true) {
                this@RockerView.postInvalidate()
                try {
                    Thread.sleep(100)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }).start()
    }

    override fun onDraw(canvas: Canvas) {
        // TODO Auto-generated method stub
        super.onDraw(canvas)
        canvas.drawBitmap(mBmpRockerBg, null,
                Rect((mRockerBg_X - mRockerBg_R).toInt(),
                        (mRockerBg_Y - mRockerBg_R).toInt(),
                        (mRockerBg_X + mRockerBg_R).toInt(),
                        (mRockerBg_Y + mRockerBg_R).toInt()), null)
        canvas.drawBitmap(mBmpRockerBtn, null,
                Rect((mRockerBtn_X - mRockerBtn_R).toInt(),
                        (mRockerBtn_Y - mRockerBtn_R).toInt(),
                        (mRockerBtn_X + mRockerBtn_R).toInt(),
                        (mRockerBtn_Y + mRockerBtn_R).toInt()), null)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN || event.action == MotionEvent.ACTION_MOVE) {
            // 当触屏区域不在活动范围内
            if (Math.sqrt(Math.pow((mRockerBg_X - event.x.toInt()).toDouble(), 2.0) + Math.pow((mRockerBg_Y - event.y.toInt()).toDouble(), 2.0)) >= mRockerBg_R) {
                //得到摇杆与触屏点所形成的角度
                val tempRad = getRad(mRockerBg_X, mRockerBg_Y, event.x, event.y)
                if (tempRad < 0.5 && tempRad > -0.5)
                    a = 1
                if (tempRad < 2 && tempRad > 1)
                    a = 2
                if (tempRad < -2.5 || tempRad > 2.5)
                    a = 3
                if (tempRad < -1 && tempRad > -2)
                    a = 4
                getXY(mRockerBg_X, mRockerBg_Y, mRockerBg_R, tempRad)
            } else {
                //如果小球中心点小于活动区域则随着用户触屏点移动即可
                mRockerBtn_X = event.x.toInt().toFloat()
                mRockerBtn_Y = event.y.toInt().toFloat()
                val dRad = getRad(mRockerBtn_X, mRockerBtn_Y, mRockerBg_X, mRockerBg_Y + mRockerBg_R)
            }
            if (mRockerChangeListener != null) {
                mRockerChangeListener!!.report(mRockerBtn_X - mCenterPoint!!.x, mRockerBtn_Y - mCenterPoint!!.y)
            }
        } else if (event.action == MotionEvent.ACTION_UP) {
            mRockerBtn_X = mCenterPoint!!.x
            mRockerBtn_Y = mCenterPoint!!.y
            if (mRockerChangeListener != null) {
                mRockerChangeListener!!.report(0f, 0f)
            }
        }

        return true

    }

    /***
     * 得到两点之间的弧度
     */
    fun getRad(px1: Float, py1: Float, px2: Float, py2: Float): Double {
        //得到两点X的距离
        val x = px2 - px1
        //得到两点Y的距离
        val y = py1 - py2
        //算出斜边长
        val xie = Math.sqrt(Math.pow(x.toDouble(), 2.0) + Math.pow(y.toDouble(), 2.0)).toFloat()
        //得到这个角度的余弦值（通过三角函数中的定理 ：邻边/斜边=角度余弦值）
        val cosAngle = x / xie
        //通过反余弦定理获取到其角度的弧度
        var rad = Math.acos(cosAngle.toDouble()).toFloat()
        //注意：当触屏的位置Y坐标<摇杆的Y坐标我们要取反值-0~-180
        if (py2 < py1) {
            rad = -rad
        }
        return rad.toDouble()
    }

    /**
     * @param R       圆周运动的旋转点
     * @param centerX 旋转点X
     * @param centerY 旋转点Y
     * @param rad     旋转的弧度
     */
    fun getXY(centerX: Float, centerY: Float, R: Float, rad: Double) {
        //获取圆周运动的X坐标
        mRockerBtn_X = (R * Math.cos(rad)).toFloat() + centerX
        //获取圆周运动的Y坐标
        mRockerBtn_Y = (R * Math.sin(rad)).toFloat() + centerY
    }

    fun setRockerChangeListener(rockerChangeListener: RockerChangeListener) {
        mRockerChangeListener = rockerChangeListener
    }

    interface RockerChangeListener {
        fun report(x: Float, y: Float)
    }
}
