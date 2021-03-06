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
val sizeFactor : Float = 4.8f
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
        scale(1f, 1f - 2 * j)
        translate(0f, h / 2)
        drawRect(RectF(-wSize / 2, -hSize * sfj, wSize / 2, 0f), paint)
        restore()
    }

    for (j in 0..(parts - 1)) {
        val sfj : Float = sf.divideScale(1 + 2 * j, parts * parts + 1)
        save()
        scale(1f - 2 * j, 1f)
        translate(-w / 2, 0f)
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

    private val renderer : Renderer = Renderer(this)

    override fun onDraw(canvas : Canvas) {
        renderer.render(canvas)
    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                renderer.handleTap()
            }
        }
        return true
    }

    data class State(var scale : Float = 0f, var dir : Float = 0f, var prevScale : Float = 0f) {

        fun update(cb : (Float)  -> Unit) {
            scale += scGap * dir
            if (Math.abs(scale - prevScale) > 1) {
                scale = prevScale + dir
                dir =0f
                prevScale = scale
                cb(prevScale)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            if (dir == 0f) {
                dir = 1f - 2 * prevScale
                cb()
            }
        }
    }

    data class Animator(var view : View, var animated : Boolean = false) {

        fun animate(cb : () -> Unit) {
            if (animated) {
                cb()
                try {
                    Thread.sleep(delay)
                    view.invalidate()
                } catch(ex : Exception) {

                }
            }
        }

        fun start() {
            if (!animated) {
                animated = true
                view.postInvalidate()
            }
        }

        fun stop() {
            if (animated) {
                animated = false
            }
        }
    }

    data class CFSBNode(var i : Int, val state : State = State()) {

        private var next : CFSBNode? = null
        private var prev : CFSBNode? = null

        init {
            addNeighbor()
        }

        fun addNeighbor() {
            if (i < colors.size - 1) {
                next = CFSBNode(i + 1)
                next?.prev = this
            }
        }

        fun draw(canvas : Canvas, paint : Paint) {
            canvas.drawCFSBNode(i, state.scale, paint)
        }

        fun update(cb : (Float) -> Unit) {
            state.update(cb)
        }

        fun startUpdating(cb : () -> Unit) {
            state.startUpdating(cb)
        }

        fun getNext(dir : Int, cb : () -> Unit) : CFSBNode {
            var curr : CFSBNode? = prev
            if (dir == 1) {
                curr = next
            }
            if (curr != null) {
                return curr
            }
            cb()
            return this
        }
    }

    data class ColoredFourSideBar(var i : Int) {

        private var curr : CFSBNode = CFSBNode(0)
        private var dir : Int = 1

        fun draw(canvas : Canvas, paint : Paint) {
            curr.draw(canvas, paint)
        }

        fun update(cb : (Float) -> Unit) {
             curr.update {
                 curr = curr.getNext(dir) {
                    dir *= -1
                 }
                 cb(it)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            curr.startUpdating(cb)
        }
    }

    data class Renderer(var view : ColoredFourSideBarView) {

        private val animator : Animator = Animator(view)
        private val cfsb : ColoredFourSideBar = ColoredFourSideBar(0)
        private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)

        fun render(canvas : Canvas) {
            canvas.drawColor(backColor)
            cfsb.draw(canvas, paint)
            animator.animate {
                cfsb.update {
                    animator.stop()
                }
            }
        }

        fun handleTap() {
            cfsb.startUpdating {
                animator.start()
            }
        }
    }

    companion object {

        fun create(activity : Activity) : ColoredFourSideBarView {
            val view : ColoredFourSideBarView = ColoredFourSideBarView(activity)
            activity.setContentView(view)
            return view
        }
    }
}