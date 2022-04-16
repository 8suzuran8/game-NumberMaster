package com.example.numbermaster

import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.core.view.setMargins
import androidx.viewpager2.widget.ViewPager2
import com.example.numbermaster.databinding.ActivityHowtoBinding

class HowtoActivity : NumberMasterActivity() {
    private lateinit var viewPager: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.base)

        this.convertIntentExtraToGlobalActivityInfo()

        val that = this

        val inflateRootLayout = findViewById<FrameLayout>(R.id.root_layout)
        val activityLayout = layoutInflater.inflate(R.layout.activity_howto, inflateRootLayout)
        val layoutBinding: ActivityHowtoBinding = ActivityHowtoBinding.bind(activityLayout)
        val layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT).apply {
            setMargins(that.globalActivityInfo["meta:otherSize"]!!.toFloat().toInt())
        }
        addContentView(layoutBinding.rootLayout, layoutParams)

        this.convertIntentExtraToGlobalActivityInfo()
        val dbHelper = NumberMasterOpenHelper(this)
        val settings = dbHelper.loadSettings()

        var viewPageItemCount = 3
        if (settings.containsKey("counter_stop_count") && settings["counter_stop_count"]!!.toInt() > 0) {
            viewPageItemCount = 4
        }
        val numberMasterViewPager = NumberMasterViewPager(
            this,
            "_howto",
            if (this.resources.configuration.orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                this.globalActivityInfo["meta:rootLayoutShort"]!!.toFloat()
            } else {
                this.globalActivityInfo["meta:rootLayoutLong"]!!.toFloat()
            },
            this.globalActivityInfo["meta:otherSize"]!!.toFloat().toInt(),
            viewPageItemCount
        )
        this.viewPager = numberMasterViewPager.viewPager

        dbHelper.writeSettings(mutableMapOf(
            "counter_stop_count" to settings["counter_stop_count"].toString(),
            "enabled_cube" to settings["enabled_cube"].toString(),
            "add_icon_read" to 1.toString(), // 既読にする
        ))
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

    fun buttonClickListener(view: View) {
        when (view.id) {
            R.id.prev_button -> {
                this.finish()
            }

            R.id.textView2 -> {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(this.getString(R.string.howto_link)))
                startActivity(intent)
            }
        }
    }
}