package com.example.numbermaster

import android.annotation.SuppressLint
import android.graphics.PixelFormat
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.core.view.setPadding
import com.example.numbermaster.databinding.ActivityCheckBinding

class CheckActivity : NumberMasterActivity() {
    var numberMaster: NumberMaster? = null
    private val puzzleNumber = 0 // simul mode非対応なので0固定

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.base)

        this.convertIntentExtraToGlobalActivityInfo()

        val that = this

        val inflateRootLayout = findViewById<FrameLayout>(R.id.root_layout)
        val activityLayout = layoutInflater.inflate(R.layout.activity_check, inflateRootLayout)
        val layoutBinding: ActivityCheckBinding = ActivityCheckBinding.bind(activityLayout).apply {
            fullSpace.layoutParams.width = (that.globalActivityInfo["meta:rootLayoutShort"]!!.toFloat() - (that.globalActivityInfo["meta:otherSize"]!!.toFloat() * 2)).toInt()
            fullSpace.layoutParams.height = (that.globalActivityInfo["meta:rootLayoutShort"]!!.toFloat() - (that.globalActivityInfo["meta:otherSize"]!!.toFloat() * 2)).toInt()
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
                this.numberMaster!!.numberMasterOnSwipeTouchListener!!.onSwipeTop(this.puzzleNumber)
            }
            R.id.button_swipe_right -> {
                this.numberMaster!!.numberMasterOnSwipeTouchListener!!.onSwipeRight(this.puzzleNumber)
            }
            R.id.button_swipe_bottom -> {
                this.numberMaster!!.numberMasterOnSwipeTouchListener!!.onSwipeBottom(this.puzzleNumber)
            }
            R.id.button_swipe_left -> {
                this.numberMaster!!.numberMasterOnSwipeTouchListener!!.onSwipeLeft(this.puzzleNumber)
            }
        }
    }

    private fun drawCube() {
        val that = this
        this.numberMaster!!.cube[this.puzzleNumber] = GLSurfaceView(this).apply {
            setEGLConfigChooser(8,8,8,8,16,0)
            setRenderer(that.numberMaster!!.numberMasterRenderer)
            setZOrderOnTop(true)
            contentDescription = "cube"
            holder.setFormat(PixelFormat.TRANSPARENT)
            visibility = View.VISIBLE
        }

        findViewById<RelativeLayout>(R.id.full_space).addView(this.numberMaster!!.cube[this.puzzleNumber])

        // 時間のかかる初期表示を行っておく
        this.numberMaster!!.numberMasterRenderer!!.rotateStart(this.numberMaster!!.numberMasterRenderer!!.rotateDown)
        this.numberMaster!!.invisibleCubeEvent(this.puzzleNumber)
    }

    @SuppressLint("DiscouragedApi")
    override fun initialProcess(globalActivityInfo: MutableMap<String, String>, prevButtonAnimation: Boolean) {
        super.initialProcess(globalActivityInfo, prevButtonAnimation)

        val that = this

        this.numberMaster = NumberMaster(this, resources, this.globalActivityInfo)

        // boardStandの設定
        this.numberMaster!!.boardStandLayout[this.puzzleNumber] = findViewById(R.id.board_stand_layout)
        this.numberMaster!!.boardStandForeground[this.puzzleNumber] = findViewById(R.id.board_stand_foreground)

        // 0 is background
        for (numberPanelIndex in 1 until this.numberMaster!!.boardStandLayout[this.puzzleNumber]!!.childCount) {
            this.numberMaster!!.numberPanels[this.puzzleNumber][numberPanelIndex - 1] = this.numberMaster!!.boardStandLayout[this.puzzleNumber]!!.getChildAt(numberPanelIndex) as ImageButton
            this.numberMaster!!.numberPanels[this.puzzleNumber][numberPanelIndex - 1]!!.apply {
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
        this.numberMaster!!.buttonsGame["prev"] = findViewById(R.id.prev_button)

        // cube
        this.drawCube()

        this.numberMaster!!.setEvent(window, false)

        val numbers = intent.getStringExtra("numbers")!!
        val status = this.numberMaster!!.dbHelper!!.makeStatus(numbers)
        this.numberMaster!!.numbers[this.puzzleNumber] = this.numberMaster!!.dbHelper!!.stringToNumbers(numbers)
        this.numberMaster!!.nonNumberPanelPosition[this.puzzleNumber] = this.numberMaster!!.dbHelper!!.dataNonNumberPanelPosition
        this.numberMaster!!.statusPuzzle[this.puzzleNumber]["size"] = status["size"].toString()
        this.numberMaster!!.statusGame["stop"] = 0.toString()
        this.numberMaster!!.statusGame["score"] = 0.toString()
        this.numberMaster!!.statusGame["time"] = 0.toString()
        this.numberMaster!!.statusPuzzle[this.puzzleNumber]["useCubeMode"] = status["use_cube_mode"].toString()
        this.numberMaster!!.settings["enabledCube"]  = if (this.numberMaster!!.statusPuzzle[this.puzzleNumber]["useCubeMode"] == 1.toString()) {
            1.toString()
        } else {
            0.toString()
        }
        this.numberMaster!!.numberMasterRenderer!!.changeTexture(status["size"]!!.toInt())

        for (key in listOf("swipe_bottom", "swipe_left", "swipe_right", "swipe_top")) {
            val id = this.resources.getIdentifier("button_$key", "id", this.packageName)
            this.numberMaster!!.buttonsPuzzle[this.puzzleNumber][key] = findViewById(id)
            if (that.numberMaster!!.statusPuzzle[this.puzzleNumber]["useCubeMode"] == 1.toString()) {
                this.numberMaster!!.buttonsPuzzle[this.puzzleNumber][key]!!.isEnabled = true
                this.numberMaster!!.buttonsPuzzle[this.puzzleNumber][key]!!.visibility = ImageButton.VISIBLE
            } else {
                this.numberMaster!!.buttonsPuzzle[this.puzzleNumber][key]!!.isEnabled = false
                this.numberMaster!!.buttonsPuzzle[this.puzzleNumber][key]!!.visibility = ImageButton.INVISIBLE
            }
        }

        this.numberMaster!!.updateNumberPanel(this.puzzleNumber)
    }
}