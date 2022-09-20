package com.example.numbermaster

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.core.view.setMargins
import androidx.core.view.setPadding
import com.example.numbermaster.databinding.ActivityStoryBinding

class StoryActivity : NumberMasterActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.base)

        this.convertIntentExtraToGlobalActivityInfo()

        val that = this

        val inflateRootLayout = findViewById<FrameLayout>(R.id.root_layout)
        val activityLayout = layoutInflater.inflate(R.layout.activity_story, inflateRootLayout)
        val layoutBinding: ActivityStoryBinding = ActivityStoryBinding.bind(activityLayout).apply {
            rootLayout.setPadding(that.globalActivityInfo["meta:otherSize"]!!.toFloat().toInt())
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