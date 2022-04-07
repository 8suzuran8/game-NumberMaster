package com.example.numbermaster

import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.core.view.setMargins
import androidx.core.view.setPadding
import com.example.numbermaster.databinding.ActivityThxBinding

class ThxActivity : NumberMasterActivity() {
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
            hint.layoutParams.width = (that.globalActivityInfo["meta:rootLayoutWidth"]!!.toFloat() / 2).toInt()
        }

        val layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT).apply {
            setMargins(that.globalActivityInfo["meta:otherSize"]!!.toFloat().toInt())
        }
        addContentView(layoutBinding.rootLayout, layoutParams)
    }

    fun buttonClickListener(view: View) {
        when (view.id) {
            R.id.prev_button -> {
                this.finish()
            }
        }
    }
}