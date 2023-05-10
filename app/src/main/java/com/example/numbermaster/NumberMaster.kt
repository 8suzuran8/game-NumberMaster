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
import android.content.res.Resources
import android.graphics.*
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import android.opengl.GLSurfaceView
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.View
import android.view.Window
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
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
    val puzzleIdName = listOf("game_main_1", "game_main_2")

    // DBがない時はNumberMasterOpenHelperのinitialInsertの値が使われる。(以下は使われない。)
    var settings: MutableMap<String, String> = mutableMapOf(
        "counterStopCount" to "0", // カンスト回数
        "enabledCube" to "0", // 立方体モード使用可能か?(スコアカンスト済みか?)
        "addIconRead" to "1" // 追加機能のHOWTOが既読か？
    )
    var statusPuzzle: MutableList<MutableMap<String, String>> = mutableListOf(
        mutableMapOf(
            "useCubeMode" to "0", // 立方体モード使用中か?
            "blindfoldMode" to "0", // 目隠しモード使用中か？(1:目隠しモード使用中 | 2:一色モード使用中)
            "size" to "1", // 現在の面サイズ(1: 3x3 | 2: 6x6 | 3: 9x9)
            "cubeSideNumber" to "0", // 立方体面番号
            "autoslide" to "0", // 0: 無効 | 1以上: スライドする行番号
        ),
        mutableMapOf(
            "useCubeMode" to "0", // 立方体モード使用中か?
            "blindfoldMode" to "0", // 目隠しモード使用中か？(1:目隠しモード使用中 | 2:一色モード使用中)
            "size" to "1", // 現在の面サイズ(1: 3x3 | 2: 6x6 | 3: 9x9)
            "cubeSideNumber" to "0", // 立方体面番号
            "autoslide" to "0", // 0: 無効 | 1以上: スライドする行番号
        ),
    )
    var statusGame: MutableMap<String, String> = mutableMapOf(
        "simulMode" to "0",
        "stop" to "1", // STOP中か?
        "score" to "0",
        "time" to "0",
        "tapCount" to "0",
    )

    private var images = mutableMapOf(
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
        "effect_multi_success" to BitmapFactory.decodeResource(this.resources, R.drawable.effect_multi_success),
    )

    var statusText: TextView? = null

    var buttonsGame: MutableMap<String, ImageButton?> = mutableMapOf(
        "prev" to null,
        "3x3" to null,
        "6x6" to null,
        "9x9" to null,
        "secret" to null,
        "cube" to null,
        "autoslide" to null,
        "semi_blindfold" to null, // 画像操作事のみ使用
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

    var autoslideImages: MutableList<ImageView?> = mutableListOf(
        null,
        null,
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

    var effectSuccess: MutableList<ImageView?> = mutableListOf( // 魔方陣
        null,
        null,
    )

    private var multiSuccessMaxTime: Double = 40.toDouble() // buttonClickSizeProcessでサイズに応じて変更
    private val multiSuccessMaxCount: Int = 4
    private var lastSuccessNumbers = MutableList(6) {MutableList(9) {MutableList(9) {0} } }
    private var multiSuccessCount: Int = 0
    private var multiSuccessStartTime: Double = 0.toDouble()
    var effectMultiSuccess: ImageView? = null
    var effectMultiSuccessMagic: ImageView? = null
    var effectMultiSuccessMagicThunder: ImageView? = null

    var effectCounterStop: ImageView? = null // 稲妻
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

        val markNumbers = mutableListOf(1, 2, 3, 4, 5, 6, 7, 8, 9)
        markNumbers.shuffle()

        var markIndex = 1
        for (markNumber in markNumbers) {
            this.images["mark$markIndex"] = BitmapFactory.decodeResource(this.resources, this.getResourceDrawable("mark$markNumber"))
            markIndex ++
        }

        // swipe event
        this.numberMasterOnSwipeTouchListener = object: NumberMasterOnSwipeTouchListener(activity) {
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                val result = super.onTouch(v, event)

                if (this.flingResult != true && event.action == MotionEvent.ACTION_UP && v.javaClass.name == "androidx.appcompat.widget.AppCompatImageButton") {
                    val parentLayout = v.parent.parent.parent.parent as ConstraintLayout
                    that.numberPanelClickListener(if (parentLayout.id == R.id.game_main_1) { 0 } else { 1 }, v)
                } else {
                    v.performClick()
                }

                this.flingResult = false
                return result
            }
            fun swipeProcess(puzzleIdNumber: Int): Boolean {
                // if (that.status["stop"]!!.toInt() == 1) return false
                if (that.statusPuzzle[puzzleIdNumber]["useCubeMode"]!!.toInt() != 1) return false
                that.cube[puzzleIdNumber]!!.visibility = View.VISIBLE
                that.boardStandLayout[puzzleIdNumber]!!.visibility = FrameLayout.INVISIBLE
                that.invisibleCubeEvent(puzzleIdNumber)

                return true
            }
            override fun onSwipeTop(puzzleIdNumberDefault: Int, x: Float?, y: Float?) {
                val puzzleIdNumber = that.getPuzzleIdNumberByXy(puzzleIdNumberDefault, x, y)
                if (!this.swipeProcess(puzzleIdNumber)) return
                that.setCubeSideNumber(puzzleIdNumber, "top")
                that.numberMasterRenderer!!.rotateStart(that.numberMasterRenderer!!.rotateUp)
            }
            override fun onSwipeRight(puzzleIdNumberDefault: Int, x: Float?, y: Float?) {
                val puzzleIdNumber = that.getPuzzleIdNumberByXy(puzzleIdNumberDefault, x, y)
                if (!this.swipeProcess(puzzleIdNumber)) return
                that.setCubeSideNumber(puzzleIdNumber, "right")
                that.numberMasterRenderer!!.rotateStart(that.numberMasterRenderer!!.rotateRight)
            }
            override fun onSwipeBottom(puzzleIdNumberDefault: Int, x: Float?, y: Float?) {
                val puzzleIdNumber = that.getPuzzleIdNumberByXy(puzzleIdNumberDefault, x, y)
                if (!this.swipeProcess(puzzleIdNumber)) return
                that.setCubeSideNumber(puzzleIdNumber, "bottom")
                that.numberMasterRenderer!!.rotateStart(that.numberMasterRenderer!!.rotateDown)
            }
            override fun onSwipeLeft(puzzleIdNumberDefault: Int, x: Float?, y: Float?) {
                val puzzleIdNumber = that.getPuzzleIdNumberByXy(puzzleIdNumberDefault, x, y)
                if (!this.swipeProcess(puzzleIdNumber)) return
                that.setCubeSideNumber(puzzleIdNumber,"left")
                that.numberMasterRenderer!!.rotateStart(that.numberMasterRenderer!!.rotateLeft)
            }
        }

        this.numberMasterCheckSuccess.numberMasterCalculator = this.numberMasterCalculator
        val sizeMax = this.numberMasterCalculator.getSizeMax(this.statusPuzzle[0]["size"]!!.toInt())

        for (puzzleIdNumber in 0..1) {
            this.nonNumberPanelPosition[puzzleIdNumber] = mutableMapOf(
                "cubeSideNumber" to this.statusPuzzle[puzzleIdNumber]["cubeSideNumber"]!!.toInt(),
                "x" to sizeMax,
                "y" to sizeMax
            )
        }

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

    fun getPuzzleIdNumberBySwipeButton(v: View): Int {
        val parentId = if (v.id in listOf(R.id.button_swipe_left, R.id.button_swipe_right)) {
            (v.parent.parent.parent as ConstraintLayout).id
        } else {
            (v.parent.parent as ConstraintLayout).id
        }

        return if (parentId == R.id.game_main_1) {
            0
        } else {
            1
        }
    }

    fun getPuzzleIdNumberByXy(puzzleIdNumberDefault: Int, x: Float? = null, y: Float? = null): Int {
        if (this.statusPuzzle[0]["useCubeMode"]!!.toInt() == 0 && this.statusPuzzle[1]["useCubeMode"]!!.toInt() == 0) return puzzleIdNumberDefault
        if (x == null || y == null) return puzzleIdNumberDefault
        if (this.statusGame["simulMode"]!!.toInt() == 0) return puzzleIdNumberDefault

        return if (this.resources.configuration.orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            // 縦
            if (y < (this.globalActivityInfo["meta:rootLayoutLong"]!!.toFloat() / 2)) {
                0
            } else {
                1
            }
        } else {
            if (x < (this.globalActivityInfo["meta:rootLayoutLong"]!!.toFloat() / 2)) {
                0
            } else {
                1
            }
        }
    }

    fun setCubeSideNumber(puzzleIdNumber: Int, mouseDirection: String) {
        this.cubeNet[puzzleIdNumber] = this.numberMasterCalculator.getNextCubeNet(mouseDirection, this.cubeNet[puzzleIdNumber])
        this.statusPuzzle[puzzleIdNumber]["cubeSideNumber"] = this.cubeNet[puzzleIdNumber][0][1].toString()
    }

    fun numberPanelClickListener(puzzleIdNumber: Int, imageView: View) {
        if (this.statusGame["stop"]!!.toInt() == 1) return

        val numberPanelSize = this.globalActivityInfo["numberPanelSize:" + this.statusPuzzle[puzzleIdNumber]["size"]]!!.toFloat()

        // 以下、移動することは明らか
        val that = this

        val clickPanelPosition = mutableMapOf(
            "cubeSideNumber" to this.statusPuzzle[puzzleIdNumber]["cubeSideNumber"]!!.toInt(),
            "x" to (imageView.translationX / numberPanelSize).roundToInt(),
            "y" to (imageView.translationY / numberPanelSize).roundToInt(),
        )

        val beforeNonNumberPanelPosition = mutableMapOf(
            "cubeSideNumber" to that.nonNumberPanelPosition[puzzleIdNumber]["cubeSideNumber"]!!.toInt(),
            "y" to that.nonNumberPanelPosition[puzzleIdNumber]["y"]!!.toInt(),
            "x" to that.nonNumberPanelPosition[puzzleIdNumber]["x"]!!.toInt(),
        )

        var moveToX = this.nonNumberPanelPosition[puzzleIdNumber]["x"]!! * numberPanelSize
        var moveToY = this.nonNumberPanelPosition[puzzleIdNumber]["y"]!! * numberPanelSize
        if (this.nonNumberPanelPosition[puzzleIdNumber]["cubeSideNumber"]!! != this.statusPuzzle[puzzleIdNumber]["cubeSideNumber"]!!.toInt()) {
            if (this.nonNumberPanelPosition[puzzleIdNumber]["x"]!! == clickPanelPosition["x"]) {
                moveToY = if (clickPanelPosition["y"] == 0) {
                    -numberPanelSize
                } else {
                    numberPanelSize * 3
                }
            } else if (this.nonNumberPanelPosition[puzzleIdNumber]["y"]!! == clickPanelPosition["y"]) {
                moveToX = if (clickPanelPosition["x"] == 0) {
                    -numberPanelSize
                } else {
                    numberPanelSize * 3
                }
            }
        }

        var tapCount = this.statusGame["tapCount"]!!.toInt() + 1
        if (tapCount > Int.MAX_VALUE) tapCount = 0
        this.statusGame["tapCount"] = tapCount.toString()
        this.updateStatus()

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
                        that.numbers[puzzleIdNumber][that.statusPuzzle[puzzleIdNumber]["cubeSideNumber"]!!.toInt()][clickPanelPosition["y"]!!][clickPanelPosition["x"]!!]
                    that.numbers[puzzleIdNumber][that.statusPuzzle[puzzleIdNumber]["cubeSideNumber"]!!.toInt()][clickPanelPosition["y"]!!][clickPanelPosition["x"]!!] =
                        that.numbers[puzzleIdNumber][that.nonNumberPanelPosition[puzzleIdNumber]["cubeSideNumber"]!!][that.nonNumberPanelPosition[puzzleIdNumber]["y"]!!][that.nonNumberPanelPosition[puzzleIdNumber]["x"]!!]
                    that.numbers[puzzleIdNumber][that.nonNumberPanelPosition[puzzleIdNumber]["cubeSideNumber"]!!][that.nonNumberPanelPosition[puzzleIdNumber]["y"]!!][that.nonNumberPanelPosition[puzzleIdNumber]["x"]!!] =
                        tmp

                    // 空白枠の更新
                    that.nonNumberPanelPosition[puzzleIdNumber]["cubeSideNumber"] =
                        that.statusPuzzle[puzzleIdNumber]["cubeSideNumber"]!!.toInt()
                    that.nonNumberPanelPosition[puzzleIdNumber]["y"] = clickPanelPosition["y"]!!
                    that.nonNumberPanelPosition[puzzleIdNumber]["x"] = clickPanelPosition["x"]!!

                    // numberPanelと背景の更新
                    that.updateNumberPanelByMove(puzzleIdNumber, beforeNonNumberPanelPosition, clickPanelPosition)

                    // 完成の確認
                    val successType = that.numberMasterCheckSuccess.checkAll(
                        that.numbers[puzzleIdNumber],
                        that.numberMasterCalculator.getSizeMax(that.statusPuzzle[puzzleIdNumber]["size"]!!.toInt()),
                        that.statusPuzzle[puzzleIdNumber]["useCubeMode"]!!.toInt()
                    )

                    if (successType == null) {
                        // 効果音
                        that.soundPool!!.play(that.soundMove, 1F, 1F, 0, 0, 1F)
                        that.autoSlide()
                        return
                    }

                    that.successProcess(puzzleIdNumber, successType)
                }
            })
            start()
        }
    }

    /**
     * 完成のアクション
     * @param successType 1: 15パズル | 2: 魔方陣 | 3: mix(useCubeModeのみ有り得る)
     */
    private fun successProcess(puzzleIdNumber: Int, successType: Int) {
        val that = this

        // アニメーション
        if (successType == 1) {
            when {
                this.statusPuzzle[puzzleIdNumber]["size"]!!.toInt() == 1 -> {
                    this.effectSuccess[puzzleIdNumber]!!.setImageBitmap(this.images["effect_puzzle3x3"]!!)
                }
                this.statusPuzzle[puzzleIdNumber]["size"]!!.toInt() == 2 -> {
                    this.effectSuccess[puzzleIdNumber]!!.setImageBitmap(this.images["effect_puzzle6x6"]!!)
                }
                this.statusPuzzle[puzzleIdNumber]["size"]!!.toInt() == 3 -> {
                    this.effectSuccess[puzzleIdNumber]!!.setImageBitmap(this.images["effect_puzzle9x9"]!!)
                }
            }
        } else {
            this.effectSuccess[puzzleIdNumber]!!.setImageBitmap(this.images["effect_magic_square"]!!)
        }

        this.effectSuccess[puzzleIdNumber]!!.visibility = View.VISIBLE
        this.effectSuccess[puzzleIdNumber]!!.stateListAnimator = AnimatorInflater.loadStateListAnimator(activity, R.xml.animate_game_success)

        // 連続完成アニメーション
        if (this.settings["counterStopCount"]!!.toInt() >= 1
            && this.effectMultiSuccess!!.visibility == View.INVISIBLE
            && this.multiSuccessCount >= this.multiSuccessMaxCount
            && this.multiSuccessStartTime + this.multiSuccessMaxTime > this.statusGame["time"]!!.toDouble()) {
            this.effectMultiSuccess!!.apply {
                visibility = View.VISIBLE
                stateListAnimator = AnimatorInflater.loadStateListAnimator(activity, R.xml.animate_game_multi_success)
            }

            // 魔法の使い分け
            if (successType == 1) {
                when {
                    this.statusPuzzle[puzzleIdNumber]["size"]!!.toInt() == 1 -> {
                        this.effectMultiSuccessMagic!!.backgroundTintList =
                            this.activity.getColorStateList(R.color.effect_multi_success_magic_red)
                    }
                    this.statusPuzzle[puzzleIdNumber]["size"]!!.toInt() == 2 -> {
                        this.effectMultiSuccessMagic!!.backgroundTintList =
                            this.activity.getColorStateList(R.color.effect_multi_success_magic_yellow)
                    }
                    this.statusPuzzle[puzzleIdNumber]["size"]!!.toInt() == 3 -> {
                        this.effectMultiSuccessMagic!!.backgroundTintList =
                            this.activity.getColorStateList(R.color.effect_multi_success_magic_white)
                    }
                }
            } else {
                when {
                    this.statusPuzzle[puzzleIdNumber]["size"]!!.toInt() == 1 -> {
                        this.effectMultiSuccessMagic!!.backgroundTintList =
                            this.activity.getColorStateList(R.color.effect_multi_success_magic_green)
                    }
                    this.statusPuzzle[puzzleIdNumber]["size"]!!.toInt() == 2 -> {
                        this.effectMultiSuccessMagic!!.backgroundTintList =
                            this.activity.getColorStateList(R.color.effect_multi_success_magic_blue)
                    }
                    this.statusPuzzle[puzzleIdNumber]["size"]!!.toInt() == 3 -> {
                        this.effectMultiSuccessMagic!!.backgroundTintList =
                            this.activity.getColorStateList(R.color.effect_multi_success_magic_black)
                    }
                }
            }

            ObjectAnimator.ofPropertyValuesHolder(
                this.effectMultiSuccess!!,
                PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, 0.toFloat(), (-that.globalActivityInfo["gameSpaceSize"]!!.toFloat() / 2))
            ).apply {
                duration = 800
                addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        that.effectMultiSuccess!!.visibility = View.INVISIBLE
                        that.multiSuccessCount = 0
                        that.multiSuccessStartTime = 0.toDouble()
                    }
                    override fun onAnimationStart(animation: Animator) {
                        that.effectMultiSuccessMagic!!.apply {
                            translationY = that.globalActivityInfo["gameSpaceSize"]!!.toFloat()
                            visibility = View.VISIBLE
                            stateListAnimator = AnimatorInflater.loadStateListAnimator(activity, R.xml.animate_game_multi_success_magic)
                        }
                        that.effectMultiSuccessMagicThunder!!.apply {
                            translationY = that.globalActivityInfo["gameSpaceSize"]!!.toFloat()
                            visibility = View.VISIBLE
                            stateListAnimator = AnimatorInflater.loadStateListAnimator(activity, R.xml.animate_game_multi_success_magic_thunder)
                        }
                    }
                })
                start()
            }
        }

        // スコアの計算
        var addScore = 0
        if (successType == 1) {
            addScore = 10
        } else if (successType == 2 || successType == 3) {
            addScore = 100
        }

        addScore *= this.statusPuzzle[puzzleIdNumber]["size"]!!.toInt()

        if (this.statusPuzzle[puzzleIdNumber]["useCubeMode"]!!.toInt() == 1) {
            addScore *= 6
        }

        if (this.statusPuzzle[puzzleIdNumber]["blindfoldMode"]!!.toInt() == 1) {
            addScore *= 5
        } else if (this.statusPuzzle[puzzleIdNumber]["blindfoldMode"]!!.toInt() == 2) {
            addScore *= 4
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

        if (this.multiSuccessStartTime == 0.toDouble()
            || this.multiSuccessStartTime + (this.multiSuccessCount * this.multiSuccessMaxTime / 4) <= this.statusGame["time"]!!.toDouble()) {
            this.multiSuccessCount = 1
            this.multiSuccessStartTime = this.statusGame["time"]!!.toDouble()
        } else {
            if (this.lastSuccessNumbers != this.numbers[puzzleIdNumber]) {
                this.multiSuccessCount += 1
                this.lastSuccessNumbers =
                    this.numberMasterCalculator.copy(this.numbers[puzzleIdNumber])
            } else {
                this.multiSuccessCount = 1
                this.multiSuccessStartTime = this.statusGame["time"]!!.toDouble()
            }
        }
        this.statusGame["score"] = newScore.toString()
        this.updateStatus()
    }

    private fun counterStopEffect() {
        val that = this

        this.effectCounterStop!!.visibility = ImageView.VISIBLE
        this.effectCounterStop!!.stateListAnimator = AnimatorInflater.loadStateListAnimator(this.activity, R.xml.animate_game_thunder)

        // 上記animation時間100 * 3loop + 100
        timer(name = "thunder", initialDelay = 400, period = 500) {
            Handler(Looper.getMainLooper()).post {
                this.cancel()
                that.effectCounterStop!!.setImageResource(R.drawable.thunder2)
                that.effectCounterStop!!.stateListAnimator = AnimatorInflater.loadStateListAnimator(that.activity, R.xml.animate_game_thunder)
                timer(name = "thunder", initialDelay = 300, period = 500) {
                    Handler(Looper.getMainLooper()).post {
                        that.effectCounterStop!!.setImageResource(R.drawable.thunder1)
                        this.cancel()
                        that.effectCounterStop!!.visibility = ImageView.INVISIBLE
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
                this.buttonsGame["stop"]!!.contentDescription = this.activity.getString(R.string.start_button)

                // simul modeの場合は保存しない
                if (this.statusGame["simulMode"]!!.toInt() == 0) {
                    // 保存するのはsimul modeの一つ目
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
                this.buttonsGame["stop"]!!.contentDescription = this.activity.getString(R.string.stop_button)
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

            val id = this.getResourceDrawable(imageName)
            this.buttonsGame[buttonKey]!!.setImageResource(id)
        }
    }

    fun buttonClickStopProcess() {
        this.changeButtonEnabled()
        this.buttonClickSecretProcess(true)

        for (puzzleIdNumber in 0..1) {
            if (this.statusPuzzle[puzzleIdNumber]["blindfoldMode"]!!.toInt() != 0) {
                this.updateNumberPanel(puzzleIdNumber)
            }
        }
    }

    fun buttonClickFinishProcess() {
        // simul modeの場合は保存しない
        if (this.statusGame["simulMode"]!!.toInt() == 0) {
            // DBに記録を残す
            // simul modeの一つ目だけ保存
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
        this.statusGame["tapCount"] = 0.toString()

        this.updateStatus()
    }

    fun buttonClickSizeProcess(sizeKey: Int, onlyOne: Boolean = false) {
        val that = this
        this.multiSuccessMaxTime = (sizeKey * 40).toDouble()
        for (puzzleIdNumber in 0..1) {
            if (onlyOne && puzzleIdNumber == 1) continue

            if (this.statusPuzzle[puzzleIdNumber]["size"]!!.toInt() != sizeKey) {
                this.statusPuzzle[puzzleIdNumber]["size"] = sizeKey.toString()
                this.numberMasterRenderer!!.changeTexture(sizeKey)
            }

            this.shuffle(puzzleIdNumber)
            this.updateNumberPanel(puzzleIdNumber)

            this.numberMasterOnSwipeTouchListener!!.onSwipeBottom(puzzleIdNumber)

            val oneNumberPanelSize = globalActivityInfo["numberPanelSize:" + that.statusPuzzle[puzzleIdNumber]["size"]]!!.toFloat().toInt()
            autoslideImages[puzzleIdNumber]!!.updateLayoutParams{
                height = globalActivityInfo["numberPanelSize:" + that.statusPuzzle[puzzleIdNumber]["size"]]!!.toFloat().toInt()
                width = (that.numberMasterCalculator.getSizeMax(that.statusPuzzle[puzzleIdNumber]["size"]!!.toInt()) + 2) * oneNumberPanelSize
            }
        }
    }

    fun buttonClickCubeProcess(onlyOne: Boolean = false) {
        if (this.settings["enabledCube"]!!.toInt() == 0) return

        var useCubeMode = 1
        var swipeButtonEnabled = true
        var swipeButtonVisibility = View.VISIBLE
        if (this.statusPuzzle[0]["useCubeMode"]!!.toInt() == 1) {
            useCubeMode = 0
            swipeButtonEnabled = false
            swipeButtonVisibility = View.INVISIBLE
        }

        for (puzzleIdNumber in 0..1) {
            if (onlyOne && puzzleIdNumber == 1) continue

            this.statusPuzzle[puzzleIdNumber]["useCubeMode"] = useCubeMode.toString()
            this.statusPuzzle[puzzleIdNumber]["cubeSideNumber"] = 0.toString()

            for (buttonName in listOf("swipe_top", "swipe_right", "swipe_bottom", "swipe_left")) {
                this.buttonsPuzzle[puzzleIdNumber][buttonName]!!.isEnabled = swipeButtonEnabled
                this.buttonsPuzzle[puzzleIdNumber][buttonName]!!.visibility = swipeButtonVisibility
            }

            this.shuffle(puzzleIdNumber)
            this.updateNumberPanel(puzzleIdNumber)

            if (puzzleIdNumber == 0 || this.statusGame["simulMode"]!!.toInt() == 1) {
                this.numberMasterOnSwipeTouchListener!!.onSwipeBottom(puzzleIdNumber)
            }
        }
    }

    fun buttonClickSemiBlindfoldProcess() {
        if (this.settings["enabledCube"]!!.toInt() == 0) return

        for (puzzleIdNumber in 0..1) {
            if (this.statusPuzzle[puzzleIdNumber]["blindfoldMode"]!!.toInt() == 0
                || this.statusPuzzle[puzzleIdNumber]["blindfoldMode"]!!.toInt() == 1) {
                this.statusPuzzle[puzzleIdNumber]["blindfoldMode"] = 2.toString()
            } else {
                this.statusPuzzle[puzzleIdNumber]["blindfoldMode"] = 0.toString()
                this.updateNumberPanel(puzzleIdNumber)
            }
        }
    }

    fun buttonClickBlindfoldProcess() {
        if (this.settings["enabledCube"]!!.toInt() == 0) return

        for (puzzleIdNumber in 0..1) {
            if (this.statusPuzzle[puzzleIdNumber]["blindfoldMode"]!!.toInt() == 0
                || this.statusPuzzle[puzzleIdNumber]["blindfoldMode"]!!.toInt() == 2) {
                this.statusPuzzle[puzzleIdNumber]["blindfoldMode"] = 1.toString()
            } else {
                this.statusPuzzle[puzzleIdNumber]["blindfoldMode"] = 0.toString()
                this.updateNumberPanel(puzzleIdNumber)
            }
        }
    }

    fun buttonClickSecretProcess(forceClose: Boolean = false) {
        if (this.settings["enabledCube"]!!.toInt() == 0) return

        val that = this
        this.activity.findViewById<RelativeLayout>(R.id.button_container).apply {
            if (this.resources.configuration.orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                if (layoutParams.height == (that.globalActivityInfo["meta:otherSize"]!!.toFloat() * 6).toInt()
                    || forceClose) {
                    updateLayoutParams {
                        height =
                            (that.globalActivityInfo["meta:otherSize"]!!.toFloat() * 1).toInt()
                    }
                } else {
                    updateLayoutParams {
                        height =
                            (that.globalActivityInfo["meta:otherSize"]!!.toFloat() * 6).toInt()
                    }
                }
            } else {
                if (layoutParams.width == (that.globalActivityInfo["meta:otherSize"]!!.toFloat() * 6).toInt()
                    || forceClose) {
                    updateLayoutParams {
                        width =
                            (that.globalActivityInfo["meta:otherSize"]!!.toFloat() * 1).toInt()
                    }
                } else {
                    updateLayoutParams {
                        width =
                            (that.globalActivityInfo["meta:otherSize"]!!.toFloat() * 6).toInt()
                    }
                }
            }
        }
    }

    private fun autoSlide() {
        val that = this
        for (puzzleIdNumber in 0..1) {
            if (this.statusPuzzle[puzzleIdNumber]["autoslide"]!!.toInt() > 0) {
                val sizeMax = numberMasterCalculator.getSizeMax(this.statusPuzzle[puzzleIdNumber]["size"]!!.toInt())
                val yIndex = this.statusPuzzle[puzzleIdNumber]["autoslide"]!!.toInt() - 1
                val currentCubeSideNumber = this.statusPuzzle[puzzleIdNumber]["cubeSideNumber"]!!.toInt()

                val newNonNumberPanelPosition = this.nonNumberPanelPosition[puzzleIdNumber].toMutableMap()
                val newNumbers = this.numbers[puzzleIdNumber][currentCubeSideNumber][yIndex].toMutableList()

                // calc next line
                var nextLine = this.statusPuzzle[puzzleIdNumber]["autoslide"]!!.toInt()
                when {
                    this.statusPuzzle[puzzleIdNumber]["size"]!!.toInt() == 1 -> {
                        if (this.statusGame["tapCount"]!!.toInt() % 5 == 0) {
                            nextLine += 1
                            if (nextLine > 3) nextLine = 1
                        }
                    }
                    this.statusPuzzle[puzzleIdNumber]["size"]!!.toInt() == 2 -> {
                        if (this.statusGame["tapCount"]!!.toInt() % 10 == 0) {
                            nextLine += 1
                            if (nextLine > 6) nextLine = 1
                        }
                    }
                    this.statusPuzzle[puzzleIdNumber]["size"]!!.toInt() == 3 -> {
                        if (this.statusGame["tapCount"]!!.toInt() % 20 == 0) {
                            nextLine += 1
                            if (nextLine > 9) nextLine = 1
                        }
                    }
                }

                if (nextLine != this.statusPuzzle[puzzleIdNumber]["autoslide"]!!.toInt()) {
                    val oneNumberPanelSize = globalActivityInfo["numberPanelSize:" + that.statusPuzzle[puzzleIdNumber]["size"]]!!.toFloat().toInt()

                    val bitmap = Bitmap.createBitmap(
                        autoslideImages[puzzleIdNumber]!!.background.toBitmap(
                            this.boardStandLayout[puzzleIdNumber]!!.width + oneNumberPanelSize,
                            oneNumberPanelSize,
                            Bitmap.Config.ARGB_8888
                        )
                    )
                    val canvas = Canvas(bitmap)

                    // set non number panel position animation
                    if (this.nonNumberPanelPosition[puzzleIdNumber]["cubeSideNumber"]!!.toInt() == currentCubeSideNumber
                        && this.nonNumberPanelPosition[puzzleIdNumber]["y"]!!.toInt() == yIndex
                    ) {
                        var startX = this.nonNumberPanelPosition[puzzleIdNumber]["x"]!!.toInt() * oneNumberPanelSize
                        val startY = 0
                        var endX = startX + oneNumberPanelSize
                        val endY = startY + oneNumberPanelSize
                        canvas.drawBitmap(
                            this.images["board_stand_once"]!!,
                            Rect(0, 0, this.images["board_stand_once"]!!.width, this.images["board_stand_once"]!!.height),
                            Rect(
                                startX,
                                startY,
                                endX,
                                endY
                            ),
                            null
                        )

                        if (this.nonNumberPanelPosition[puzzleIdNumber]["x"]!!.toInt() == 0) {
                            startX = (sizeMax + 1) * oneNumberPanelSize
                            endX = startX + oneNumberPanelSize
                            canvas.drawBitmap(
                                this.images["board_stand_once"]!!,
                                Rect(0, 0, this.images["board_stand_once"]!!.width, this.images["board_stand_once"]!!.height),
                                Rect(
                                    startX,
                                    startY,
                                    endX,
                                    endY
                                ),
                                null
                            )
                            newNonNumberPanelPosition["x"] = sizeMax
                        } else {
                            newNonNumberPanelPosition["x"] = this.nonNumberPanelPosition[puzzleIdNumber]["x"]!! - 1
                        }
                    }

                    // set numbers
                    if (this.nonNumberPanelPosition[puzzleIdNumber]["cubeSideNumber"]!!.toInt() != currentCubeSideNumber
                        || this.nonNumberPanelPosition[puzzleIdNumber]["y"]!!.toInt() != yIndex
                        || this.nonNumberPanelPosition[puzzleIdNumber]["x"]!!.toInt() != 0
                    ) {
                        this.updateNumberPanelForegroundOnce(
                            puzzleIdNumber,
                            canvas,
                            0,
                            sizeMax + 1,
                            this.numbers[puzzleIdNumber][currentCubeSideNumber][yIndex][0],
                            0
                        )
                    }
                    newNumbers[sizeMax] = this.numbers[puzzleIdNumber][currentCubeSideNumber][yIndex][0]
                    for (xIndex in 0 .. sizeMax) {
                        if (this.nonNumberPanelPosition[puzzleIdNumber]["cubeSideNumber"]!!.toInt() != currentCubeSideNumber
                            || this.nonNumberPanelPosition[puzzleIdNumber]["y"]!!.toInt() != yIndex
                            || this.nonNumberPanelPosition[puzzleIdNumber]["x"]!!.toInt() != xIndex
                        ) {
                            this.updateNumberPanelForegroundOnce(
                                puzzleIdNumber,
                                canvas,
                                0,
                                xIndex,
                                this.numbers[puzzleIdNumber][currentCubeSideNumber][yIndex][xIndex],
                                0
                            )
                        }

                        if (xIndex > 0) {
                            newNumbers[xIndex - 1] = this.numbers[puzzleIdNumber][currentCubeSideNumber][yIndex][xIndex]
                        }
                    }

                    this.autoslideImages[puzzleIdNumber]!!.background = bitmap.toDrawable(this.resources)
                    autoslideImages[puzzleIdNumber]!!.apply {
                        visibility = ImageView.VISIBLE
                    }
                    ObjectAnimator.ofPropertyValuesHolder(
                        autoslideImages[puzzleIdNumber]!!,
                        PropertyValuesHolder.ofFloat("translationX", -oneNumberPanelSize.toFloat())
                    ).apply {
                        duration = 200
                        addListener(object : AnimatorListenerAdapter() {
                            override fun onAnimationEnd(animation: Animator) {
                                autoslideImages[puzzleIdNumber]!!.apply {
                                    translationX = 0.toFloat()
                                    translationY = ((that.statusPuzzle[puzzleIdNumber]["autoslide"]!!.toInt() - 1) * oneNumberPanelSize).toFloat()
                                    visibility = ImageView.INVISIBLE
                                }
                            }
                        })
                        start()
                    }

                    this.nonNumberPanelPosition[puzzleIdNumber] = newNonNumberPanelPosition.toMutableMap()
                    this.numbers[puzzleIdNumber][currentCubeSideNumber][yIndex] = newNumbers.toMutableList()
                    this.updateNumberPanel(puzzleIdNumber)

                    this.statusPuzzle[puzzleIdNumber]["autoslide"] = nextLine.toString()
                }
            }
        }
    }

    fun buttonClickAutoslideProcess(onlyOne: Boolean = false) {
        val that = this

        val autoslide = if (this.statusPuzzle[0]["autoslide"]!!.toInt() == 0) {
            "1"
        } else {
            "0"
        }

        for (puzzleIdNumber in 0..1) {
            if (onlyOne && puzzleIdNumber == 1) continue

            this.statusPuzzle[puzzleIdNumber]["autoslide"] = autoslide

            val oneNumberPanelSize = globalActivityInfo["numberPanelSize:" + that.statusPuzzle[puzzleIdNumber]["size"]]!!.toFloat().toInt()
            autoslideImages[puzzleIdNumber]!!.updateLayoutParams{
                height = globalActivityInfo["numberPanelSize:" + that.statusPuzzle[puzzleIdNumber]["size"]]!!.toFloat().toInt()
                width = (that.numberMasterCalculator.getSizeMax(that.statusPuzzle[puzzleIdNumber]["size"]!!.toInt()) + 2) * oneNumberPanelSize
            }

            if (autoslide == "1") {
                autoslideImages[puzzleIdNumber]!!.top = 0
                autoslideImages[puzzleIdNumber]!!.apply {
                    translationY = 0.toFloat()
                }
            }
        }

        if (this.statusPuzzle[0]["autoslide"]!! == "1"
            || this.statusPuzzle[1]["autoslide"]!! == "1") {
            this.buttonsGame["autoslide"]!!.setImageResource(+R.drawable.button_enabled_autoslide)
        } else {
            this.buttonsGame["autoslide"]!!.setImageResource(+R.drawable.button_disabled_autoslide)
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
    fun invisibleCubeEvent(puzzleIdNumber: Int) {
        val that = this
        timer(name = "invisible_cube_event", period = 10) {
            if (!that.numberMasterRenderer!!.execRotate) {
                this.cancel()
                Handler(Looper.getMainLooper()).post {
                    that.updateNumberPanel(puzzleIdNumber)
                    that.boardStandLayout[puzzleIdNumber]!!.visibility = FrameLayout.VISIBLE
                    that.cube[puzzleIdNumber]!!.visibility = View.INVISIBLE

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
            unit2,
            this.statusGame["tapCount"]!!.toInt())
        this.statusText!!.contentDescription =
            "score is " +
            this.statusGame["score"]!!.toInt() + "." +
            "time is " +
            value1 +
            unit1 +
            value2 +
            unit2 + "." +
            "tap count is " +
            this.statusGame["tapCount"]!!.toInt() + "."
        this.statusText!!.visibility = TextView.VISIBLE
    }

    // ゲーム中のデータを読み込み再開する
    // 読み込みに成功したらtrueを返す
    // falseが返ったらnewGameを実行しなければならない
    // loadはsimul modeの一つ目だけ
    fun loadGame(): Boolean {
        val result = this.dbHelper!!.loadGame()
        if (result && this.dbHelper!!.exists) {
            this.settings["counterStopCount"] = this.dbHelper!!.dataGame["settings"]!!["counter_stop_count"].toString()
            this.settings["enabledCube"] = this.dbHelper!!.dataGame["settings"]!!["enabled_cube"].toString()
            this.statusGame["score"] = this.dbHelper!!.dataGame["current_game_status"]!!["score"].toString()
            this.statusGame["time"] = this.dbHelper!!.dataGame["current_game_status"]!!["time"].toString()
            this.statusGame["tapCount"] = this.dbHelper!!.dataGame["current_game_status"]!!["tap_count"].toString()

            for (puzzleIdNumber in 0..1) {
                this.statusPuzzle[puzzleIdNumber]["useCubeMode"] =
                    this.dbHelper!!.dataGame["current_game_status"]!!["use_cube_mode"].toString()
                this.statusPuzzle[puzzleIdNumber]["blindfoldMode"] =
                    this.dbHelper!!.dataGame["current_game_status"]!!["blindfold_mode"].toString()
                this.statusPuzzle[puzzleIdNumber]["size"] =
                    this.dbHelper!!.dataGame["current_game_status"]!!["size"].toString()
                this.statusPuzzle[puzzleIdNumber]["cubeSideNumber"] =
                    this.dbHelper!!.dataGame["current_game_status"]!!["cube_side_number"].toString()

                this.nonNumberPanelPosition[puzzleIdNumber] =
                    this.dbHelper!!.dataNonNumberPanelPosition

                if (puzzleIdNumber == 0) {
                    this.numbers[puzzleIdNumber] = this.dbHelper!!.dataNumbers
                } else {
                    this.shuffle(1)
                }

                if (this.statusPuzzle[puzzleIdNumber]["useCubeMode"]!!.toInt() == 1) {
                    for (buttonName in listOf(
                        "swipe_top",
                        "swipe_right",
                        "swipe_bottom",
                        "swipe_left"
                    )) {
                        this.buttonsPuzzle[puzzleIdNumber][buttonName]!!.isEnabled = true
                        this.buttonsPuzzle[puzzleIdNumber][buttonName]!!.visibility = View.VISIBLE
                    }
                }
                this.numberMasterRenderer!!.changeTexture(this.statusPuzzle[puzzleIdNumber]["size"]!!.toInt())
            }


            this.updateStatus()

            return true
        }
        return false
    }

    fun shuffle(puzzleIdNumber: Int) {
        this.numbers[puzzleIdNumber] = this.numberMasterCalculator.shuffleNumbers(
            this.statusPuzzle[puzzleIdNumber]["useCubeMode"]!!.toInt(),
            this.statusPuzzle[puzzleIdNumber]["size"]!!.toInt()
        )
        this.nonNumberPanelPosition[puzzleIdNumber] = this.numberMasterCalculator.getRandom9Position(
            this.statusPuzzle[puzzleIdNumber]["useCubeMode"]!!.toInt(),
            this.statusPuzzle[puzzleIdNumber]["size"]!!.toInt(),
            this.numbers[puzzleIdNumber]
        )

        if (puzzleIdNumber == 0) {
            this.dbHelper!!.writeGameStartNumbers(
                this.nonNumberPanelPosition[puzzleIdNumber],
                this.numbers[puzzleIdNumber]
            )
        }
    }

    private fun updateNumberPanelByMove(puzzleIdNumber: Int, beforeNonNumberPanelPosition: MutableMap<String, Int>, afterNonNumberPanelPosition: MutableMap<String, Int>) {
        val sizeMax = this.numberMasterCalculator.getSizeMax(this.statusPuzzle[puzzleIdNumber]["size"]!!.toInt())

        val bitmap = Bitmap.createBitmap(
            this.boardStandLayout[puzzleIdNumber]!!.background.toBitmap(
                this.boardStandLayout[puzzleIdNumber]!!.width,
                this.boardStandLayout[puzzleIdNumber]!!.height,
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
                puzzleIdNumber,
                canvas,
                beforeNonNumberPanelPosition["y"]!! - 1,
                beforeNonNumberPanelPosition["x"]!!
            )
        }
        // 右
        if (beforeNonNumberPanelPosition["x"]!! < sizeMax) {
            this.updateNumberPanelForegroundOnce(
                puzzleIdNumber,
                canvas,
                beforeNonNumberPanelPosition["y"]!!,
                beforeNonNumberPanelPosition["x"]!! + 1
            )
        }
        // 下
        if (beforeNonNumberPanelPosition["y"]!! < sizeMax) {
            this.updateNumberPanelForegroundOnce(
                puzzleIdNumber,
                canvas,
                beforeNonNumberPanelPosition["y"]!! + 1,
                beforeNonNumberPanelPosition["x"]!!
            )
        }
        // 左
        if (beforeNonNumberPanelPosition["x"]!! > 0) {
            this.updateNumberPanelForegroundOnce(
                puzzleIdNumber,
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
        this.updateNumberPanelBackgroundOnce(puzzleIdNumber, canvas, y, x)

        // 上
        if (afterNonNumberPanelPosition["y"]!! > 0) {
            y = afterNonNumberPanelPosition["y"]!! - 1
            x = afterNonNumberPanelPosition["x"]!!
            this.updateNumberPanelBackgroundOnce(puzzleIdNumber, canvas, y, x)

            this.updateNumberPanelButtonOnce(
                puzzleIdNumber,
                this.numbers[puzzleIdNumber][this.statusPuzzle[puzzleIdNumber]["cubeSideNumber"]!!.toInt()][y][x],
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
            this.updateNumberPanelBackgroundOnce(puzzleIdNumber, canvas, y, x)

            this.updateNumberPanelButtonOnce(
                puzzleIdNumber,
                this.numbers[puzzleIdNumber][this.statusPuzzle[puzzleIdNumber]["cubeSideNumber"]!!.toInt()][y][x],
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
            this.updateNumberPanelBackgroundOnce(puzzleIdNumber, canvas, y, x)

            this.updateNumberPanelButtonOnce(
                puzzleIdNumber,
                this.numbers[puzzleIdNumber][this.statusPuzzle[puzzleIdNumber]["cubeSideNumber"]!!.toInt()][y][x],
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
            this.updateNumberPanelBackgroundOnce(puzzleIdNumber, canvas, y, x)

            this.updateNumberPanelButtonOnce(
                puzzleIdNumber,
                this.numbers[puzzleIdNumber][this.statusPuzzle[puzzleIdNumber]["cubeSideNumber"]!!.toInt()][y][x],
                numberPanelViewIndex,
                y,
                x
            )
            numberPanelViewIndex += 1
        }

        var description = ""

        for (yIndex in 0..sizeMax) {
            description += (yIndex + 1).toString() + " row, from left"
            for (xIndex in 0..sizeMax) {
                if (
                    this.nonNumberPanelPosition[puzzleIdNumber]["cubeSideNumber"]!! == this.statusPuzzle[puzzleIdNumber]["cubeSideNumber"]!!.toInt()
                    && this.nonNumberPanelPosition[puzzleIdNumber]["x"]!! == xIndex
                    && this.nonNumberPanelPosition[puzzleIdNumber]["y"]!! == yIndex
                ) {
                    description += ", space"
                    continue
                }
                description += ", " + this.numbers[puzzleIdNumber][this.statusPuzzle[puzzleIdNumber]["cubeSideNumber"]!!.toInt()][yIndex][xIndex].toString()
            }

            description += " ,"
        }

        this.boardStandLayout[puzzleIdNumber]!!.contentDescription = description

        for (numberPanelIndex in numberPanelViewIndex until this.numberPanels[puzzleIdNumber].size) {
            this.numberPanels[puzzleIdNumber][numberPanelIndex]!!.apply {
                visibility = ImageButton.INVISIBLE
                isEnabled = false
            }
        }

        this.boardStandLayout[puzzleIdNumber]!!.background = bitmap.toDrawable(this.resources)
    }

    private fun isNumberPanelPosition(puzzleIdNumber: Int, currentCubeSideNumber: Int, y: Int, x: Int): Boolean {
        if (this.nonNumberPanelPosition[puzzleIdNumber]["cubeSideNumber"]!! == currentCubeSideNumber) {
            if (
                this.nonNumberPanelPosition[puzzleIdNumber]["x"]!! == x
                && this.nonNumberPanelPosition[puzzleIdNumber]["y"]!! != y
                && this.nonNumberPanelPosition[puzzleIdNumber]["y"]!! - 1 <= y
                && this.nonNumberPanelPosition[puzzleIdNumber]["y"]!! + 1 >= y
            ) {
                return true
            }

            if (
                this.nonNumberPanelPosition[puzzleIdNumber]["y"]!! == y
                && this.nonNumberPanelPosition[puzzleIdNumber]["x"]!! != x
                && this.nonNumberPanelPosition[puzzleIdNumber]["x"]!! - 1 <= x
                && this.nonNumberPanelPosition[puzzleIdNumber]["x"]!! + 1 >= x
            ) {
                return true
            }

            return false
        }

        val sizeMax = this.numberMasterCalculator.getSizeMax(this.statusPuzzle[puzzleIdNumber]["size"]!!.toInt())

        if (this.nonNumberPanelPosition[puzzleIdNumber]["x"]!! == sizeMax) {
            if (
                x == 0
                && this.nonNumberPanelPosition[puzzleIdNumber]["y"]!! == y
                && this.nonNumberPanelPosition[puzzleIdNumber]["cubeSideNumber"]!!
                == this.numberMasterCalculator.getTargetCubeSideNumber(
                    "right",
                    this.cubeNet[puzzleIdNumber]
                )
            ) {
                return true
            }
        }

        if (this.nonNumberPanelPosition[puzzleIdNumber]["y"] == sizeMax) {
            if (
                y == 0
                && this.nonNumberPanelPosition[puzzleIdNumber]["x"]!! == x
                && this.nonNumberPanelPosition[puzzleIdNumber]["cubeSideNumber"]!!
                == this.numberMasterCalculator.getTargetCubeSideNumber(
                    "bottom",
                    this.cubeNet[puzzleIdNumber]
                )
            ) {
                return true
            }
        }

        if (this.nonNumberPanelPosition[puzzleIdNumber]["x"] == 0) {
            if (
                x == sizeMax
                && this.nonNumberPanelPosition[puzzleIdNumber]["y"]!! == y
                && this.nonNumberPanelPosition[puzzleIdNumber]["cubeSideNumber"]!!
                == this.numberMasterCalculator.getTargetCubeSideNumber(
                    "left",
                    this.cubeNet[puzzleIdNumber]
                )
            ) {
                return true
            }
        }

        if (this.nonNumberPanelPosition[puzzleIdNumber]["y"] == 0) {
            if (
                y == sizeMax
                && this.nonNumberPanelPosition[puzzleIdNumber]["x"]!! == x
                && this.nonNumberPanelPosition[puzzleIdNumber]["cubeSideNumber"]!!
                == this.numberMasterCalculator.getTargetCubeSideNumber(
                    "top",
                    this.cubeNet[puzzleIdNumber]
                )
            ) {
                return true
            }
        }

        return false
    }

    fun updateNumberPanel(puzzleIdNumber: Int) {
        var numberPanelViewIndex = 0
        val sizeMax = this.numberMasterCalculator.getSizeMax(this.statusPuzzle[puzzleIdNumber]["size"]!!.toInt())

        // 数字盤の設定
        val bitmap = Bitmap.createBitmap(
            this.boardStandLayout[puzzleIdNumber]!!.background.toBitmap(
                this.boardStandLayout[puzzleIdNumber]!!.width,
                this.boardStandLayout[puzzleIdNumber]!!.height,
                Bitmap.Config.ARGB_8888
            )
        )
        val canvas = Canvas(bitmap)

        canvas.drawBitmap(
            this.images["board_frame"]!!,
            Rect(0, 0, this.images["board_frame"]!!.width, this.images["board_frame"]!!.height),
            Rect(0, 0, this.boardStandLayout[puzzleIdNumber]!!.width, this.boardStandLayout[puzzleIdNumber]!!.height),
            null
        )

        var description = ""

        for (yIndex in 0..sizeMax) {
            description += (yIndex + 1).toString() + " row, from left"
            for (xIndex in 0..sizeMax) {
                if (
                    this.nonNumberPanelPosition[puzzleIdNumber]["cubeSideNumber"]!! == this.statusPuzzle[puzzleIdNumber]["cubeSideNumber"]!!.toInt()
                    && this.nonNumberPanelPosition[puzzleIdNumber]["x"]!! == xIndex
                    && this.nonNumberPanelPosition[puzzleIdNumber]["y"]!! == yIndex
                ) {
                    description += ", space"
                    this.updateNumberPanelBackgroundOnce(puzzleIdNumber, canvas, yIndex, xIndex)
                    continue
                }

                description += ", " + this.numbers[puzzleIdNumber][this.statusPuzzle[puzzleIdNumber]["cubeSideNumber"]!!.toInt()][yIndex][xIndex].toString()

                if (this.isNumberPanelPosition(puzzleIdNumber, this.statusPuzzle[puzzleIdNumber]["cubeSideNumber"]!!.toInt(), yIndex, xIndex)) {
                    this.updateNumberPanelButtonOnce(
                        puzzleIdNumber,
                        this.numbers[puzzleIdNumber][this.statusPuzzle[puzzleIdNumber]["cubeSideNumber"]!!.toInt()][yIndex][xIndex],
                        numberPanelViewIndex,
                        yIndex,
                        xIndex
                    )

                    numberPanelViewIndex += 1

                    this.updateNumberPanelBackgroundOnce(puzzleIdNumber, canvas, yIndex, xIndex)
                    continue
                }

                this.updateNumberPanelForegroundOnce(
                    puzzleIdNumber,
                    canvas,
                    yIndex,
                    xIndex
                )
            }

            description += " ,"
        }

        this.boardStandLayout[puzzleIdNumber]!!.contentDescription = description

        for (numberPanelIndex in numberPanelViewIndex until this.numberPanels[puzzleIdNumber].size) {
            this.numberPanels[puzzleIdNumber][numberPanelIndex]!!.apply {
                visibility = ImageButton.INVISIBLE
                isEnabled = false
            }
        }

        this.boardStandLayout[puzzleIdNumber]!!.background = bitmap.toDrawable(this.resources)
    }

    private fun updateNumberPanelButtonOnce(puzzleIdNumber: Int, number: Int, index: Int, y: Int, x: Int) {
        val that = this
        val numberPanelSize =
            this.globalActivityInfo["numberPanelSize:" + this.statusPuzzle[puzzleIdNumber]["size"]!!]!!.toFloat()

        this.numberPanels[puzzleIdNumber][index]!!.apply {
            translationX = numberPanelSize * x
            translationY = numberPanelSize * y
            updateLayoutParams {
                width = numberPanelSize.toInt()
                height = numberPanelSize.toInt()
            }

            if (that.statusPuzzle[puzzleIdNumber]["blindfoldMode"]!!.toInt() == 0 || that.buttonsGame["3x3"]!!.isEnabled) {
                setImageBitmap(that.images["number$number"]!!)
            } else {
                if (that.statusPuzzle[puzzleIdNumber]["blindfoldMode"]!!.toInt() == 2) {
                    setImageBitmap(that.images["mark$number"]!!)
                } else {
                    setImageBitmap(that.images["number_question"]!!)
                }
            }
            contentDescription = that.activity.getString(R.string.number_panel_noN, number)
            visibility = ImageButton.VISIBLE
            isEnabled = true
        }
    }

    private fun updateNumberPanelBackgroundOnce(puzzleIdNumber: Int, canvas: Canvas, y: Int, x: Int) {
        val numberPanelSize =
            this.globalActivityInfo["numberPanelSize:" + this.statusPuzzle[puzzleIdNumber]["size"]!!]!!.toFloat()

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

    private fun updateNumberPanelForegroundOnce(puzzleIdNumber: Int, canvas: Canvas, y: Int, x: Int, number: Int? = this.numbers[puzzleIdNumber][this.statusPuzzle[puzzleIdNumber]["cubeSideNumber"]!!.toInt()][y][x], margin: Int = this.globalActivityInfo["boardFrameWidth"]!!.toFloat().toInt()) {
        val numberPanelSize =
            this.globalActivityInfo["numberPanelSize:" + this.statusPuzzle[puzzleIdNumber]["size"]!!]!!.toFloat()

        val startX = (numberPanelSize * x + margin).toInt()
        val startY = (numberPanelSize * y + margin).toInt()
        val endX = (startX + numberPanelSize).toInt()
        val endY = (startY + numberPanelSize).toInt()

        canvas.drawBitmap(
            this.images["number_background"]!!,
            Rect(0, 0, this.images["number_background"]!!.width, this.images["number_background"]!!.height),
            Rect(startX, startY, endX, endY),
            null
        )

        val numberPanelImage = if (this.statusPuzzle[puzzleIdNumber]["blindfoldMode"]!!.toInt() == 0 || this.buttonsGame["3x3"]!!.isEnabled) {
            this.images["number$number"]!!
        } else {
            if (this.statusPuzzle[puzzleIdNumber]["blindfoldMode"]!!.toInt() == 2) {
                this.images["mark$number"]!!
            } else {
                this.images["number_question"]!!
            }
        }

        canvas.drawBitmap(
            numberPanelImage,
            Rect(0, 0, this.images["number_background"]!!.width, this.images["number_background"]!!.height),
            Rect(startX, startY, endX, endY),
            null
        )
    }

    private fun getResourceDrawable(name: String): Int {
        when (name) {
            "button_enabled_finish" -> {
                return R.drawable.button_enabled_finish
            }
            "button_disabled_finish" -> {
                return R.drawable.button_disabled_finish
            }
            "button_enabled_secret" -> {
                return R.drawable.button_enabled_menu
            }
            "button_disabled_secret" -> {
                return R.drawable.button_disabled_secret
            }
            "button_enabled_menu" -> {
                return R.drawable.button_enabled_menu
            }
            "button_disabled_menu" -> {
                return R.drawable.button_disabled_menu
            }
            "button_enabled_3x3" -> {
                return R.drawable.button_enabled_3x3
            }
            "button_disabled_3x3" -> {
                return R.drawable.button_disabled_3x3
            }
            "button_enabled_6x6" -> {
                return R.drawable.button_enabled_6x6
            }
            "button_disabled_6x6" -> {
                return R.drawable.button_disabled_6x6
            }
            "button_enabled_9x9" -> {
                return R.drawable.button_enabled_9x9
            }
            "button_disabled_9x9" -> {
                return R.drawable.button_disabled_9x9
            }
            "mark1" -> {
                return R.drawable.mark1
            }
            "mark2" -> {
                return R.drawable.mark2
            }
            "mark3" -> {
                return R.drawable.mark3
            }
            "mark4" -> {
                return R.drawable.mark4
            }
            "mark5" -> {
                return R.drawable.mark5
            }
            "mark6" -> {
                return R.drawable.mark6
            }
            "mark7" -> {
                return R.drawable.mark7
            }
            "mark8" -> {
                return R.drawable.mark8
            }
            "mark9" -> {
                return R.drawable.mark9
            }
        }

        return -1
    }
}