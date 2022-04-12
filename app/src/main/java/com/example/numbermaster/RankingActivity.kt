package com.example.numbermaster

import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ListView
import android.widget.RelativeLayout
import androidx.core.view.setMargins
import androidx.viewpager2.widget.ViewPager2
import com.example.numbermaster.databinding.ActivityRankingBinding
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

// @todo 横向きでコピーをするとエラーになる。
class RankingActivity : NumberMasterActivity() {
    private lateinit var viewPager: ViewPager2
    private lateinit var numberMasterViewPager: NumberMasterViewPager
    private var dbHelper: NumberMasterOpenHelper? = null
    private var settings: MutableMap<String, Int>? = null
    private val dialogs: MutableMap<Int, MutableMap<String, AlertDialog.Builder>> = mutableMapOf(
        DIALOG_TYPE_COPY to mutableMapOf(),
        DIALOG_TYPE_DELETE to mutableMapOf(),
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.base)

        this.convertIntentExtraToGlobalActivityInfo()
        this.dbHelper = NumberMasterOpenHelper(this)
        this.settings = this.dbHelper!!.loadSettings()

        val that = this

        val inflateRootLayout = findViewById<FrameLayout>(R.id.root_layout)
        val activityLayout = layoutInflater.inflate(R.layout.activity_ranking, inflateRootLayout)
        val layoutBinding: ActivityRankingBinding = ActivityRankingBinding.bind(activityLayout).apply {
            updateButton.apply {
                layoutParams.width = that.globalActivityInfo["meta:otherSize"]!!.toFloat().toInt()
                layoutParams.height = that.globalActivityInfo["meta:otherSize"]!!.toFloat().toInt()
            }
            // 検索機能はカンスト後に使える様になる。
            if (that.settings!!.containsKey("counter_stop_count") && that.settings!!["counter_stop_count"]!!.toInt() > 0) {
                textContainer.visibility = RelativeLayout.VISIBLE
            }
            textContainer.layoutParams.height = (that.globalActivityInfo["meta:otherSize"]!!.toFloat() * 2).toInt()
            textContainer.setPadding(0, 0, 0, that.globalActivityInfo["meta:otherSize"]!!.toFloat().toInt())

            if (that.resources.configuration.orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                textLayout.layoutParams.width =
                    (that.globalActivityInfo["meta:rootLayoutShort"]!!.toFloat() - (that.globalActivityInfo["meta:otherSize"]!!.toFloat() * 3)).toInt()
                textBox.layoutParams.width =
                    (that.globalActivityInfo["meta:rootLayoutShort"]!!.toFloat() - (that.globalActivityInfo["meta:otherSize"]!!.toFloat() * 4)).toInt()
                viewPager2.layoutParams.height =
                    (that.globalActivityInfo["meta:rootLayoutLong"]!!.toFloat() - (that.globalActivityInfo["meta:otherSize"]!!.toFloat() * 3)).toInt()
            } else {
                textLayout.layoutParams.width =
                    (that.globalActivityInfo["meta:rootLayoutLong"]!!.toFloat() - (that.globalActivityInfo["meta:otherSize"]!!.toFloat() * 3)).toInt()
                textBox.layoutParams.width =
                    (that.globalActivityInfo["meta:rootLayoutLong"]!!.toFloat() - (that.globalActivityInfo["meta:otherSize"]!!.toFloat() * 4)).toInt()
                viewPager2.layoutParams.height =
                    (that.globalActivityInfo["meta:rootLayoutShort"]!!.toFloat() - (that.globalActivityInfo["meta:otherSize"]!!.toFloat() * 3)).toInt()
            }
        }

