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
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.content.res.Resources
import android.graphics.*

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool

import android.opengl.GLSurfaceView
import android.os.Handler
import android.os.Looper
import android.view.*
import android.widget.*
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
    var statusPuzzle: MutableList<MutableMap<String, String>> = mutableListOf(
        mutableMapOf(
            "useCubeMode" to "0", // 立方体モード使用中か?
            "blindfoldMode" to "0", // 目隠しモード使用中か？
            "size" to "1", // 現在の面サイズ(1: 3x3 | 2: 6x6 | 3: 9x9)
            "cubeSideNumber" to "0", // 立方体面番号
        ),
        mutableMapOf(
            "useCubeMode" to "0", // 立方体モード使用中か?
            "blindfoldMode" to "0", // 目隠しモード使用中か？
            "size" to "1", // 現在の面サイズ(1: 3x3 | 2: 6x6 | 3: 9x9)
            "cubeSideNumber" to "0", // 立方体面番号
        ),
    )
    var statusGame: MutableMap<String, String> = mutableMapOf(
        "simulMode" to "0",
        "stop" to "1", // STOP中か?
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
        "number_question" to BitmapFactory.decodeResource(this.resources, R.drawable.number_question),
        "effect_magic_square" to BitmapFactory.decodeResource(this.resources, R.drawable.effect_magic_square),
        "effect_puzzle3x3" to BitmapFactory.decodeResource(this.resources, R.drawable.effect_puzzle3x3),
        "effect_puzzle6x6" to BitmapFactory.decodeResource(this.resources, R.drawable.effect_puzzle6x6),
        "effect_puzzle9x9" to BitmapFactory.decodeResource(this.resources, R.drawable.effect_puzzle9x9),
    )

    var statusText: TextView? = null

    var buttonsGame: MutableMap<String, ImageButton?> = mutableMapOf(
        "prev" to null,
        "3x3" to null,
        "6x6" to null,
        "9x9" to null,
        "secret" to null,
        "cube" to null,
        "blindfold" to null,
        "finish" to null,
        "stop" to null,
    )

    var nonNumberPanelPosition: MutableList<MutableMap<String, Int>> = mutableListOf(
        mutableMapOf("cubeSideNumber" to 0, "x" to 2, "y" to 2),
        mutableMapOf("cubeSideNumber" to 0, "x" to 2, "y" to 2),
    )
    var boardStandLayout: MutableList<FrameLayout?> = mutableListOf(
        null,
        null,
    )
    var boardStandForeground: MutableList<ImageView?> = mutableListOf(
        null,
        null,
    )

    // 数字の管理
    // 神ステージの6面 x 9 x 9 = 81 * 6 = 486
    // [神ステージの面番号][y位置][x位置]
    // 面番号は初期表示面が0で、それを中心に上から時計回りで、上1、右2、下3、左4、初期表示面の裏側が5
    // x位置、y位置は左上が0, 0
    var numbers = mutableListOf(
        MutableList(6) {MutableList(9) {MutableList(9) {0} } },
        MutableList(6) {MutableList(9) {MutableList(9) {0} } },
    )

    // 数字盤のImageViewの変数
    var numberPanels: MutableList<MutableList<ImageView?>> = mutableListOf(
        MutableList(4) {null},
        MutableList(4) {null},
    )

    var buttonsPuzzle: MutableList<MutableMap<String, ImageButton?>> = mutableListOf(
        mutableMapOf(
            "swipe_top" to null,
            "swipe_right" to null,
            "swipe_bottom" to null,
            "swipe_left" to null,
        ),
        mutableMapOf(
            "swipe_top" to null,
            "swipe_right" to null,
            "swipe_bottom" to null,
            "swipe_left" to null,
        ),
    )
    var effect: MutableList<ImageView?> = mutableListOf( // 魔方陣
        null,
        null,
    )
    var effect2: MutableList<ImageView?> = mutableListOf( // 稲妻
        null,
        null,
    )
    var cube: MutableList<GLSurfaceView?> = mutableListOf(
        null,
        null,
    )

    // [0]横、[1]縦
    // [*][1]と[*][3]は同じ値でなければならない
    private var cubeNet: MutableList<MutableList<MutableList<Int?>>> = mutableListOf(
        mutableListOf(
            mutableListOf(4, 0, 2, 5),
            mutableListOf(1, 0, 3, 5),
        ),
        mutableListOf(
            mutableListOf(4, 0, 2, 5),
            mutableListOf(1, 0, 3, 5),
        ),
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
                // if (that.status["stop"]!!.toInt() == 1) return false
                if (that.statusPuzzle[0]["useCubeMode"]!!.toInt() != 1) return false
                that.cube[0]!!.visibility = View.VISIBLE
                that.boardStandLayout[0]!!.visibility = FrameLayout.INVISIBLE
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
        val sizeMax = this.numberMasterCalculator.getSizeMax(this.statusPuzzle[0]["size"]!!.toInt())
        this.nonNumberPanelPosition[0] = mutableMapOf("cubeSideNumber" to this.statusPuzzle[0]["cubeSideNumber"]!!.toInt(), "x" to sizeMax, "y" to sizeMax)

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
        this.cubeNet[0] = this.numberMasterCalculator.getNextCubeNet(mouseDirection, this.cubeNet[0])
        this.statusPuzzle[0]["cubeSideNumber"] = this.cubeNet[0][0][1].toString()
    }

    fun numberPanelClickListener(imageView: View) {
        if (this.statusGame["stop"]!!.toInt() == 1) return
        if (!imageView.contentDescription.startsWith("number panel")) return

        val numberPanelSize = this.globalActivityInfo["numberPanelSize:" + this.statusPuzzle[0]["size"]]!!.toFloat()

        // 以下、移動することは明らか
        val that = this

        val clickPanelPosition = mutableMapOf(
            "cubeSideNumber" to this.statusPuzzle[0]["cubeSideNumber"]!!.toInt(),
            "x" to (imageView.translationX / numberPanelSize).roundToInt(),
            "y" to (imageView.translationY / numberPanelSize).roundToInt(),
        )

        val beforeNonNumberPanelPosition = mutableMapOf(
            "cubeSideNumber" to that.nonNumberPanelPosition[0]["cubeSideNumber"]!!.toInt(),
            "y" to that.nonNumberPanelPosition[0]["y"]!!.toInt(),
            "x" to that.nonNumberPanelPosition[0]["x"]!!.toInt(),
        )

        var moveToX = this.nonNumberPanelPosition[0]["x"]!! * numberPanelSize
        var moveToY = this.nonNumberPanelPosition[0]["y"]!! * numberPanelSize
        if (this.nonNumberPanelPosition[0]["cubeSideNumber"]!! != this.statusPuzzle[0]["cubeSideNumber"]!!.toInt()) {
            if (this.nonNumberPanelPosition[0]["x"]!! == clickPanelPosition["x"]) {
                moveToY = if (clickPanelPosition["y"] == 0) {
                    -numberPanelSize
                } else {
                    numberPanelSize * 3
                }
            } else if (this.nonNumberPanelPosition[0]["y"]!! == clickPanelPosition["y"]) {
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
                        that.numbers[0][that.statusPuzzle[0]["cubeSideNumber"]!!.toInt()][clickPanelPosition["y"]!!][clickPanelPosition["x"]!!]
                    that.numbers[0][that.statusPuzzle[0]["cubeSideNumber"]!!.toInt()][clickPanelPosition["y"]!!][clickPanelPosition["x"]!!] =
                        that.numbers[0][that.nonNumberPanelPosition[0]["cubeSideNumber"]!!][that.nonNumberPanelPosition[0]["y"]!!][that.nonNumberPanelPosition[0]["x"]!!]
                    that.numbers[0][that.nonNumberPanelPosition[0]["cubeSideNumber"]!!][that.nonNumberPanelPosition[0]["y"]!!][that.nonNumberPanelPosition[0]["x"]!!] =
                        tmp

                    // 空白枠の更新
                    that.nonNumberPanelPosition[0]["cubeSideNumber"] =
                        that.statusPuzzle[0]["cubeSideNumber"]!!.toInt()
                    that.nonNumberPanelPosition[0]["y"] = clickPanelPosition["y"]!!
                    that.nonNumberPanelPosition[0]["x"] = clickPanelPosition["x"]!!

                    // numberPanelと背景の更新
                    that.updateNumberPanelByMove(beforeNonNumberPanelPosition, clickPanelPosition)

                    // 完成の確認
                    val successType = that.numberMasterCheckSuccess.checkAll(
                        that.numbers[0],
                        that.numberMasterCalculator.getSizeMax(that.statusPuzzle[0]["size"]!!.toInt()),
                        that.statusPuzzle[0]["useCubeMode"]!!.toInt()
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
                this.statusPuzzle[0]["size"]!!.toInt() == 1 -> {
                    this.effect[0]!!.setImageBitmap(this.images["effect_puzzle3x3"]!!)
                }
                this.statusPuzzle[0]["size"]!!.toInt() == 2 -> {
                    this.effect[0]!!.setImageBitmap(this.images["effect_puzzle6x6"]!!)
                }
                this.statusPuzzle[0]["size"]!!.toInt() == 3 -> {
                    this.effect[0]!!.setImageBitmap(this.images["effect_puzzle9x9"]!!)
                }
            }
        } else {
            this.effect[0]!!.setImageBitmap(this.images["effect_magic_square"]!!)
        }

        this.effect[0]!!.visibility = View.VISIBLE
        this.effect[0]!!.stateListAnimator = AnimatorInflater.loadStateListAnimator(activity, R.xml.animate_game_success)

        // スコアの計算
        var addScore = 0
        if (successType == 1) {
            addScore = 10
        } else if (successType == 2 || successType == 3) {
            addScore = 100
        }

        addScore *= this.statusPuzzle[0]["size"]!!.toInt()

        if (this.statusPuzzle[0]["useCubeMode"]!!.toInt() == 1) {
            addScore *= 6
        }

        if (this.statusPuzzle[0]["blindfoldMode"]!!.toInt() == 1) {
            addScore *= 5
        }

        if (successType == 3) {
            addScore *= 2
        }

        if (this.settings["counterStopCount"]!!.toInt() > 1) {
            addScore = floor(addScore / this.settings["counterStopCount"]!!.toDouble()).toInt()
        }

        var newScore: Int = this.statusGame["score"]!!.toInt() + addScore
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

        this.statusGame["score"] = newScore.toString()
        this.updateStatus()
    }

    private fun counterStopEffect() {
        val that = this

        this.effect2[0]!!.visibility = ImageView.VISIBLE
        this.effect2[0]!!.stateListAnimator = AnimatorInflater.loadStateListAnimator(this.activity, R.xml.animate_game_thunder)

        // 上記animation時間100 * 3loop + 100
        timer(name = "thunder", initialDelay = 400, period = 500) {
            Handler(Looper.getMainLooper()).post {
                this.cancel()
                that.effect2[0]!!.setImageResource(R.drawable.thunder2)
                that.effect2[0]!!.stateListAnimator = AnimatorInflater.loadStateListAnimator(that.activity, R.xml.animate_game_thunder)
                timer(name = "thunder", initialDelay = 300, period = 500) {
                    Handler(Looper.getMainLooper()).post {
                        that.effect2[0]!!.setImageResource(R.drawable.thunder1)
                        this.cancel()
                        that.effect2[0]!!.visibility = ImageView.INVISIBLE
                    }
                }
            }
        }
    }

    private fun changeButtonEnabled(changeStopButton: Boolean = true) {
        // 設定の変更
        // デフォルトでストップ状態
        if (changeStopButton) {
            if (this.statusGame["stop"]!!.toInt() == 0) {
                this.statusGame["stop"] = 1.toString()
                this.buttonsGame["stop"]!!.setImageResource(+R.drawable.button_enabled_stop)

                // simul modeの場合は保存しない
                if (this.statusGame["simulMode"]!!.toInt() == 0) {
                    this.dbHelper!!.writeByStopButton(
                        this.settings,
                        this.statusGame,
                        this.statusPuzzle[0],
                        this.nonNumberPanelPosition[0],
                        this.numbers[0]
                    )
                }
            } else {
                this.statusGame["stop"] = 0.toString()
                this.buttonsGame["stop"]!!.setImageResource(+R.drawable.button_disabled_stop)
            }
        }

        // 他のボタンの有効無効設定
        this.buttonsGame["prev"]!!.isEnabled = !this.buttonsGame["prev"]!!.isEnabled
        for (buttonKey in listOf("finish", "secret", "3x3", "6x6", "9x9")) {
            // ボタンの有効無効設定
            this.buttonsGame[buttonKey]!!.isEnabled = !this.buttonsGame[buttonKey]!!.isEnabled

            // ボタンの画像変更
            var enableString = "_enabled_"
            if (this.statusGame["stop"]!!.toInt() == 0) {
                enableString = "_disabled_"
            }

            var imageName = "button$enableString$buttonKey"
            if (buttonKey == "secret") {
                imageName = if (this.settings["enabledCube"]!!.toInt() == 1) {
                    "button" + enableString + "menu"
                } else {
                    "button_disabled_$buttonKey"
                }
            }

            val id = this.resources.getIdentifier(imageName, "drawable", this.activity.packageName)
            this.buttonsGame[buttonKey]!!.setImageResource(id)
        }
    }

    fun buttonClickStopProcess() {
        this.changeButtonEnabled()
        this.buttonClickSecretProcess(true)

        if (this.statusPuzzle[0]["blindfoldMode"]!!.toInt() == 1) {
            this.updateNumberPanel()
        }
    }

    fun buttonClickFinishProcess() {
        // simul modeの場合は保存しない
        if (this.statusGame["simulMode"]!!.toInt() == 0) {
            // DBに記録を残す
            this.dbHelper!!.writeHistory(
                this.statusGame,
                this.statusPuzzle[0],
                this.nonNumberPanelPosition[0],
                this.numbers[0]
            )
        }

        // ステータスを初期化する
        this.statusGame["score"] = 0.toString()
        this.statusGame["time"] = 0.toString()

        this.updateStatus()
    }

    fun buttonClickSizeProcess(sizeKey: Int) {
        if (this.statusPuzzle[0]["size"]!!.toInt() != sizeKey) {
            this.statusPuzzle[0]["size"] = sizeKey.toString()
            this.numberMasterRenderer!!.changeTexture(sizeKey)
        }

        this.shuffle()
        this.updateNumberPanel()
        this.numberMasterOnSwipeTouchListener!!.onSwipeBottom()
    }

    fun buttonClickCubeProcess() {
        if (this.settings["enabledCube"]!!.toInt() == 0) return

        var swipeButtonEnabled = true
        var swipeButtonVisibility = View.VISIBLE
        if (this.statusPuzzle[0]["useCubeMode"]!!.toInt() == 1) {
            this.statusPuzzle[0]["useCubeMode"] = 0.toString()
            this.statusPuzzle[0]["cubeSideNumber"] = 0.toString()
            swipeButtonEnabled = false
            swipeButtonVisibility = View.INVISIBLE
        } else {
            this.statusPuzzle[0]["useCubeMode"] = 1.toString()
        }

        for (buttonName in listOf("swipe_top", "swipe_right", "swipe_bottom", "swipe_left")) {
            this.buttonsPuzzle[0][buttonName]!!.isEnabled = swipeButtonEnabled
            this.buttonsPuzzle[0][buttonName]!!.visibility = swipeButtonVisibility
        }

        this.shuffle()
        this.updateNumberPanel()
        this.numberMasterOnSwipeTouchListener!!.onSwipeBottom()
    }

    fun buttonClickBlindfoldProcess() {
        if (this.settings["enabledCube"]!!.toInt() == 0) return

        if (this.statusPuzzle[0]["blindfoldMode"]!!.toInt() == 0) {
            this.statusPuzzle[0]["blindfoldMode"] = 1.toString()
        } else {
            this.statusPuzzle[0]["blindfoldMode"] = 0.toString()
            this.updateNumberPanel()
        }
    }

    fun buttonClickSecretProcess(forceClose: Boolean = false) {
        if (this.settings["enabledCube"]!!.toInt() == 0) return

        val that = this
        this.activity.findViewById<RelativeLayout>(R.id.button_container).apply {
            if (this.resources.configuration.orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                if (layoutParams.height == (that.globalActivityInfo["meta:otherSize"]!!.toFloat() * 4).toInt()
                    || forceClose) {
                    updateLayoutParams {
                        height =
                            (that.globalActivityInfo["meta:otherSize"]!!.toFloat() * 1).toInt()
                    }
                } else {
                    updateLayoutParams {
                        height =
                            (that.globalActivityInfo["meta:otherSize"]!!.toFloat() * 4).toInt()
                    }
                }
            } else {
                if (layoutParams.width == (that.globalActivityInfo["meta:otherSize"]!!.toFloat() * 4).toInt()
                    || forceClose) {
                    updateLayoutParams {
                        width =
                            (that.globalActivityInfo["meta:otherSize"]!!.toFloat() * 1).toInt()
                    }
                } else {
                    updateLayoutParams {
                        width =
                            (that.globalActivityInfo["meta:otherSize"]!!.toFloat() * 4).toInt()
                    }
                }
            }
        }
    }

    fun setEvent(window: Window, enableTimer: Boolean = true) {
        val that = this

        // タイマー
        if (enableTimer) {
            timer(name = "timer", period = 1000) {
                if (that.statusGame["stop"]!!.toInt() == 1) return@timer
                that.statusGame["time"] = (that.statusGame["time"]!!.toDouble() + 1).toString()
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
                    that.boardStandLayout[0]!!.visibility = FrameLayout.VISIBLE
                    that.cube[0]!!.visibility = View.INVISIBLE

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
        val minutes = (this.statusGame["time"]!!.toDouble() / 60).toInt()
        val seconds = (this.statusGame["time"]!!.toDouble() % 60).toInt()

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
            this.statusGame["score"]!!.toInt(),
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
            this.statusPuzzle[0]["useCubeMode"] = this.dbHelper!!.dataGame["current_game_status"]!!["use_cube_mode"].toString()
            this.statusPuzzle[0]["blindfoldMode"] = this.dbHelper!!.dataGame["current_game_status"]!!["blindfold_mode"].toString()
            this.statusPuzzle[0]["size"] = this.dbHelper!!.dataGame["current_game_status"]!!["size"].toString()
            this.statusPuzzle[0]["cubeSideNumber"] = this.dbHelper!!.dataGame["current_game_status"]!!["cube_side_number"].toString()
            this.statusGame["score"] = this.dbHelper!!.dataGame["current_game_status"]!!["score"].toString()
            this.statusGame["time"] = this.dbHelper!!.dataGame["current_game_status"]!!["time"].toString()

            this.nonNumberPanelPosition[0] = this.dbHelper!!.dataNonNumberPanelPosition
            this.numbers[0] = this.dbHelper!!.dataNumbers

            if (this.statusPuzzle[0]["useCubeMode"]!!.toInt() == 1) {
                for (buttonName in listOf("swipe_top", "swipe_right", "swipe_bottom", "swipe_left")) {
                    this.buttonsPuzzle[0][buttonName]!!.isEnabled = true
                    this.buttonsPuzzle[0][buttonName]!!.visibility = View.VISIBLE
                }
            }

            this.numberMasterRenderer!!.changeTexture(this.statusPuzzle[0]["size"]!!.toInt())

            this.updateStatus()

            return true
        }
        return false
    }

    fun shuffle() {
        this.numbers[0] = this.numberMasterCalculator.shuffleNumbers(
            this.statusPuzzle[0]["useCubeMode"]!!.toInt(),
            this.statusPuzzle[0]["size"]!!.toInt()
        )
        this.nonNumberPanelPosition[0] = this.numberMasterCalculator.getRandom9Position(
            this.statusPuzzle[0]["useCubeMode"]!!.toInt(),
            this.statusPuzzle[0]["size"]!!.toInt(),
            this.numbers[0]
        )

        this.dbHelper!!.writeGameStartNumbers(this.nonNumberPanelPosition[0], this.numbers[0])
    }

    private fun updateNumberPanelByMove(beforeNonNumberPanelPosition: MutableMap<String, Int>, afterNonNumberPanelPosition: MutableMap<String, Int>) {
        val sizeMax = this.numberMasterCalculator.getSizeMax(this.statusPuzzle[0]["size"]!!.toInt())

        val bitmap = Bitmap.createBitmap(
            this.boardStandLayout[0]!!.background.toBitmap(
                this.boardStandLayout[0]!!.width,
                this.boardStandLayout[0]!!.height,
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
                this.numbers[0][this.statusPuzzle[0]["cubeSideNumber"]!!.toInt()][y][x],
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
                this.numbers[0][this.statusPuzzle[0]["cubeSideNumber"]!!.toInt()][y][x],
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
                this.numbers[0][this.statusPuzzle[0]["cubeSideNumber"]!!.toInt()][y][x],
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
                this.numbers[0][this.statusPuzzle[0]["cubeSideNumber"]!!.toInt()][y][x],
                numberPanelViewIndex,
                y,
                x
            )
            numberPanelViewIndex += 1
        }

        for (numberPanelIndex in numberPanelViewIndex until this.numberPanels[0].size) {
            this.numberPanels[0][numberPanelIndex]!!.apply {
                visibility = ImageButton.INVISIBLE
                isEnabled = false
            }
        }

        this.boardStandLayout[0]!!.background = bitmap.toDrawable(this.resources)
    }

    private fun isNumberPanelPosition(currentCubeSideNumber: Int, y: Int, x: Int): Boolean {
        if (this.nonNumberPanelPosition[0]["cubeSideNumber"]!! == currentCubeSideNumber) {
            if (
                this.nonNumberPanelPosition[0]["x"]!! == x
                && this.nonNumberPanelPosition[0]["y"]!! != y
                && this.nonNumberPanelPosition[0]["y"]!! - 1 <= y
                && this.nonNumberPanelPosition[0]["y"]!! + 1 >= y
            ) {
                return true
            }

            if (
                this.nonNumberPanelPosition[0]["y"]!! == y
                && this.nonNumberPanelPosition[0]["x"]!! != x
                && this.nonNumberPanelPosition[0]["x"]!! - 1 <= x
                && this.nonNumberPanelPosition[0]["x"]!! + 1 >= x
            ) {
                return true
            }

            return false
        }

        val sizeMax = this.numberMasterCalculator.getSizeMax(this.statusPuzzle[0]["size"]!!.toInt())

        if (this.nonNumberPanelPosition[0]["x"]!! == sizeMax) {
            if (
                x == 0
                && this.nonNumberPanelPosition[0]["y"]!! == y
                && this.nonNumberPanelPosition[0]["cubeSideNumber"]!!
                == this.numberMasterCalculator.getTargetCubeSideNumber(
                    "right",
                    this.cubeNet[0]
                )
            ) {
                return true
            }
        }

        if (this.nonNumberPanelPosition[0]["y"] == sizeMax) {
            if (
                y == 0
                && this.nonNumberPanelPosition[0]["x"]!! == x
                && this.nonNumberPanelPosition[0]["cubeSideNumber"]!!
                == this.numberMasterCalculator.getTargetCubeSideNumber(
                    "bottom",
                    this.cubeNet[0]
                )
            ) {
                return true
            }
        }

        if (this.nonNumberPanelPosition[0]["x"] == 0) {
            if (
                x == sizeMax
                && this.nonNumberPanelPosition[0]["y"]!! == y
                && this.nonNumberPanelPosition[0]["cubeSideNumber"]!!
                == this.numberMasterCalculator.getTargetCubeSideNumber(
                    "left",
                    this.cubeNet[0]
                )
            ) {
                return true
            }
        }

        if (this.nonNumberPanelPosition[0]["y"] == 0) {
            if (
                y == sizeMax
                && this.nonNumberPanelPosition[0]["x"]!! == x
                && this.nonNumberPanelPosition[0]["cubeSideNumber"]!!
                == this.numberMasterCalculator.getTargetCubeSideNumber(
                    "top",
                    this.cubeNet[0]
                )
            ) {
                return true
            }
        }

        return false
    }

    fun updateNumberPanel() {
        var numberPanelViewIndex = 0
        val sizeMax = this.numberMasterCalculator.getSizeMax(this.statusPuzzle[0]["size"]!!.toInt())

        // 数字盤の設定
        val bitmap = Bitmap.createBitmap(
            this.boardStandLayout[0]!!.background.toBitmap(
                this.boardStandLayout[0]!!.width,
                this.boardStandLayout[0]!!.height,
                Bitmap.Config.ARGB_8888
            )
        )
        val canvas = Canvas(bitmap)

        canvas.drawBitmap(
            this.images["board_frame"]!!,
            Rect(0, 0, this.images["board_frame"]!!.width, this.images["board_frame"]!!.height),
            Rect(0, 0, this.boardStandLayout[0]!!.width, this.boardStandLayout[0]!!.height),
            null
        )

        for (yIndex in 0..sizeMax) {
            for (xIndex in 0..sizeMax) {
                if (
                    this.nonNumberPanelPosition[0]["cubeSideNumber"]!! == this.statusPuzzle[0]["cubeSideNumber"]!!.toInt()
                    && this.nonNumberPanelPosition[0]["x"]!! == xIndex
                    && this.nonNumberPanelPosition[0]["y"]!! == yIndex
                ) {
                    this.updateNumberPanelBackgroundOnce(canvas, yIndex, xIndex)
                    continue
                }

                if (this.isNumberPanelPosition(this.statusPuzzle[0]["cubeSideNumber"]!!.toInt(), yIndex, xIndex)) {
                    this.updateNumberPanelButtonOnce(
                        this.numbers[0][this.statusPuzzle[0]["cubeSideNumber"]!!.toInt()][yIndex][xIndex],
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

        for (numberPanelIndex in numberPanelViewIndex until this.numberPanels[0].size) {
            this.numberPanels[0][numberPanelIndex]!!.apply {
                visibility = ImageButton.INVISIBLE
                isEnabled = false
            }
        }

        this.boardStandLayout[0]!!.background = bitmap.toDrawable(this.resources)
    }

    private fun updateNumberPanelButtonOnce(number: Int, index: Int, y: Int, x: Int) {
        val that = this
        val numberPanelSize =
            this.globalActivityInfo["numberPanelSize:" + this.statusPuzzle[0]["size"]!!]!!.toFloat()

        this.numberPanels[0][index]!!.apply {
            translationX = numberPanelSize * x
            translationY = numberPanelSize * y
            updateLayoutParams {
                width = numberPanelSize.toInt()
                height = numberPanelSize.toInt()
            }

            if (that.statusPuzzle[0]["blindfoldMode"]!!.toInt() == 0 || that.buttonsGame["3x3"]!!.isEnabled) {
                setImageBitmap(that.images["number$number"]!!)
            } else {
                setImageBitmap(that.images["number_question"]!!)
            }
            contentDescription = that.activity.getString(R.string.number_panel_noN, number)
            visibility = ImageButton.VISIBLE
            isEnabled = true
        }
    }

    private fun updateNumberPanelBackgroundOnce(canvas: Canvas, y: Int, x: Int) {
        val numberPanelSize =
            this.globalActivityInfo["numberPanelSize:" + this.statusPuzzle[0]["size"]!!]!!.toFloat()

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
            this.globalActivityInfo["numberPanelSize:" + this.statusPuzzle[0]["size"]!!]!!.toFloat()

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

        val numberPanelImage = if (this.statusPuzzle[0]["blindfoldMode"]!!.toInt() == 0 || this.buttonsGame["3x3"]!!.isEnabled) {
            this.images["number" + this.numbers[0][this.statusPuzzle[0]["cubeSideNumber"]!!.toInt()][y][x]]!!
        } else {
            this.images["number_question"]!!
        }

        canvas.drawBitmap(
            numberPanelImage,
            Rect(0, 0, this.images["number_background"]!!.width, this.images["number_background"]!!.height),
            Rect(startX, startY, endX, endY),
            null
        )
    }
}