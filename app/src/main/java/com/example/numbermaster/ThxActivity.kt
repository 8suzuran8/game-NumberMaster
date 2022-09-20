package com.example.numbermaster

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.ScrollView
import androidx.core.view.setMargins
import androidx.core.view.setPadding
import com.example.numbermaster.databinding.ActivityThxBinding

class ThxActivity : NumberMasterActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.base)

        this.convertIntentExtraToGlobalActivityInfo()

        val that = this

        val inflateRootLayout = findViewById<FrameLayout>(R.id.root_layout)
        val activityLayout = layoutInflater.inflate(R.layout.activity_thx, inflateRootLayout)
        val layoutBinding: ActivityThxBinding = ActivityThxBinding.bind(activityLayout).apply {
            thanks.setPadding(that.globalActivityInfo["meta:otherSize"]!!.toFloat().toInt())
            hint.setPadding(that.globalActivityInfo["meta:otherSize"]!!.toFloat().toInt())
            hint.layoutParams.width = if (that.resources.configuration.orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                (that.globalActivityInfo["meta:rootLayoutShort"]!!.toFloat() / 2).toInt()
            } else {
                (that.globalActivityInfo["meta:rootLayoutLong"]!!.toFloat() / 2).toInt()
            }
        }

        val layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT).apply {
            setMargins(that.globalActivityInfo["meta:otherSize"]!!.toFloat().toInt())
        }
        addContentView(layoutBinding.rootLayout, layoutParams)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        val that = this

        findViewById<ScrollView>(R.id.hint).apply {
            layoutParams.width = if (that.resources.configuration.orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                (that.globalActivityInfo["meta:rootLayoutShort"]!!.toFloat() / 2).toInt()
            } else {
                (that.globalActivityInfo["meta:rootLayoutLong"]!!.toFloat() / 2).toInt()
            }
        }
    }

    fun buttonClickListener(view: View) {
        when (view.id) {
            R.id.prev_button -> {
                this.finish()
            }
        }
    }
}