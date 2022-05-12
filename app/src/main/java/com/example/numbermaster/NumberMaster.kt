/**
 * 数字師(number master)
 *
 * 15パズルと魔方陣を組み合わせたゲーム
 * サイズは3x3 or 6x6 or 9x9
 * 操作は15パズル
 * 揃えるパターンは15パズル(低スコア)と魔方陣(高スコア)
 * スコアは
 *     (3x3の15パズル) = 1
 *     (6x6の15パズル) = 2
 *     (9x9の15パズル) = 3
 *     (15パズルで連続でなく過去に揃えた形) = 上と同じ
 *     (3x3の魔方陣) = 6
 *     (3x3の魔方陣2パターン目) = 12
 *     (6x6の魔方陣) = 24
 *     (6x6の魔方陣2パターン目) = 48
 *     (9x9の魔方陣) = 72
 *     (9x9の魔方陣2パターン目) = 96
 *     (3x3の魔方陣3パターン目) = 150
 *     (3x3の魔方陣4パターン目) = 200
 *     (6x6の魔方陣3パターン目) = 250
 *     (6x6の魔方陣4パターン目) = 300
 *     (9x9の魔方陣3パターン目) = 350
 *     (9x9の魔方陣4パターン目) = 400
 *     (魔方陣で連続でなく過去に揃えた形) = 上の半分
 * 上部のボタンは
 *     3x3(STOP時のみ押せる)
 *     6x6(STOP時のみ押せる)
 *     9x9(STOP時のみ押せる)
 * 下部のボタンは
 *     SHUFFLE(STOP時のみ押せる)
 *     FINISH(STOP時のみ押せる)
 *     START or STOP
 */

package com.example.numbermaster

import android.animation.*
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.content.res.Resources
import android.graphics.*

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool

import android.opengl.GLSurfaceView
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.*
import android.widget.FrameLayout
import android.widget.ImageButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.drawable.toDrawable
import androidx.core.view.updateLayoutParams
import kotlin.concurrent.timer
import kotlin.math.floor
import kotlin.math.roundToInt

/**
 * NumberMaster*クラスのアクセサー的存在。
 * AndroidStudioの機能からNumberMaster*を使うことはあってはいけない。
 * NumberMaster*クラスはNumberMasterクラス内を綺麗にするために分けただけ。
 *
 * 15パズルの完成はscore+(10 * size)
 * 15パズルで過去に完成させた形と同じ場合はscore+(5 * size)
 * 魔方陣の完成はscore+(100 * size)
 * 魔方陣で過去に完成させた形と同じ場合はscore+(50 * size)
 * useCubeModeの場合のはscore+(n * size * 6)
 * useCubeModeで15パズルと魔方陣が混ざっている場合はscore+(100 * size * 6 * 2)
 */
class NumberMaster constructor(private val activity: AppCompatActivity, private val resources: Resources, private val globalActivityInfo: MutableMap<String, String>) {
    val numberMasterCalculator: NumberMasterCalculator = NumberMasterCalculator()
    var numberMasterRenderer: NumberMasterRenderer? = null
    var numberMasterOnSwipeTouchListener: NumberMasterOnSwipeTouchListener? = null
    private val numberMasterCheckSuccess: NumberMasterCheckSuccess = NumberMasterCheckSuccess()

    // DBがない時はNumberMasterOpenHelperのinitialInsertの値が使われる。(以下は使われない。)
    var settings: MutableMap<String, String> = mutableMapOf(
        "counterStopCount" to "0", // カンスト回数
        "enabledCube" to "0", // 立方体モード使用可能か?(スコアカンスト済みか?)
        "addIconRead" to "1" // 追加機能のHOWTOが既読か？
    )
    var status: MutableMap<String, String> = mutableMapOf(
        "useCubeMode" to "0", // 立方体モード使用中か?
        "stop" to "1", // STOP中か?
        "size" to "1", // 現在の面サイズ(1: 3x3 | 2: 6x6 | 3: 9x9)
        "cubeSideNumber" to "0", // 立方体面番号
        "score" to "0",
        "time" to "0",
    )

