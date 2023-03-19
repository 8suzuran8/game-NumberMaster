package com.example.numbermaster

import android.animation.AnimatorInflater
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.PixelFormat
import android.media.AudioManager
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.setPadding
import androidx.core.view.updateLayoutParams
import com.example.numbermaster.databinding.ActivityGameBinding

class GameActivity : NumberMasterActivity() {
    private var numberMaster: NumberMaster? = null
    private var gameMainSize: Int = 0

    private val dialogs: MutableMap<String, AlertDialog.Builder?> = mutableMapOf(
        "3x3" to null,
        "6x6" to null,
        "9x9" to null,
        "cube" to null,
        "finish" to null,
    )

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.base)

        // BGMの音量をスマホのボタンに対応
        volumeControlStream = AudioManager.STREAM_MUSIC

        this.convertIntentExtraToGlobalActivityInfo()
        this.gameMainSize = (this.globalActivityInfo["gameSpaceSize"]!!.toFloat() + (this.globalActivityInfo["meta:otherSize"]!!.toFloat().toInt() * 2)).toInt()

        val that = this

        val inflateRootLayout = findViewById<FrameLayout>(R.id.root_layout)
        val activityLayout = layoutInflater.inflate(R.layout.activity_game, inflateRootLayout)
        val layoutBinding: ActivityGameBinding = ActivityGameBinding.bind(activityLayout)

        val layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT).apply {
            setMargins(
                0,
                0,
                0,
                if (that.resources.configuration.orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                    that.globalActivityInfo["meta:otherSize"]!!.toFloat().toInt()
                } else {
                    0
                }
            )
        }
        addContentView(layoutBinding.rootLayout, layoutParams)

        this.hideGame()

        this.dialogs["3x3"] = this.createDialog(R.id.button_3x3)
        this.dialogs["6x6"] = this.createDialog(R.id.button_6x6)
        this.dialogs["9x9"] = this.createDialog(R.id.button_9x9)
        this.dialogs["cube"] = this.createDialog(R.id.button_cube)
        this.dialogs["finish"] = this.createDialog(R.id.button_finish)
    }

    /*
    NumberMaster.invisibleCubeEventの終了時に実行している
    private fun viewGame() {
        findViewById<FrameLayout>(R.id.root_layout).apply {
            z = 1F
        }
        findViewById<FrameLayout>(R.id.base_root_layout).apply {
            z = 0F
        }
    }
     */

    private fun hideGame() {
        findViewById<FrameLayout>(R.id.root_layout).apply {
            z = 0F
        }
        findViewById<FrameLayout>(R.id.base_root_layout).apply {
            z = 1F
        }
    }

    /*
    // thunderエフェクトの確認用の処理
    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_Z) {
            this.numberMaster!!.counterStopEffect()
        }

        return super.onKeyUp(keyCode, event)
    }
     */

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

    fun buttonLongClickListener(view: View) {
        if (this.numberMaster!!.statusGame["stop"]!!.toInt() == 0) return
        if (this.numberMaster!!.settings["counterStopCount"]!!.toInt() == 0) return
        if (this.numberMaster!!.statusGame["simulMode"]!!.toInt() == 0) return

        when (view.id) {
            R.id.button_3x3 -> {
                this.numberMaster!!.buttonClickSizeProcess(1, true)
            }
            R.id.button_6x6 -> {
                this.numberMaster!!.buttonClickSizeProcess(2, true)
            }
            R.id.button_9x9 -> {
                this.numberMaster!!.buttonClickSizeProcess(3, true)
            }
            R.id.button_cube -> {
                this.numberMaster!!.buttonClickCubeProcess(true)
            }
            R.id.button_autoslide -> {
                this.numberMaster!!.buttonClickAutoslideProcess(true)
            }
        }
    }

    fun buttonClickListener(view: View) {
        when (view.id) {
            R.id.prev_button -> {
                if (this.numberMaster!!.statusGame["stop"]!!.toInt() == 1) {
                    this.onFinish()
                    this.finish()
                }
            }
            R.id.button_swipe_top -> {
                val parentIdNumber = this.numberMaster!!.getPuzzleIdNumberBySwipeButton(view)
                this.numberMaster!!.numberMasterOnSwipeTouchListener!!.onSwipeTop(parentIdNumber)
            }
            R.id.button_swipe_right -> {
                val parentIdNumber = this.numberMaster!!.getPuzzleIdNumberBySwipeButton(view)
                this.numberMaster!!.numberMasterOnSwipeTouchListener!!.onSwipeRight(parentIdNumber)
            }
            R.id.button_swipe_bottom -> {
                val parentIdNumber = this.numberMaster!!.getPuzzleIdNumberBySwipeButton(view)
                this.numberMaster!!.numberMasterOnSwipeTouchListener!!.onSwipeBottom(parentIdNumber)
            }
            R.id.button_swipe_left -> {
                val parentIdNumber = this.numberMaster!!.getPuzzleIdNumberBySwipeButton(view)
                this.numberMaster!!.numberMasterOnSwipeTouchListener!!.onSwipeLeft(parentIdNumber)
            }
            R.id.button_3x3 -> {
                this.dialogs["3x3"]!!.show()
            }
            R.id.button_6x6 -> {
                this.dialogs["6x6"]!!.show()
            }
            R.id.button_9x9 -> {
                this.dialogs["9x9"]!!.show()
            }
            R.id.button_cube -> {
                this.dialogs["cube"]!!.show()
            }
            R.id.button_finish -> {
                this.dialogs["finish"]!!.show()
            }
            R.id.button_secret -> {
                this.numberMaster!!.buttonClickSecretProcess()
            }
            R.id.button_autoslide -> {
                this.numberMaster!!.buttonClickAutoslideProcess()
            }
            R.id.button_simul -> {
                val that = this
                if (this.numberMaster!!.statusGame["simulMode"]!!.toInt() == 0) {
                    this.numberMaster!!.statusGame["simulMode"] = 1.toString()
                    findViewById<ConstraintLayout>(R.id.game_main_2).apply {
                        updateLayoutParams {
                            width = that.gameMainSize
                            height = that.gameMainSize
                        }
                    }
                } else {
                    this.numberMaster!!.statusGame["simulMode"] = 0.toString()
                    findViewById<ConstraintLayout>(R.id.game_main_2).apply {
                        updateLayoutParams {
                            width = 0
                            height = 0
                        }
                    }
                }
            }
            R.id.button_semi_blindfold -> {
                this.numberMaster!!.buttonClickSemiBlindfoldProcess()
            }
            R.id.button_blindfold -> {
                this.numberMaster!!.buttonClickBlindfoldProcess()
            }
            R.id.button_stop -> {
                if (!this.numberMaster!!.buttonsGame["3x3"]!!.isEnabled) {
                    this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                } else {
                    this.requestedOrientation = if (this.resources.configuration.orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                        ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                    } else {
                        ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                    }
                }

                this.numberMaster!!.buttonClickStopProcess()
            }
        }
    }

    private fun drawCube(puzzleIdNumber: Int) {
        val that = this

        this.numberMaster!!.cube[puzzleIdNumber] = GLSurfaceView(this).apply {
            setEGLConfigChooser(8, 8, 8, 8, 16, 0)
            setRenderer(that.numberMaster!!.numberMasterRenderer)
            setZOrderOnTop(true)
            contentDescription = "cube"
            holder.setFormat(PixelFormat.TRANSPARENT)
            visibility = View.VISIBLE
        }

        val puzzleId = this.getResourceId(this.numberMaster!!.puzzleIdName[puzzleIdNumber])
        findViewById<ConstraintLayout>(puzzleId).findViewById<RelativeLayout>(R.id.full_space).addView(this.numberMaster!!.cube[puzzleIdNumber])

        // 時間のかかる初期表示を行っておく
        this.numberMaster!!.numberMasterRenderer!!.rotateStart(this.numberMaster!!.numberMasterRenderer!!.rotateDown)
        this.numberMaster!!.invisibleCubeEvent(puzzleIdNumber)
    }

    override fun initialProcess(globalActivityInfo: MutableMap<String, String>, prevButtonAnimation: Boolean) {
        super.initialProcess(globalActivityInfo, false)

        val that = this

        this.numberMaster = NumberMaster(this, resources, this.globalActivityInfo)

        // 音楽とアニメーションを合わせるため、この位置
        findViewById<ImageView>(R.id.prev_button_image).apply {
            stateListAnimator =
                AnimatorInflater.loadStateListAnimator(that, R.xml.animate_all_prev)
        }

        // buttonsの設定
        findViewById<RelativeLayout>(R.id.button_container).apply {
            if (that.resources.configuration.orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                layoutParams.width = that.globalActivityInfo["meta:rootLayoutShort"]!!.toFloat().toInt()
                layoutParams.height =
                    (that.globalActivityInfo["meta:otherSize"]!!.toFloat() * 1).toInt()
            } else {
                layoutParams.width =
                    (that.globalActivityInfo["meta:otherSize"]!!.toFloat() * 1).toInt()
                layoutParams.height =that.globalActivityInfo["meta:rootLayoutShort"]!!.toFloat().toInt()
            }
        }

        for (key in listOf("3x3", "6x6", "9x9", "secret", "autoslide", "simul", "semi_blindfold", "blindfold", "cube", "finish", "stop")) {
            val id = this.getResourceId("button_$key")
            this.numberMaster!!.buttonsGame[key] = findViewById(id)
            this.numberMaster!!.buttonsGame[key]!!.layoutParams.width = that.globalActivityInfo["meta:otherSize"]!!.toFloat().toInt()
            this.numberMaster!!.buttonsGame[key]!!.layoutParams.height = that.globalActivityInfo["meta:otherSize"]!!.toFloat().toInt()
            this.numberMaster!!.buttonsGame[key]!!.isEnabled = true
        }
        this.numberMaster!!.buttonsGame["prev"] = findViewById(R.id.prev_button)

        // longClick
        for (key in listOf("3x3", "6x6", "9x9", "cube", "autoslide")) {
            val id = this.getResourceId("button_$key")
            findViewById<ImageButton>(id).apply {
                setOnTouchListener(object : View.OnTouchListener {
                    private var runLongClickListener = false
                    private val handler = getHandler()

                    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                        if (event?.action == MotionEvent.ACTION_DOWN) {
                            handler.postDelayed({
                                this.runLongClickListener = true
                                that.buttonLongClickListener(v!!)
                            }, 5000L)
                        } else if (event?.action == MotionEvent.ACTION_UP) {
                            handler.removeCallbacksAndMessages(null)
                            if (!this.runLongClickListener) {
                                v!!.performClick()
                            }
                            this.runLongClickListener = false
                        }
                        return true
                    }
                })
            }
        }

        for (i in 0..1) {
            val puzzleId = this.getResourceId(this.numberMaster!!.puzzleIdName[i])
            for (key in listOf("swipe_bottom", "swipe_left", "swipe_right", "swipe_top")) {
                val id = this.getResourceId("button_$key")
                this.numberMaster!!.buttonsPuzzle[i][key] =
                    findViewById<ConstraintLayout>(puzzleId).findViewById(id)

                if (key == "swipe_bottom" || key == "swipe_top") {
                    this.numberMaster!!.buttonsPuzzle[i][key]!!.layoutParams.height =
                        that.globalActivityInfo["meta:otherSize"]!!.toFloat().toInt()
                } else {
                    this.numberMaster!!.buttonsPuzzle[i][key]!!.layoutParams.width =
                        that.globalActivityInfo["meta:otherSize"]!!.toFloat().toInt()
                }
            }

            findViewById<ConstraintLayout>(puzzleId).findViewById<RelativeLayout>(R.id.full_space)
                .apply {
                    layoutParams.width =
                        (that.globalActivityInfo["meta:rootLayoutShort"]!!.toFloat() - (that.globalActivityInfo["meta:otherSize"]!!.toFloat() * 2)).toInt()
                    layoutParams.height =
                        (that.globalActivityInfo["meta:rootLayoutShort"]!!.toFloat() - (that.globalActivityInfo["meta:otherSize"]!!.toFloat() * 2)).toInt()
                }

            findViewById<ConstraintLayout>(puzzleId).findViewById<RelativeLayout>(R.id.layout_middle).apply {
                layoutParams.height = that.globalActivityInfo["gameSpaceSize"]!!.toFloat().toInt()
            }
            // boardStandの設定
            this.numberMaster!!.boardStandLayout[i] =
                findViewById<ConstraintLayout>(puzzleId).findViewById<FrameLayout>(R.id.board_stand_layout).apply {
                    layoutParams.width =
                        that.globalActivityInfo["gameSpaceSize"]!!.toFloat().toInt()
                    layoutParams.height =
                        that.globalActivityInfo["gameSpaceSize"]!!.toFloat().toInt()
                    setPadding(that.globalActivityInfo["boardFrameWidth"]!!.toFloat().toInt())
                }
            this.numberMaster!!.boardStandForeground[i] = findViewById<ConstraintLayout>(puzzleId).findViewById(R.id.board_stand_foreground)

            // 0 is background
            // -1 is autoslide
            for (numberPanelIndex in 1 until this.numberMaster!!.boardStandLayout[i]!!.childCount - 1) {
                this.numberMaster!!.numberPanels[i][numberPanelIndex - 1] =
                    this.numberMaster!!.boardStandLayout[i]!!.getChildAt(numberPanelIndex) as ImageButton
                this.numberMaster!!.numberPanels[i][numberPanelIndex - 1]!!.apply {
                    setOnTouchListener(that.numberMaster!!.numberMasterOnSwipeTouchListener)
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

            // autoslide
            this.numberMaster!!.autoslideImages[i] = findViewById<ConstraintLayout>(puzzleId).findViewById(R.id.autoslide_animate)

            // effectの設定
            this.numberMaster!!.effectSuccess[i] = findViewById<ConstraintLayout>(puzzleId).findViewById<ImageView>(R.id.effect_success).apply {
                visibility = ImageView.INVISIBLE
            }

            // cube
            this.drawCube(i)
            this.numberMaster!!.numberMasterRenderer!!.changeTexture(this.numberMaster!!.statusPuzzle[i]["size"]!!.toInt())
        }

        this.numberMaster!!.effectMultiSuccess = findViewById<ImageView>(R.id.effect_multi_success).apply {
            visibility = ImageView.INVISIBLE
            translationY = 0.toFloat()
        }

        this.numberMaster!!.effectMultiSuccessMagic = findViewById<ImageView>(R.id.effect_multi_success_magic).apply {
            layoutParams.apply {
                width = that.globalActivityInfo["numberPanelSize:1"]!!.toFloat().toInt()
                height = that.globalActivityInfo["numberPanelSize:1"]!!.toFloat().toInt()
            }
            visibility = ImageView.INVISIBLE
            alpha = 0F
        }

        this.numberMaster!!.effectMultiSuccessMagicThunder = findViewById<ImageView>(R.id.effect_multi_success_magic_thunder).apply {
            layoutParams.apply {
                width = that.globalActivityInfo["numberPanelSize:1"]!!.toFloat().toInt()
                height = that.globalActivityInfo["numberPanelSize:1"]!!.toFloat().toInt()
            }
            visibility = ImageView.INVISIBLE
            imageMatrix = this.imageMatrix.apply {
                setScale(
                    0.1F, // @todo デバイスによって大きさが変わるので計算で出さなければならない
                    0.1F,
                    -that.globalActivityInfo["numberPanelSize:1"]!!.toFloat(),
                    -that.globalActivityInfo["numberPanelSize:1"]!!.toFloat()
                )
            }
        }

        this.numberMaster!!.cube[1]!!.visibility = View.INVISIBLE

        this.numberMaster!!.effectCounterStop = findViewById<ImageView>(R.id.effect_counter_stop).apply {
            visibility = ImageView.INVISIBLE
        }

        this.numberMaster!!.setEvent(window)

        // statusの設定
        // loadGameの前で行わなければならない。
        this.numberMaster!!.statusText = findViewById<TextView>(R.id.status).apply {
            layoutParams.height = that.globalActivityInfo["meta:otherSize"]!!.toFloat().toInt()
        }

        if (!this.numberMaster!!.loadGame() || this.numberMaster!!.numbers[0][0][0][0] == 0) {
            this.numberMaster!!.updateStatus()
            this.numberMaster!!.shuffle(0)
            this.numberMaster!!.shuffle(1)
        }
        this.numberMaster!!.updateNumberPanel(0)

        // elseの場合はxmlでOK
        // loadGameの後で行わなければならない。
        if (this.numberMaster!!.settings["enabledCube"]!!.toInt() == 1) {
            val id = R.drawable.button_enabled_menu
            this.numberMaster!!.buttonsGame["secret"]!!.setImageResource(id)
        }

        // ステータスの文字色
        val statusTextColor: Int

        when (this.numberMaster!!.settings["counterStopCount"]!!.toInt()) {
            0 -> {
                // 白
                statusTextColor = Color.argb(255, 255, 255, 255)
            }

            1 -> {
                // 黄
                statusTextColor = Color.argb(255, 218, 165, 32)
            }

            2 -> {
                // 赤
                statusTextColor = Color.argb(255, 218, 112, 214)
            }

            3 -> {
                // 青
                statusTextColor = Color.argb(255, 85, 51, 238)
            }

            else -> {
                // 緑
                statusTextColor = Color.argb(255, 153, 204, 51)
            }
        }

        this.numberMaster!!.statusText!!.apply {
            setTextColor(statusTextColor)
        }

        // simul mode layout
        if (that.resources.configuration.orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            findViewById<LinearLayout>(R.id.game_layout).orientation = LinearLayout.VERTICAL
        } else {
            findViewById<LinearLayout>(R.id.game_layout).orientation =LinearLayout.HORIZONTAL
        }

        findViewById<ConstraintLayout>(R.id.game_main_1).apply {
            layoutParams.height = that.gameMainSize
            layoutParams.width = that.gameMainSize
        }
        findViewById<ConstraintLayout>(R.id.game_main_2).apply {
            layoutParams.height = 0
            layoutParams.width = 0
        }
    }

    private fun createDialog(buttonId: Int): AlertDialog.Builder {
        val that = this

        val messageId = when (buttonId) {
            R.id.button_finish -> R.string.message_complete_this_game
            else -> R.string.message_resize_and_shuffle
        }

        val size = when (buttonId) {
            R.id.button_3x3 -> 1
            R.id.button_6x6 -> 2
            R.id.button_9x9 -> 3
            else -> 1
        }

        return AlertDialog.Builder(this).apply {
            setTitle(R.string.message_title_game)
            setMessage(messageId)
            setPositiveButton(R.string.message_button_text_ok) { _, _ ->
                when (buttonId) {
                    R.id.button_finish -> {
                        that.numberMaster!!.buttonClickFinishProcess()
                    }
                    R.id.button_cube -> {
                        that.numberMaster!!.buttonClickCubeProcess()
                    }
                    else -> {
                        if (that.numberMaster!!.statusGame["simulMode"]!!.toInt() == 0) {
                            that.numberMaster!!.buttonClickSizeProcess(size, true)
                        } else {
                            that.numberMaster!!.buttonClickSizeProcess(size)
                        }
                    }
                }
            }
            setNegativeButton(R.string.message_button_text_cancel) { _, _ ->
            }

            create()
        }
    }
}