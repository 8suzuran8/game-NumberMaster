package com.example.numbermaster

import android.graphics.PixelFormat
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.setPadding
import com.example.numbermaster.databinding.ActivityCheckBinding

class CheckActivity : NumberMasterActivity() {
    var numberMaster: NumberMaster? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.base)

        this.convertIntentExtraToGlobalActivityInfo()

        val gameY = (this.globalActivityInfo["meta:rootLayoutHeight"]!!.toFloat() / 2) - (this.globalActivityInfo["meta:rootLayoutWidth"]!!.toFloat() / 2) - this.globalActivityInfo["meta:otherSize"]!!.toFloat()

        val that = this

        val inflateRootLayout = findViewById<ConstraintLayout>(R.id.root_layout)
        val activityLayout = layoutInflater.inflate(R.layout.activity_check, inflateRootLayout)
        val layoutBinding: ActivityCheckBinding = ActivityCheckBinding.bind(activityLayout).apply {
            fullSpace.layoutParams.width = that.globalActivityInfo["meta:rootLayoutWidth"]!!.toFloat().toInt()
            fullSpace.layoutParams.height = that.globalActivityInfo["meta:rootLayoutWidth"]!!.toFloat().toInt()
            val fullSpaceLayoutParams = fullSpace.layoutParams as ConstraintLayout.LayoutParams
            fullSpaceLayoutParams.setMargins(0, gameY.toInt(), 0, 0)
            fullSpace.layoutParams = fullSpaceLayoutParams
            layoutMiddle.layoutParams.height = that.globalActivityInfo["gameSpaceSize"]!!.toFloat().toInt()
            boardStandLayout.layoutParams.width = that.globalActivityInfo["gameSpaceSize"]!!.toFloat().toInt()
            boardStandLayout.layoutParams.height = that.globalActivityInfo["gameSpaceSize"]!!.toFloat().toInt()
            boardStandLayout.setPadding(that.globalActivityInfo["boardFrameWidth"]!!.toFloat().toInt())

            buttonSwipeBottom.layoutParams.height = that.globalActivityInfo["meta:otherSize"]!!.toFloat().toInt()
            buttonSwipeLeft.layoutParams.width = that.globalActivityInfo["meta:otherSize"]!!.toFloat().toInt()
            buttonSwipeRight.layoutParams.width = that.globalActivityInfo["meta:otherSize"]!!.toFloat().toInt()
            buttonSwipeTop.layoutParams.height = that.globalActivityInfo["meta:otherSize"]!!.toFloat().toInt()
        }

        val layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT).apply {
            setMargins(
                0,
                that.globalActivityInfo["meta:otherSize"]!!.toFloat().toInt(),
                0,
                that.globalActivityInfo["meta:otherSize"]!!.toFloat().toInt()
            )
        }
        addContentView(layoutBinding.rootLayout, layoutParams)
    }

    override fun onResume() {
        super.onResume()
        if (this.numberMaster != null && this.numberMaster!!.bgmMediaPlayer != null) {
            this.numberMaster!!.bgmMediaPlayer!!.start()
        }
    }

    override fun onPause() {
        super.onPause()
        if (this.numberMaster != null && this.numberMaster!!.bgmMediaPlayer != null) {
            this.numberMaster!!.bgmMediaPlayer!!.pause()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (this.numberMaster != null && this.numberMaster!!.bgmMediaPlayer != null) {
            this.numberMaster!!.bgmMediaPlayer!!.reset()
            this.numberMaster!!.bgmMediaPlayer!!.release()
            this.numberMaster!!.bgmMediaPlayer = null
        }
    }

    private fun onFinish() {
        if (this.numberMaster != null && this.numberMaster!!.bgmMediaPlayer != null) {
            this.numberMaster!!.bgmMediaPlayer!!.reset()
            this.numberMaster!!.bgmMediaPlayer!!.release()
            this.numberMaster!!.bgmMediaPlayer = null
        }
    }

    fun buttonClickListener(view: View) {
        when (view.id) {
            R.id.prev_button -> {
                this.onFinish()
                this.finish()
            }
            R.id.button_swipe_top -> {
                this.numberMaster!!.numberMasterOnSwipeTouchListener!!.onSwipeTop()
            }
            R.id.button_swipe_right -> {
                this.numberMaster!!.numberMasterOnSwipeTouchListener!!.onSwipeRight()
            }
            R.id.button_swipe_bottom -> {
                this.numberMaster!!.numberMasterOnSwipeTouchListener!!.onSwipeBottom()
            }
            R.id.button_swipe_left -> {
                this.numberMaster!!.numberMasterOnSwipeTouchListener!!.onSwipeLeft()
            }
        }
    }

    private fun drawCube() {
        val that = this
        this.numberMaster!!.cube = GLSurfaceView(this).apply {
            setEGLConfigChooser(8,8,8,8,16,0)
            setRenderer(that.numberMaster!!.numberMasterRenderer)
            setZOrderOnTop(true)
            contentDescription = "cube"
            holder.setFormat(PixelFormat.TRANSPARENT)
            visibility = View.VISIBLE
        }

        findViewById<RelativeLayout>(R.id.full_space).addView(this.numberMaster!!.cube)

        // 時間のかかる初期表示を行っておく
        this.numberMaster!!.numberMasterRenderer!!.rotateStart(this.numberMaster!!.numberMasterRenderer!!.rotateDown)
        this.numberMaster!!.invisibleCubeEvent()
    }

    override fun initialProcess(globalActivityInfo: MutableMap<String, String>) {
        super.initialProcess(globalActivityInfo)

        val that = this

        this.numberMaster = NumberMaster(this, resources, this.globalActivityInfo)

        // boardStandの設定
        this.numberMaster!!.boardStandLayout = findViewById(R.id.board_stand_layout)
        this.numberMaster!!.boardStandForeground = findViewById(R.id.board_stand_foreground)

        // 0 is background
        for (numberPanelIndex in 1 until this.numberMaster!!.boardStandLayout!!.childCount) {
            this.numberMaster!!.numberPanels[numberPanelIndex - 1] = this.numberMaster!!.boardStandLayout!!.getChildAt(numberPanelIndex) as ImageButton
            this.numberMaster!!.numberPanels[numberPanelIndex - 1]!!.apply {
                layoutParams.apply {
                    width = that.globalActivityInfo["numberPanelSize:1"]!!.toFloat().toInt()
                    height = that.globalActivityInfo["numberPanelSize:1"]!!.toFloat().toInt()
                }
                visibility = ImageButton.INVISIBLE
                isEnabled = false
                x = 0F
                y = 0F
            }
        }

        // buttonsの設定
        this.numberMaster!!.buttons["prev"] = findViewById(R.id.prev_button)
        this.numberMaster!!.buttons["swipe_bottom"] = findViewById(R.id.button_swipe_bottom)
        this.numberMaster!!.buttons["swipe_left"] = findViewById(R.id.button_swipe_left)
        this.numberMaster!!.buttons["swipe_right"] = findViewById(R.id.button_swipe_right)
        this.numberMaster!!.buttons["swipe_top"] = findViewById(R.id.button_swipe_top)

        // cube
        this.drawCube()

        this.numberMaster!!.setEvent(window, false)

        val numbers = intent.getStringExtra("numbers")!!
        val status = this.numberMaster!!.dbHelper!!.makeStatus(numbers)
        this.numberMaster!!.numbers = this.numberMaster!!.dbHelper!!.stringToNumbers(numbers)
        this.numberMaster!!.nonNumberPanelPosition = this.numberMaster!!.dbHelper!!.dataNonNumberPanelPosition
        this.numberMaster!!.status["size"] = status["size"].toString()
        this.numberMaster!!.status["stop"] = 0.toString()
        this.numberMaster!!.status["score"] = 0.toString()
        this.numberMaster!!.status["time"] = 0.toString()
        this.numberMaster!!.status["useCubeMode"] = status["use_cube_mode"].toString()
        this.numberMaster!!.settings["enabledCube"]  = if (this.numberMaster!!.status["useCubeMode"] == 1.toString()) {
            1.toString()
        } else {
            0.toString()
        }
        this.numberMaster!!.numberMasterRenderer!!.changeTexture(status["size"]!!.toInt())
        for (key in listOf("swipe_bottom", "swipe_left", "swipe_right", "swipe_top")) {
            this.numberMaster!!.buttons[key]!!.apply {
                if (that.numberMaster!!.status["useCubeMode"] == 1.toString()) {
                    isEnabled = true
                    visibility = ImageButton.VISIBLE
                } else {
                    isEnabled = false
                    visibility = ImageButton.INVISIBLE
                }
            }
        }
        this.numberMaster!!.updateNumberPanel()
    }
}