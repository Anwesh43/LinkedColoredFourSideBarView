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