        val layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT).apply {
            setMargins(that.globalActivityInfo["meta:otherSize"]!!.toFloat().toInt())
        }
        addContentView(layoutBinding.rootLayout, layoutParams)

        this.numberMasterViewPager = NumberMasterViewPager(this,
            "_ranking",
            if (this.resources.configuration.orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                this.globalActivityInfo["meta:rootLayoutShort"]!!.toFloat()
            } else {
                this.globalActivityInfo["meta:rootLayoutLong"]!!.toFloat()
            },
            this.globalActivityInfo["meta:otherSize"]!!.toFloat().toInt(),
            3
        )
        this.viewPager = this.numberMasterViewPager.viewPager
    }

    override fun onBackPressed() {
        if (viewPager.currentItem == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed()
        } else {
            // Otherwise, select the previous step.
            viewPager.currentItem = viewPager.currentItem - 1
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        val that = this

        if (this.resources.configuration.orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            findViewById<TextInputLayout>(R.id.text_layout).apply {
                layoutParams.width =
                    (that.globalActivityInfo["meta:rootLayoutShort"]!!.toFloat() - (that.globalActivityInfo["meta:otherSize"]!!.toFloat() * 3)).toInt()
            }
            findViewById<TextInputEditText>(R.id.text_box).apply {
                layoutParams.width =
                    (that.globalActivityInfo["meta:rootLayoutShort"]!!.toFloat() - (that.globalActivityInfo["meta:otherSize"]!!.toFloat() * 4)).toInt()
            }
            findViewById<ViewPager2>(R.id.view_pager2).apply {
                layoutParams.height =
                    (that.globalActivityInfo["meta:rootLayoutLong"]!!.toFloat() - (that.globalActivityInfo["meta:otherSize"]!!.toFloat() * 3)).toInt()
            }
        } else {
            findViewById<TextInputLayout>(R.id.text_layout).apply {
                layoutParams.width =
                    (that.globalActivityInfo["meta:rootLayoutLong"]!!.toFloat() - (that.globalActivityInfo["meta:otherSize"]!!.toFloat() * 3)).toInt()
            }
            findViewById<TextInputEditText>(R.id.text_box).apply {
                layoutParams.width =
                    (that.globalActivityInfo["meta:rootLayoutLong"]!!.toFloat() - (that.globalActivityInfo["meta:otherSize"]!!.toFloat() * 4)).toInt()
            }
            findViewById<ViewPager2>(R.id.view_pager2).apply {
                layoutParams.height =
                    (that.globalActivityInfo["meta:rootLayoutShort"]!!.toFloat() - (that.globalActivityInfo["meta:otherSize"]!!.toFloat() * 3)).toInt()
            }
        }
    }

    fun buttonClickListener(view: View) {
        when (view.id) {
            R.id.prev_button -> {
                this.finish()
            }

            R.id.update_button -> {
                this.numberMasterViewPager.adapter!!.fragment!!.loadRanking(
                    findViewById(R.id.ranking_container),
                    findViewById<TextInputEditText>(R.id.text_box).text.toString(),
                    this
                )
            }

            R.id.copy_button -> {
                this.dialogs[DIALOG_TYPE_COPY]!![view.contentDescription.toString()]!!.show()
            }

            R.id.recycle_button -> {
                this.dialogs[DIALOG_TYPE_DELETE]!![view.contentDescription.toString()]!!.show()
            }
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        val rankingList = findViewById<ListView>(R.id.ranking_container)

        for (i in 0 until rankingList.childCount) {
            val copyButton = rankingList.getChildAt(i).findViewById<ImageButton>(R.id.copy_button)
            this.dialogs[DIALOG_TYPE_COPY]!![copyButton.contentDescription.toString()] =
                this.createDialog(DIALOG_TYPE_COPY, copyButton.contentDescription.toString().toInt())

            if (this.settings!!["counter_stop_count"]!!.toInt() > 0) {
                val recycleButton =
                    rankingList.getChildAt(i).findViewById<ImageButton>(R.id.recycle_button)
                this.dialogs[DIALOG_TYPE_DELETE]!![recycleButton.contentDescription.toString()] =
                    this.createDialog(DIALOG_TYPE_DELETE, recycleButton.contentDescription.toString().toInt())
            }
        }
    }

    private fun createDialog(dialogType: Int, buttonId: Int): AlertDialog.Builder {
        val that = this

        return AlertDialog.Builder(this).apply {
            setTitle(R.string.message_title_game)
            if (dialogType == DIALOG_TYPE_COPY) {
                setMessage(R.string.message_title_copy)
                setNegativeButton(R.string.message_copy_start_number) { _, _ ->
                    val startNumber = that.dbHelper!!.loadHistoryStartNumber(buttonId)
                    val clipboardManager: ClipboardManager =
                        that.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    clipboardManager.setPrimaryClip(
                        ClipData.newPlainText(
                            "start_numbers",
                            startNumber
                        )
                    )
                }
                setPositiveButton(R.string.message_copy_finish_number) { _, _ ->
                    val finishNumber = that.dbHelper!!.loadHistoryFinishNumber(buttonId)
                    val clipboardManager: ClipboardManager =
                        that.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    clipboardManager.setPrimaryClip(
                        ClipData.newPlainText(
                            "start_numbers",
                            finishNumber
                        )
                    )
                }
            } else if (dialogType == DIALOG_TYPE_DELETE) {
                setMessage(R.string.message_delete_complete_data)
                setNegativeButton(R.string.message_button_text_cancel) { _, _ ->
                }
                setPositiveButton(R.string.message_button_text_ok) { _, _ ->
                    that.dbHelper!!.deleteHistory(buttonId)
                    finish()
                    startActivity(intent)
                }
            }

            create()
        }
    }

    companion object {
        const val DIALOG_TYPE_COPY = 1
        const val DIALOG_TYPE_DELETE = 2
    }
}