    private val images = mutableMapOf(
        "board_frame" to BitmapFactory.decodeResource(this.resources, R.drawable.board_frame),
        "board_stand_once" to BitmapFactory.decodeResource(this.resources, R.drawable.board_stand_once),
        "number_background" to BitmapFactory.decodeResource(this.resources, R.drawable.number_background),
        "number1" to BitmapFactory.decodeResource(this.resources, R.drawable.number1),
        "number2" to BitmapFactory.decodeResource(this.resources, R.drawable.number2),
        "number3" to BitmapFactory.decodeResource(this.resources, R.drawable.number3),
        "number4" to BitmapFactory.decodeResource(this.resources, R.drawable.number4),
        "number5" to BitmapFactory.decodeResource(this.resources, R.drawable.number5),
        "number6" to BitmapFactory.decodeResource(this.resources, R.drawable.number6),
        "number7" to BitmapFactory.decodeResource(this.resources, R.drawable.number7),
        "number8" to BitmapFactory.decodeResource(this.resources, R.drawable.number8),
        "number9" to BitmapFactory.decodeResource(this.resources, R.drawable.number9),
        "effect_magic_square" to BitmapFactory.decodeResource(this.resources, R.drawable.effect_magic_square),
        "effect_puzzle3x3" to BitmapFactory.decodeResource(this.resources, R.drawable.effect_puzzle3x3),
        "effect_puzzle6x6" to BitmapFactory.decodeResource(this.resources, R.drawable.effect_puzzle6x6),
        "effect_puzzle9x9" to BitmapFactory.decodeResource(this.resources, R.drawable.effect_puzzle9x9),
    )

    var nonNumberPanelPosition: MutableMap<String, Int> = mutableMapOf("cubeSideNumber" to 0, "x" to 2, "y" to 2)
    var statusText: TextView? = null
    var boardStandLayout: FrameLayout? = null
    var boardStandForeground: ImageView? = null

    // 数字の管理
    // 神ステージの6面 x 9 x 9 = 81 * 6 = 486
    // [神ステージの面番号][y位置][x位置]
    // 面番号は初期表示面が0で、それを中心に上から時計回りで、上1、右2、下3、左4、初期表示面の裏側が5
    // x位置、y位置は左上が0, 0
    var numbers = MutableList(6) {MutableList(9) {MutableList(9) {0} } }

    // 数字盤のImageViewの変数
    var numberPanels: MutableList<ImageView?> = MutableList(4) {null}

    var buttons: MutableMap<String, ImageButton?> = mutableMapOf(
        "prev" to null,
        "3x3" to null,
        "6x6" to null,
        "9x9" to null,
        "secret" to null,
        "finish" to null,
        "stop" to null,
        "swipe_top" to null,
        "swipe_right" to null,
        "swipe_bottom" to null,
        "swipe_left" to null,
    )
    var effect: ImageView? = null // 魔方陣
    var effect2: ImageView? = null // 稲妻
    var cube: GLSurfaceView? = null

    // [0]横、[1]縦
    // [*][1]と[*][3]は同じ値でなければならない
    private var cubeNet: MutableList<MutableList<Int?>> = mutableListOf(
        mutableListOf(4, 0, 2, 5),
        mutableListOf(1, 0, 3, 5),
    )

    // 音楽、効果音
    private var soundPool: SoundPool? = null
    private var soundMove = 0
    private var soundSuccess = 0
    private var soundCounterStop = 0
    var bgmMediaPlayer: MediaPlayer? = null

    var dbHelper: NumberMasterOpenHelper? = null

    init {
        val that = this

        that.numberMasterCalculator.numberPanelSize = globalActivityInfo["numberPanelSize:1"]!!.toFloat().toInt()

        // swipe event
        this.numberMasterOnSwipeTouchListener = object: NumberMasterOnSwipeTouchListener(activity) {
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                val result = super.onTouch(v, event)

                if (this.flingResult != true && event.action == MotionEvent.ACTION_UP && v.javaClass.name == "androidx.appcompat.widget.AppCompatImageButton") {
                    that.numberPanelClickListener(v)
                } else {
                    v.performClick()
                }

                this.flingResult = false
                return result
            }
            fun swipeProcess(): Boolean {
                if (that.status["stop"]!!.toInt() == 1) return false
                if (that.status["useCubeMode"]!!.toInt() != 1) return false
                that.cube!!.visibility = View.VISIBLE
                that.boardStandLayout!!.visibility = FrameLayout.INVISIBLE
                that.invisibleCubeEvent()

                return true
            }
            override fun onSwipeTop() {
                if (!this.swipeProcess()) return
                that.setCubeSideNumber("top")
                that.numberMasterRenderer!!.rotateStart(that.numberMasterRenderer!!.rotateUp)
            }
            override fun onSwipeRight() {
                if (!this.swipeProcess()) return
                that.setCubeSideNumber("right")
                that.numberMasterRenderer!!.rotateStart(that.numberMasterRenderer!!.rotateRight)
            }
            override fun onSwipeBottom() {
                if (!this.swipeProcess()) return
                that.setCubeSideNumber("bottom")
                that.numberMasterRenderer!!.rotateStart(that.numberMasterRenderer!!.rotateDown)
            }
            override fun onSwipeLeft() {
                if (!this.swipeProcess()) return
                that.setCubeSideNumber("left")
                that.numberMasterRenderer!!.rotateStart(that.numberMasterRenderer!!.rotateLeft)
            }
        }

