package com.tju.tank.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class LifeView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : View(context, attrs, defStyle) {
    private var life: Int = 0
    private var widths = 600
    private var heights = 60
    private var color: Int = 0

    fun setSize(width: Int, height: Int) {
        this.widths = width
        this.heights = height
    }

    fun setLife(life: Int) {
        this.life = life
    }


    fun setColor(color: Int) {
        this.color = color
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val x = left
        val y = top

        val paintB = Paint()
        paintB.color = Color.WHITE
        paintB.style = Paint.Style.STROKE
        paintB.strokeWidth = 3f
        canvas.drawRect(x.toFloat(), y.toFloat(), (x + widths).toFloat(), (y + heights).toFloat(), paintB)
        val paintL = Paint()
        paintL.color = color
        canvas.drawRect((x + 1).toFloat(), (y + 1).toFloat(), x + widths * life.toFloat() / 100, (y + heights - 1).toFloat(), paintL)


    }

}