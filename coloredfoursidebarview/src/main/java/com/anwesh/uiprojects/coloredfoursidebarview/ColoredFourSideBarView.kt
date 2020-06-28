package com.anwesh.uiprojects.coloredfoursidebarview

/**
 * Created by anweshmishra on 29/06/20.
 */

import android.view.View
import android.view.MotionEvent
import android.graphics.Paint
import android.graphics.Color
import android.graphics.Canvas
import android.graphics.RectF
import android.app.Activity
import android.content.Context

val colors : Array<String> = arrayOf("#F44336", "#4CAF50", "#2196F3", "#009688", "#3F51B5")
val sizeFactor : Float = 3f
val backColor : Int = Color.parseColor("#BDBDBD")
val delay : Long = 20
val parts : Int = 2
val scGap : Float = 0.02f / (parts * parts)

fun Int.inverse() : Float = 1f / this
fun Float.maxScale(i : Int, n : Int) : Float = Math.max(0f, this - i * n.inverse())
fun Float.divideScale(i : Int, n : Int) : Float = Math.min(n.inverse(), maxScale(i, n)) * n
fun Float.sinify() : Float = Math.sin(this * Math.PI).toFloat()

fun Canvas.drawColoredFourSidedBar(scale : Float, w : Float, h : Float, paint : Paint) {
    val sf : Float = scale.sinify()
    val hSize : Float = h / sizeFactor
    val wSize : Float = w / sizeFactor
    for (j in 0..(parts - 1)) {
        val sfj : Float = sf.divideScale(j * 2, parts * parts + 1)
        save()
        translate(0f, h / 2)
        scale(1f, 1f - 2 * j)
        drawRect(RectF(-wSize / 2, -hSize * sfj, wSize / 2, 0f), paint)
        restore()
    }

    for (j in 0..(parts - 1)) {
        val sfj : Float = sf.divideScale(1 + 2 * j, parts * parts + 1)
        save()
        translate(-w / 2, 0f)
        scale(1f - 2 * j, 1f)
        drawRect(RectF(0f, -hSize / 2, wSize * sfj, hSize / 2), paint)
        restore()
    }
}

fun Canvas.drawCFSBNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    paint.color = Color.parseColor(colors[i])
    save()
    translate(w / 2, h / 2)
    drawColoredFourSidedBar(scale, w, h, paint)
    restore()
}

class ColoredFourSideBarView(ctx : Context) : View(ctx) {

    override fun onDraw(canvas : Canvas) {

    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {

            }
        }
        return true
    }
}