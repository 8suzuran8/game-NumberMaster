package com.example.numbermaster

import android.animation.*
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.*
import android.widget.*
import androidx.core.view.*
import com.example.numbermaster.databinding.ActivityMenuBinding
import kotlin.concurrent.timer

class MenuActivity : NumberMasterActivity() {
    private var activityIntents: MutableMap<Int, Intent?> = mutableMapOf(
        R.id.menu_start_button to null,
        R.id.menu_input_button to null,
        R.id.menu_ranking_button to null,
        R.id.menu_howto_button to null,
        R.id.menu_story_button to null,
        R.id.menu_thanks_hint_button to null,
    )

    private var initialed = false
    private var menuY: Float? = null

    // prevボタンで戻った時には実行されない
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.base)

        this.activityIntents = mutableMapOf(
            R.id.menu_start_button to Intent(application, GameActivity::class.java),
            R.id.menu_input_button to Intent(application, InputActivity::class.java),
            R.id.menu_ranking_button to Intent(application, RankingActivity::class.java),
            R.id.menu_howto_button to Intent(application, HowtoActivity::class.java),
            R.id.menu_story_button to Intent(application, StoryActivity::class.java),
            R.id.menu_thanks_hint_button to Intent(application, ThxActivity::class.java),
        )

        this.convertIntentExtraToGlobalActivityInfo()
        for (key in this.activityIntents.keys) {
            this.convertGlobalActivityInfoToIntentExtra(this.activityIntents[key]!!)
        }

        val that = this
        val menuSize = (this.globalActivityInfo["meta:rootLayoutWidth"]!!.toFloat() - (this.globalActivityInfo["meta:otherSize"]!!.toFloat() * 2)).toInt()
        this.menuY = ((this.globalActivityInfo["meta:rootLayoutHeight"]!!.toFloat()) / 2) - (menuSize / 2) - this.globalActivityInfo["meta:otherSize"]!!.toFloat()

        val dbHelper = NumberMasterOpenHelper(this)
        val settings = dbHelper.loadSettings()

        val inflateRootLayout = findViewById<FrameLayout>(R.id.root_layout)
        val activityLayout = layoutInflater.inflate(R.layout.activity_menu, inflateRootLayout)
        val layoutBinding: ActivityMenuBinding = ActivityMenuBinding.bind(activityLayout).apply {
            titleImage.pivotX = (menuSize / 2).toFloat()
            titleImage.stateListAnimator = AnimatorInflater.loadStateListAnimator(that, R.xml.animate_menu_title)
            menuLayout.setPadding(that.globalActivityInfo["boardFrameWidth"]!!.toFloat().toInt())
            menuLayout.stateListAnimator = AnimatorInflater.loadStateListAnimator(that, R.xml.animate_menu_menu)
            menuLayout.layoutParams.width = menuSize
            menuLayout.layoutParams.height = menuSize
            menuLayout.translationY = that.menuY!!

            if (settings.containsKey("add_icon_read") && settings["add_icon_read"]!!.toInt() == 0) {
                menuHowtoButton.setImageResource(R.drawable.add_icon)
                val imageMatrix = menuHowtoButton.imageMatrix.apply {
                    val oneMenuSize = menuSize / 3
                    val position = (oneMenuSize - (oneMenuSize * 0.4)).toFloat()
                    postScale(0.2F, 0.2F)
                    postTranslate(position, position)
                }
                menuHowtoButton.imageMatrix = imageMatrix
            }

            // 1メニューのサイズを1/3にする
            for (rowIndex in 0 until menuLayout.childCount) {
                val rowLayout = menuLayout.getChildAt(rowIndex) as TableRow
                for (imageButtonIndex in 0 until rowLayout.childCount) {
                    rowLayout.getChildAt(imageButtonIndex).apply {
                        layoutParams.height = that.globalActivityInfo["numberPanelSize:1"]!!.toFloat().toInt()
                        layoutParams.width = that.globalActivityInfo["numberPanelSize:1"]!!.toFloat().toInt()
                    }
                }
            }
        }

        val layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT).apply {
            setMargins(that.globalActivityInfo["meta:otherSize"]!!.toFloat().toInt())
        }
        addContentView(layoutBinding.rootLayout, layoutParams)
    }

    fun buttonClickListener(view: View) {
        val menuLayout = findViewById<TableLayout>(R.id.menu_layout)

        // メニューのスケールが変化中なら処理しない
        if (menuLayout.alpha < 1.0) return

        // メニュー内の押した部分をアクティブにし、それ以外を非アクティブにする。
        for (rowIndex in 0 until menuLayout.childCount) {
            val rowLayout = menuLayout.getChildAt(rowIndex) as TableRow
            for (imageButtonIndex in 0 until rowLayout.childCount) {
                val imageButton = rowLayout.getChildAt(imageButtonIndex) as ImageButton
                if (view.id == imageButton.id) {
                    imageButton.setBackgroundResource(+R.drawable.menu_active)
                } else {
                    imageButton.setBackgroundResource(0)
                }
            }
        }

        // 存在しないメニューを押したら、ここで終了
        if (!this.activityIntents.contains(view.id) || this.activityIntents[view.id] == null) {
            if (view.id != R.id.prev_button) {
                return
            }
        }

        when (view.id) {
            R.id.prev_button -> {
                this.finishAffinity()
                return
            }
            R.id.menu_start_button -> {
                val that = this
                findViewById<TableLayout>(R.id.menu_layout).apply {
                    stateListAnimator = AnimatorInflater.loadStateListAnimator(that, R.xml.animate_menu_togame_menu)
                }

                findViewById<ImageView>(R.id.title_image).apply {
                    stateListAnimator = AnimatorInflater.loadStateListAnimator(that, R.xml.animate_menu_togame_title)
                }

                // 上記animation時間1000 + マージン500
                timer(name = "toGame", initialDelay = 1500, period = 1500) {
                    Handler(Looper.getMainLooper()).post {
                        this.cancel()
                        startActivity(that.activityIntents[view.id])
                    }
                }
                return
            }
            else -> {
                startActivity(this.activityIntents[view.id])
                return
            }
        }
    }
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (!hasFocus) return

        val that = this

        if (this.initialed) {
            findViewById<ImageView>(R.id.title_image).apply {
                alpha = 1F
            }
            findViewById<TableLayout>(R.id.menu_layout).apply {
                translationY = that.menuY!!
            }
        } else {
            this.initialed = true
        }
    }
}