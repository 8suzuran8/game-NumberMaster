package com.example.numbermaster

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.core.view.setMargins
import androidx.viewpager2.widget.ViewPager2
import com.example.numbermaster.databinding.ActivityRankingBinding
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class RankingActivity : NumberMasterActivity() {
    private lateinit var viewPager: ViewPager2
    private lateinit var numberMasterViewPager: NumberMasterViewPager
    private var dbHelper: NumberMasterOpenHelper? = null
    private var settings: MutableMap<String, Int>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.base)

        this.convertIntentExtraToGlobalActivityInfo()
        this.dbHelper = NumberMasterOpenHelper(this)
        this.settings = this.dbHelper!!.loadSettings()

        val that = this

        val inflateRootLayout = this.findViewById<FrameLayout>(R.id.root_layout)
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
        }
    }

    companion object {
        const val DIALOG_TYPE_COPY = 1
        const val DIALOG_TYPE_DELETE = 2
    }
}