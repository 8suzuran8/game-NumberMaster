package com.example.numbermaster

import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity

open class NumberMasterOnSwipeTouchListener(context: AppCompatActivity): View.OnTouchListener {
    private val gestureDetector: GestureDetector
    var flingResult: Boolean? = null

    private var e1: MutableMap<String, Float?> = mutableMapOf(
        "x" to null,
        "y" to null,
    )
    private var e2: MutableMap<String, Float?> = mutableMapOf(
        "x" to null,
        "y" to null,
    )

    companion object {
        private const val SWIPE_THRESHOLD = 100
        private const val SWIPE_VELOCITY_THRESHOLD = 100
    }

    init {
        this.gestureDetector = GestureDetector(context, GestureListener())
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        v.performClick()

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (e1["x"] == null) {
                    this.e1["x"] = event.x
                    this.e1["y"] = event.y
                }
            }
            MotionEvent.ACTION_UP -> {
                this.e2["x"] = event.x
                this.e2["y"] = event.y

                var result = false
                try {
                    val diffY = this.e2["y"]!! - this.e1["y"]!!
                    val diffX = this.e2["x"]!! - this.e1["x"]!!

                    if (kotlin.math.abs(diffX) > kotlin.math.abs(diffY) && kotlin.math.abs(diffX) > SWIPE_THRESHOLD) {
                        if (diffX > 0) {
                            onSwipeRight(0, e1["x"], e1["y"])
                        } else {
                            onSwipeLeft(0, e1["x"], e1["y"])
                        }
                        result = true
                    } else if (kotlin.math.abs(diffX) < kotlin.math.abs(diffY) && kotlin.math.abs(diffY) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffY > 0) {
                            onSwipeBottom(0, e1["x"], e1["y"])
                        } else {
                            onSwipeTop(0, e1["x"], e1["y"])
                        }
                        result = true
                    } else {
                        this.e1["x"] = null
                        this.e1["y"] = null

                        this.gestureDetector.setIsLongpressEnabled(false)
                        return this.gestureDetector.onTouchEvent(event)
                    }
                } catch (exception: Exception) {
                    this.e1["x"] = null
                    this.e1["y"] = null

                    exception.printStackTrace()
                }

                this.e1["x"] = null
                this.e1["y"] = null

                flingResult = result
                return result
            }
        }

        return true
    }

    private inner class GestureListener : GestureDetector.SimpleOnGestureListener() {
        override fun onDown(e: MotionEvent): Boolean {
            return true
        }
    }

    open fun onSwipeRight(puzzleIdNumberDefault: Int, x: Float? = null, y: Float? = null) {}
    open fun onSwipeLeft(puzzleIdNumberDefault: Int, x: Float? = null, y: Float? = null) {}
    open fun onSwipeTop(puzzleIdNumberDefault: Int, x: Float? = null, y: Float? = null) {}
    open fun onSwipeBottom(puzzleIdNumberDefault: Int, x: Float? = null, y: Float? = null) {}
}