        this.numberMasterCheckSuccess.numberMasterCalculator = this.numberMasterCalculator
        val sizeMax = this.numberMasterCalculator.getSizeMax(this.status["size"]!!.toInt())
        this.nonNumberPanelPosition = mutableMapOf("cubeSideNumber" to this.status["cubeSideNumber"]!!.toInt(), "x" to sizeMax, "y" to sizeMax)

        // 3DCG
        this.numberMasterRenderer = NumberMasterRenderer(activity, mutableListOf(R.drawable.texture_3x3, R.drawable.texture_6x6, R.drawable.texture_9x9))

        // DB
        this.dbHelper = NumberMasterOpenHelper(activity)

        /**
         * https://soundeffect-lab.info/sound/anime/
         */
        val audioAttribute = AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_GAME).setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build()
        this.soundPool = SoundPool.Builder().setAudioAttributes(audioAttribute).setMaxStreams(3).build()
        this.soundMove = this.soundPool!!.load(activity, R.raw.sound_move,1)
        this.soundSuccess = this.soundPool!!.load(activity, R.raw.sound_success,1)
        this.soundCounterStop = this.soundPool!!.load(activity, R.raw.sound_counter_stop,1)

        /**
         * https://dova-s.jp
         */
        this.bgmMediaPlayer = MediaPlayer.create(activity, R.raw.bgm001)
        this.bgmMediaPlayer!!.isLooping = true
        this.bgmMediaPlayer!!.start()
    }

    fun setCubeSideNumber(mouseDirection: String) {
        this.cubeNet = this.numberMasterCalculator.getNextCubeNet(mouseDirection, this.cubeNet)
        this.status["cubeSideNumber"] = this.cubeNet[0][1].toString()
    }

    fun numberPanelClickListener(imageView: View) {
        if (this.status["stop"]!!.toInt() == 1) return
        if (!imageView.contentDescription.startsWith("number panel")) return

        val numberPanelSize = this.globalActivityInfo["numberPanelSize:" + this.status["size"]]!!.toFloat()

        // 以下、移動することは明らか
        val that = this

        val clickPanelPosition = mutableMapOf(
            "cubeSideNumber" to this.status["cubeSideNumber"]!!.toInt(),
            "x" to (imageView.translationX / numberPanelSize).roundToInt(),
            "y" to (imageView.translationY / numberPanelSize).roundToInt(),
        )

        val beforeNonNumberPanelPosition = mutableMapOf(
            "cubeSideNumber" to that.nonNumberPanelPosition["cubeSideNumber"]!!.toInt(),
            "y" to that.nonNumberPanelPosition["y"]!!.toInt(),
            "x" to that.nonNumberPanelPosition["x"]!!.toInt(),
        )

        var moveToX = this.nonNumberPanelPosition["x"]!! * numberPanelSize
        var moveToY = this.nonNumberPanelPosition["y"]!! * numberPanelSize
        if (this.nonNumberPanelPosition["cubeSideNumber"]!! != this.status["cubeSideNumber"]!!.toInt()) {
            if (this.nonNumberPanelPosition["x"]!! == clickPanelPosition["x"]) {
                moveToY = if (clickPanelPosition["y"] == 0) {
                    -numberPanelSize
                } else {
                    numberPanelSize * 3
                }
            } else if (this.nonNumberPanelPosition["y"]!! == clickPanelPosition["y"]) {
                moveToX = if (clickPanelPosition["x"] == 0) {
                    -numberPanelSize
                } else {
                    numberPanelSize * 3
                }
            }
        }

        // 移動アニメーション設定
        ObjectAnimator.ofPropertyValuesHolder(
            imageView,
            PropertyValuesHolder.ofFloat("translationX", moveToX),
            PropertyValuesHolder.ofFloat("translationY", moveToY)
        ).apply {
            duration = 100
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)

                    // 数字の場所を入れ替える
                    val tmp =
                        that.numbers[that.status["cubeSideNumber"]!!.toInt()][clickPanelPosition["y"]!!][clickPanelPosition["x"]!!]
                    that.numbers[that.status["cubeSideNumber"]!!.toInt()][clickPanelPosition["y"]!!][clickPanelPosition["x"]!!] =
                        that.numbers[that.nonNumberPanelPosition["cubeSideNumber"]!!][that.nonNumberPanelPosition["y"]!!][that.nonNumberPanelPosition["x"]!!]
                    that.numbers[that.nonNumberPanelPosition["cubeSideNumber"]!!][that.nonNumberPanelPosition["y"]!!][that.nonNumberPanelPosition["x"]!!] =
                        tmp

                    // 空白枠の更新
                    that.nonNumberPanelPosition["cubeSideNumber"] =
                        that.status["cubeSideNumber"]!!.toInt()
                    that.nonNumberPanelPosition["y"] = clickPanelPosition["y"]!!
                    that.nonNumberPanelPosition["x"] = clickPanelPosition["x"]!!

                    // numberPanelと背景の更新
                    that.updateNumberPanelByMove(beforeNonNumberPanelPosition, clickPanelPosition)

                    // 完成の確認
                    val successType = that.numberMasterCheckSuccess.checkAll(
                        that.numbers,
                        that.numberMasterCalculator.getSizeMax(that.status["size"]!!.toInt()),
                        that.status["useCubeMode"]!!.toInt()
                    )

                    if (successType == null) {
                        // 効果音
                        that.soundPool!!.play(that.soundMove, 1F, 1F, 0, 0, 1F)

                        return
                    }

                    that.successProcess(successType)
                }
            })
            start()
        }
    }

    /**
     * 完成のアクション
     * @param successType 1: 15パズル | 2: 魔方陣 | 3: mix(useCubeModeのみ有り得る)
     */
    private fun successProcess(successType: Int) {
        val that = this

        // アニメーション
        if (successType == 1) {
            when {
                this.status["size"]!!.toInt() == 1 -> {
                    this.effect!!.setImageBitmap(this.images["effect_puzzle3x3"]!!)
                }
                this.status["size"]!!.toInt() == 2 -> {
                    this.effect!!.setImageBitmap(this.images["effect_puzzle6x6"]!!)
                }
                this.status["size"]!!.toInt() == 3 -> {
                    this.effect!!.setImageBitmap(this.images["effect_puzzle9x9"]!!)
                }
            }
        } else {
            this.effect!!.setImageBitmap(this.images["effect_magic_square"]!!)
        }

        this.effect!!.visibility = View.VISIBLE
        this.effect!!.stateListAnimator = AnimatorInflater.loadStateListAnimator(activity, R.xml.animate_game_success)

        // スコアの計算
        var addScore = 0
        if (successType == 1) {
            addScore = 10
        } else if (successType == 2 || successType == 3) {
            addScore = 100
        }

        addScore *= this.status["size"]!!.toInt()

        if (this.status["useCubeMode"]!!.toInt() == 1) {
            addScore *= 6
        }

        if (successType == 3) {
            addScore *= 2
        }

        if (this.settings["counterStopCount"]!!.toInt() > 1) {
            addScore = floor(addScore / this.settings["counterStopCount"]!!.toDouble()).toInt()
        }

        var newScore: Int = this.status["score"]!!.toInt() + addScore
        if (newScore > 99999) {
            newScore -= 99999
            this.settings["counterStopCount"] = (this.settings["counterStopCount"]!!.toInt() + 1).toString()
            this.settings["enabledCube"] = 1.toString()
            this.settings["addIconRead"] = 0.toString()
            this.counterStopEffect()

            // 効果音
            that.soundPool!!.play(that.soundCounterStop, 1F, 1F, 0, 0, 1F)
        } else {
            // 効果音
            this.soundPool!!.play(this.soundSuccess, 1F, 1F, 0, 0, 1F)
        }

        this.status["score"] = newScore.toString()
        this.updateStatus()
    }

    private fun counterStopEffect() {
        val that = this

        this.effect2!!.visibility = ImageView.VISIBLE
        this.effect2!!.stateListAnimator = AnimatorInflater.loadStateListAnimator(this.activity, R.xml.animate_game_thunder)

        // 上記animation時間100 * 3loop + 100
        timer(name = "thunder", initialDelay = 400, period = 500) {
            Handler(Looper.getMainLooper()).post {
                this.cancel()
                that.effect2!!.setImageResource(R.drawable.thunder2)
                that.effect2!!.stateListAnimator = AnimatorInflater.loadStateListAnimator(that.activity, R.xml.animate_game_thunder)
                timer(name = "thunder", initialDelay = 300, period = 500) {
                    Handler(Looper.getMainLooper()).post {
                        that.effect2!!.setImageResource(R.drawable.thunder1)
                        this.cancel()
                        that.effect2!!.visibility = ImageView.INVISIBLE
                    }
                }
            }
        }
    }

    private fun changeButtonEnabled(changeStopButton: Boolean = true) {
        // 設定の変更
        // デフォルトでストップ状態
        if (changeStopButton) {
            if (this.status["stop"]!!.toInt() == 0) {
                this.status["stop"] = 1.toString()
                this.buttons["stop"]!!.setImageResource(+R.drawable.button_enabled_stop)

                this.dbHelper!!.writeByStopButton(
                    this.settings,
                    this.status,
                    this.nonNumberPanelPosition,
                    this.numbers
                )
            } else {
                this.status["stop"] = 0.toString()
                this.buttons["stop"]!!.setImageResource(+R.drawable.button_disabled_stop)
            }
        }

        // 他のボタンの有効無効設定
        this.buttons["prev"]!!.isEnabled = !this.buttons["prev"]!!.isEnabled
        for (buttonKey in listOf("finish", "secret", "3x3", "6x6", "9x9")) {
            // ボタンの有効無効設定
            this.buttons[buttonKey]!!.isEnabled = !this.buttons[buttonKey]!!.isEnabled

            // ボタンの画像変更
            var enableString = "_enabled_"
            if (this.status["stop"]!!.toInt() == 0) {
                enableString = "_disabled_"
            }

            var imageName = "button$enableString$buttonKey"
            if (buttonKey == "secret") {
                imageName = if (this.settings["enabledCube"]!!.toInt() == 1) {
                    "button" + enableString + "cube"
                } else {
                    "button_disabled_$buttonKey"
                }
            }

            val id = this.resources.getIdentifier(imageName, "drawable", this.activity.packageName)
            this.buttons[buttonKey]!!.setImageResource(id)
        }
    }

    fun buttonClickStopProcess() {
        this.changeButtonEnabled()

        if (this.buttons["3x3"]!!.isEnabled) {
            this.dbHelper!!.writeByStopButton(
                this.settings,
                this.status,
                this.nonNumberPanelPosition,
                this.numbers
            )
        }
    }

    fun buttonClickFinishProcess() {
        // DBに記録を残す
        this.dbHelper!!.writeHistory(this.status, this.nonNumberPanelPosition, this.numbers)

        // ステータスを初期化する
        this.status["score"] = 0.toString()
        this.status["time"] = 0.toString()

        this.updateStatus()
    }

    fun buttonClickSizeProcess(sizeKey: Int) {
        if (this.status["size"]!!.toInt() != sizeKey) {
            this.status["size"] = sizeKey.toString()
            this.numberMasterRenderer!!.changeTexture(sizeKey)
        }

        this.shuffle()
        this.updateNumberPanel()
        this.numberMasterOnSwipeTouchListener!!.onSwipeBottom()
    }

    fun buttonClickSecretProcess() {
        if (this.settings["enabledCube"]!!.toInt() == 0) return

        var swipeButtonEnabled = true
        var swipeButtonVisibility = View.VISIBLE
        if (this.status["useCubeMode"]!!.toInt() == 1) {
            this.status["useCubeMode"] = 0.toString()
            this.status["cubeSideNumber"] = 0.toString()
            swipeButtonEnabled = false
            swipeButtonVisibility = View.INVISIBLE
        } else {
            this.status["useCubeMode"] = 1.toString()
        }

        for (buttonName in listOf("swipe_top", "swipe_right", "swipe_bottom", "swipe_left")) {
            this.buttons[buttonName]!!.isEnabled = swipeButtonEnabled
            this.buttons[buttonName]!!.visibility = swipeButtonVisibility
        }

        this.shuffle()
        this.updateNumberPanel()
        this.numberMasterOnSwipeTouchListener!!.onSwipeBottom()
    }

    fun setEvent(window: Window, enableTimer: Boolean = true) {
        val that = this

        // タイマー
        if (enableTimer) {
            timer(name = "timer", period = 1000) {
                if (that.status["stop"]!!.toInt() == 1) return@timer
                that.status["time"] = (that.status["time"]!!.toDouble() + 1).toString()
                Handler(Looper.getMainLooper()).post {
                    that.updateStatus()
                }
            }
        }

        // 神モードの回転操作
        window.decorView.setOnTouchListener(this.numberMasterOnSwipeTouchListener)
    }

    // swipe時に呼ばれる
    fun invisibleCubeEvent() {
        val that = this
        timer(name = "invisible_cube_event", period = 10) {
            if (!that.numberMasterRenderer!!.execRotate) {
                this.cancel()
                Handler(Looper.getMainLooper()).post {
                    that.updateNumberPanel()
                    that.boardStandLayout!!.visibility = FrameLayout.VISIBLE
                    that.cube!!.visibility = View.INVISIBLE

                    try {
                        activity.findViewById<FrameLayout>(R.id.root_layout).apply {
                            z = 1F
                        }
                    } catch(exception: Exception) {
                        activity.findViewById<ConstraintLayout>(R.id.root_layout).apply {
                            z = 1F
                        }
                    }
                    activity.findViewById<FrameLayout>(R.id.base_root_layout).apply {
                        z = 0F
                    }
                }
            }
        }
    }

    fun updateStatus() {
        val minutes = (this.status["time"]!!.toDouble() / 60).toInt()
        val seconds = (this.status["time"]!!.toDouble() % 60).toInt()

        var value1 = minutes
        var value2 = seconds
        var unit1 = "m"
        var unit2 = "s"
        if (value1 > 100) {
            unit1 = "h"
            unit2 = "m"

            value1 = (minutes / 60)
            value2 = (minutes % 60)
        }
        this.statusText!!.text = this.activity.getString(
            R.string.status,
            this.status["score"]!!.toInt(),
            value1,
            unit1,
            value2,
            unit2)

        this.statusText!!.visibility = TextView.VISIBLE
    }

    // ゲーム中のデータを読み込み再開する
    // 読み込みに成功したらtrueを返す
    // falseが返ったらnewGameを実行しなければならない
    fun loadGame(): Boolean {
        val result = this.dbHelper!!.loadGame()
        if (result && this.dbHelper!!.exists) {
            this.settings["counterStopCount"] = this.dbHelper!!.dataGame["settings"]!!["counter_stop_count"].toString()
            this.settings["enabledCube"] = this.dbHelper!!.dataGame["settings"]!!["enabled_cube"].toString()
            this.status["useCubeMode"] = this.dbHelper!!.dataGame["current_game_status"]!!["use_cube_mode"].toString()
            this.status["size"] = this.dbHelper!!.dataGame["current_game_status"]!!["size"].toString()
            this.status["cubeSideNumber"] = this.dbHelper!!.dataGame["current_game_status"]!!["cube_side_number"].toString()
            this.status["score"] = this.dbHelper!!.dataGame["current_game_status"]!!["score"].toString()
            this.status["time"] = this.dbHelper!!.dataGame["current_game_status"]!!["time"].toString()

            this.nonNumberPanelPosition = this.dbHelper!!.dataNonNumberPanelPosition
            this.numbers = this.dbHelper!!.dataNumbers

            if (this.status["useCubeMode"]!!.toInt() == 1) {
                for (buttonName in listOf("swipe_top", "swipe_right", "swipe_bottom", "swipe_left")) {
                    this.buttons[buttonName]!!.isEnabled = true
                    this.buttons[buttonName]!!.visibility = View.VISIBLE
                }
            }

            this.numberMasterRenderer!!.changeTexture(this.status["size"]!!.toInt())

            this.updateStatus()

            return true
        }
        return false
    }

    fun shuffle() {
        this.numbers = this.numberMasterCalculator.shuffleNumbers(
            this.status["useCubeMode"]!!.toInt(),
            this.status["size"]!!.toInt()
        )
        this.nonNumberPanelPosition = this.numberMasterCalculator.getRandom9Position(
            this.status["useCubeMode"]!!.toInt(),
            this.status["size"]!!.toInt(),
            this.numbers
        )

        this.dbHelper!!.writeGameStartNumbers(this.nonNumberPanelPosition, this.numbers)
    }

    private fun updateNumberPanelByMove(beforeNonNumberPanelPosition: MutableMap<String, Int>, afterNonNumberPanelPosition: MutableMap<String, Int>) {
        val sizeMax = this.numberMasterCalculator.getSizeMax(this.status["size"]!!.toInt())

        val bitmap = Bitmap.createBitmap(
            this.boardStandLayout!!.background.toBitmap(
                this.boardStandLayout!!.width,
                this.boardStandLayout!!.height,
                Bitmap.Config.ARGB_8888
            )
        )
        val canvas = Canvas(bitmap)

        val translatePaint = Paint()
        translatePaint.color = 0

        // beforeの上下左右を設定
        // 上
        if (beforeNonNumberPanelPosition["y"]!! > 0) {
            this.updateNumberPanelForegroundOnce(
                canvas,
                beforeNonNumberPanelPosition["y"]!! - 1,
                beforeNonNumberPanelPosition["x"]!!
            )
        }
        // 右
        if (beforeNonNumberPanelPosition["x"]!! < sizeMax) {
            this.updateNumberPanelForegroundOnce(
                canvas,
                beforeNonNumberPanelPosition["y"]!!,
                beforeNonNumberPanelPosition["x"]!! + 1
            )
        }
        // 下
        if (beforeNonNumberPanelPosition["y"]!! < sizeMax) {
            this.updateNumberPanelForegroundOnce(
                canvas,
                beforeNonNumberPanelPosition["y"]!! + 1,
                beforeNonNumberPanelPosition["x"]!!
            )
        }
        // 左
        if (beforeNonNumberPanelPosition["x"]!! > 0) {
            this.updateNumberPanelForegroundOnce(
                canvas,
                beforeNonNumberPanelPosition["y"]!!,
                beforeNonNumberPanelPosition["x"]!! - 1
            )
        }

        // afterの上下左右中央を透明に
        // numberPanelsをafterの上下左右に配置
        var y: Int
        var x: Int
        var numberPanelViewIndex = 0
        // 中
        y = afterNonNumberPanelPosition["y"]!!
        x = afterNonNumberPanelPosition["x"]!!
        this.updateNumberPanelBackgroundOnce(canvas, y, x)

        // 上
        if (afterNonNumberPanelPosition["y"]!! > 0) {
            y = afterNonNumberPanelPosition["y"]!! - 1
            x = afterNonNumberPanelPosition["x"]!!
            this.updateNumberPanelBackgroundOnce(canvas, y, x)

            this.updateNumberPanelButtonOnce(
                this.numbers[this.status["cubeSideNumber"]!!.toInt()][y][x],
                numberPanelViewIndex,
                y,
                x
            )
            numberPanelViewIndex += 1
        }
        // 右
        if (afterNonNumberPanelPosition["x"]!! < sizeMax) {
            y = afterNonNumberPanelPosition["y"]!!
            x = afterNonNumberPanelPosition["x"]!! + 1
            this.updateNumberPanelBackgroundOnce(canvas, y, x)

            this.updateNumberPanelButtonOnce(
                this.numbers[this.status["cubeSideNumber"]!!.toInt()][y][x],
                numberPanelViewIndex,
                y,
                x
            )
            numberPanelViewIndex += 1
        }
        // 下
        if (afterNonNumberPanelPosition["y"]!! < sizeMax) {
            y = afterNonNumberPanelPosition["y"]!! + 1
            x = afterNonNumberPanelPosition["x"]!!
            this.updateNumberPanelBackgroundOnce(canvas, y, x)

            this.updateNumberPanelButtonOnce(
                this.numbers[this.status["cubeSideNumber"]!!.toInt()][y][x],
                numberPanelViewIndex,
                y,
                x
            )
            numberPanelViewIndex += 1
        }
        // 左
        if (afterNonNumberPanelPosition["x"]!! > 0) {
            y = afterNonNumberPanelPosition["y"]!!
            x = afterNonNumberPanelPosition["x"]!! - 1
            this.updateNumberPanelBackgroundOnce(canvas, y, x)

            this.updateNumberPanelButtonOnce(
                this.numbers[this.status["cubeSideNumber"]!!.toInt()][y][x],
                numberPanelViewIndex,
                y,
                x
            )
            numberPanelViewIndex += 1
        }

        for (numberPanelIndex in numberPanelViewIndex until this.numberPanels.size) {
            this.numberPanels[numberPanelIndex]!!.apply {
                visibility = ImageButton.INVISIBLE
                isEnabled = false
            }
        }

        this.boardStandLayout!!.background = bitmap.toDrawable(this.resources)
    }

    private fun isNumberPanelPosition(currentCubeSideNumber: Int, y: Int, x: Int): Boolean {
        if (this.nonNumberPanelPosition["cubeSideNumber"]!! == currentCubeSideNumber) {
            if (
                this.nonNumberPanelPosition["x"]!! == x
                && this.nonNumberPanelPosition["y"]!! != y
                && this.nonNumberPanelPosition["y"]!! - 1 <= y
                && this.nonNumberPanelPosition["y"]!! + 1 >= y
            ) {
                return true
            }

            if (
                this.nonNumberPanelPosition["y"]!! == y
                && this.nonNumberPanelPosition["x"]!! != x
                && this.nonNumberPanelPosition["x"]!! - 1 <= x
                && this.nonNumberPanelPosition["x"]!! + 1 >= x
            ) {
                return true
            }

            return false
        }

        val sizeMax = this.numberMasterCalculator.getSizeMax(this.status["size"]!!.toInt())

        if (this.nonNumberPanelPosition["x"]!! == sizeMax) {
            if (
                x == 0
                && this.nonNumberPanelPosition["y"]!! == y
                && this.nonNumberPanelPosition["cubeSideNumber"]!!
                == this.numberMasterCalculator.getTargetCubeSideNumber(
                    "right",
                    this.cubeNet
                )
            ) {
                return true
            }
        }

        if (this.nonNumberPanelPosition["y"] == sizeMax) {
            if (
                y == 0
                && this.nonNumberPanelPosition["x"]!! == x
                && this.nonNumberPanelPosition["cubeSideNumber"]!!
                == this.numberMasterCalculator.getTargetCubeSideNumber(
                    "bottom",
                    this.cubeNet
                )
            ) {
                return true
            }
        }

        if (this.nonNumberPanelPosition["x"] == 0) {
            if (
                x == sizeMax
                && this.nonNumberPanelPosition["y"]!! == y
                && this.nonNumberPanelPosition["cubeSideNumber"]!!
                == this.numberMasterCalculator.getTargetCubeSideNumber(
                    "left",
                    this.cubeNet
                )
            ) {
                return true
            }
        }

        if (this.nonNumberPanelPosition["y"] == 0) {
            if (
                y == sizeMax
                && this.nonNumberPanelPosition["x"]!! == x
                && this.nonNumberPanelPosition["cubeSideNumber"]!!
                == this.numberMasterCalculator.getTargetCubeSideNumber(
                    "top",
                    this.cubeNet
                )
            ) {
                return true
            }
        }

        return false
    }

    fun updateNumberPanel() {
        var numberPanelViewIndex = 0
        val sizeMax = this.numberMasterCalculator.getSizeMax(this.status["size"]!!.toInt())

        // 数字盤の設定
        val bitmap = Bitmap.createBitmap(
            this.boardStandLayout!!.background.toBitmap(
                this.boardStandLayout!!.width,
                this.boardStandLayout!!.height,
                Bitmap.Config.ARGB_8888
            )
        )
        val canvas = Canvas(bitmap)

        canvas.drawBitmap(
            this.images["board_frame"]!!,
            Rect(0, 0, this.images["board_frame"]!!.width, this.images["board_frame"]!!.height),
            Rect(0, 0, this.boardStandLayout!!.width, this.boardStandLayout!!.height),
            null
        )

        for (yIndex in 0..sizeMax) {
            for (xIndex in 0..sizeMax) {
                if (
                    this.nonNumberPanelPosition["cubeSideNumber"]!! == this.status["cubeSideNumber"]!!.toInt()
                    && this.nonNumberPanelPosition["x"]!! == xIndex
                    && this.nonNumberPanelPosition["y"]!! == yIndex
                ) {
                    this.updateNumberPanelBackgroundOnce(canvas, yIndex, xIndex)
                    continue
                }

                if (this.isNumberPanelPosition(this.status["cubeSideNumber"]!!.toInt(), yIndex, xIndex)) {
                    this.updateNumberPanelButtonOnce(
                        this.numbers[this.status["cubeSideNumber"]!!.toInt()][yIndex][xIndex],
                        numberPanelViewIndex,
                        yIndex,
                        xIndex
                    )

                    numberPanelViewIndex += 1

                    this.updateNumberPanelBackgroundOnce(canvas, yIndex, xIndex)
                    continue
                }

                this.updateNumberPanelForegroundOnce(
                    canvas,
                    yIndex,
                    xIndex
                )
            }
        }

        for (numberPanelIndex in numberPanelViewIndex until this.numberPanels.size) {
            this.numberPanels[numberPanelIndex]!!.apply {
                visibility = ImageButton.INVISIBLE
                isEnabled = false
            }
        }

        this.boardStandLayout!!.background = bitmap.toDrawable(this.resources)
    }

    private fun updateNumberPanelButtonOnce(number: Int, index: Int, y: Int, x: Int) {
        val that = this
        val numberPanelSize =
            this.globalActivityInfo["numberPanelSize:" + this.status["size"]!!]!!.toFloat()

        this.numberPanels[index]!!.apply {
            translationX = numberPanelSize * x
            translationY = numberPanelSize * y
            updateLayoutParams {
                width = numberPanelSize.toInt()
                height = numberPanelSize.toInt()
            }
            setImageBitmap(that.images["number$number"]!!)
            contentDescription = that.activity.getString(R.string.number_panel_noN, number)
            visibility = ImageButton.VISIBLE
            isEnabled = true
        }
    }

    private fun updateNumberPanelBackgroundOnce(canvas: Canvas, y: Int, x: Int) {
        val numberPanelSize =
            this.globalActivityInfo["numberPanelSize:" + this.status["size"]!!]!!.toFloat()

        val startX = (numberPanelSize * x + this.globalActivityInfo["boardFrameWidth"]!!.toFloat()).toInt()
        val startY = (numberPanelSize * y + this.globalActivityInfo["boardFrameWidth"]!!.toFloat()).toInt()
        val endX = (startX + numberPanelSize).toInt()
        val endY = (startY + numberPanelSize).toInt()

        canvas.drawBitmap(
            this.images["board_stand_once"]!!,
            Rect(0, 0, this.images["board_stand_once"]!!.width, this.images["board_stand_once"]!!.height),
            Rect(startX, startY, endX, endY),
            null
        )
    }

    private fun updateNumberPanelForegroundOnce(canvas: Canvas, y: Int, x: Int) {
        val numberPanelSize =
            this.globalActivityInfo["numberPanelSize:" + this.status["size"]!!]!!.toFloat()

        val startX = (numberPanelSize * x + this.globalActivityInfo["boardFrameWidth"]!!.toFloat()).toInt()
        val startY = (numberPanelSize * y + this.globalActivityInfo["boardFrameWidth"]!!.toFloat()).toInt()
        val endX = (startX + numberPanelSize).toInt()
        val endY = (startY + numberPanelSize).toInt()

        canvas.drawBitmap(
            this.images["number_background"]!!,
            Rect(0, 0, this.images["number_background"]!!.width, this.images["number_background"]!!.height),
            Rect(startX, startY, endX, endY),
            null
        )

        canvas.drawBitmap(
            this.images["number" + this.numbers[this.status["cubeSideNumber"]!!.toInt()][y][x]]!!,
            Rect(0, 0, this.images["number_background"]!!.width, this.images["number_background"]!!.height),
            Rect(startX, startY, endX, endY),
            null
        )
    }
}