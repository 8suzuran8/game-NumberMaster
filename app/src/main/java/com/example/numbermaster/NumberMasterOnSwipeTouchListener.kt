package com.example.numbermaster

import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.abs

open class NumberMasterOnSwipeTouchListener(context: AppCompatActivity): View.OnTouchListener {

    private val gestureDetector: GestureDetector
    var flingResult: Boolean? = null

    companion object {
        private const val SWIPE_THRESHOLD = 100
        private const val SWIPE_VELOCITY_THRESHOLD = 100
    }

    init {
        this.gestureDetector = GestureDetector(context, GestureListener())
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        v.performClick()

        this.gestureDetector.setIsLongpressEnabled(false)
        return this.gestureDetector.onTouchEvent(event)
    }

    private inner class GestureListener : GestureDetector.SimpleOnGestureListener() {
        override fun onDown(e: MotionEvent): Boolean {
            return true
        }

        override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
            var result = false
            try {
                val diffY = e2.y - e1.y
                val diffX = e2.x - e1.x
                if (abs(diffX) > abs(diffY)) {
                    if (abs(diffX) > SWIPE_THRESHOLD && abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0) {
                            onSwipeRight(0, e1.x, e1.y)
                        } else {
                            onSwipeLeft(0, e1.x, e1.y)
                        }
                        result = true
                    }
                } else if (abs(diffY) > SWIPE_THRESHOLD && abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffY > 0) {
                        onSwipeBottom(0, e1.x, e1.y)
                    } else {
                        onSwipeTop(0, e1.x, e1.y)
                    }
                    result = true
                }
            } catch (exception: Exception) {
                exception.printStackTrace()
            }

            flingResult = result
            return result
        }
    }

    open fun onSwipeRight(puzzleIdNumberDefault: Int, x: Float? = null, y: Float? = null) {}
    open fun onSwipeLeft(puzzleIdNumberDefault: Int, x: Float? = null, y: Float? = null) {}
    open fun onSwipeTop(puzzleIdNumberDefault: Int, x: Float? = null, y: Float? = null) {}
    open fun onSwipeBottom(puzzleIdNumberDefault: Int, x: Float? = null, y: Float? = null) {}
}