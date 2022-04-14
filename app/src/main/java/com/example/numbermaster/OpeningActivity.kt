package com.example.numbermaster

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.view.updateLayoutParams

// いちばん初めのactivityは画面サイズを取れていないのでdata bindingが使えない
// ゆえにこのactivityだけonWindowFocusChangedでサイズ調節
open class OpeningActivity : NumberMasterActivity() {
    private var menuActivityIntent: Intent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.base)

        val inflateRootLayout = findViewById<FrameLayout>(R.id.root_layout)
        val layout = layoutInflater.inflate(R.layout.activity_opening, inflateRootLayout)
        val layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
        addContentView(layout, layoutParams)

        // ここでtableを作っておく
        NumberMasterOpenHelper(this).apply {
            writableDatabase
        }
    }

    private fun makeGlobalActivityInfo() {
        val layout = findViewById<FrameLayout>(R.id.base_root_layout)

        if (layout.width < layout.height) {
            this.globalActivityInfo["meta:rootLayoutShort"] = layout.width.toString()
            this.globalActivityInfo["meta:rootLayoutLong"] = layout.height.toString()
        } else {
            this.globalActivityInfo["meta:rootLayoutShort"] = layout.height.toString()
            this.globalActivityInfo["meta:rootLayoutLong"] = layout.width.toString()
        }
        this.globalActivityInfo["meta:otherSize"] = (this.globalActivityInfo["meta:rootLayoutShort"]!!.toFloat() / 10).toString()

        this.globalActivityInfo["gameSpaceSize"] = ((this.globalActivityInfo["meta:rootLayoutShort"]!!.toFloat() * 0.8) - (this.globalActivityInfo["meta:otherSize"]!!.toFloat() * 2)).toString()
        this.globalActivityInfo["boardFrameWidth"] = (this.globalActivityInfo["gameSpaceSize"]!!.toFloat() / 40).toString()

        val gameSpaceSizeWithoutFrame = this.globalActivityInfo["gameSpaceSize"]!!.toFloat() - (this.globalActivityInfo["boardFrameWidth"]!!.toFloat() * 2)
        this.globalActivityInfo["numberPanelSize:1"] = (gameSpaceSizeWithoutFrame / 3).toString()
        this.globalActivityInfo["numberPanelSize:2"] = (gameSpaceSizeWithoutFrame / 6).toString()
        this.globalActivityInfo["numberPanelSize:3"] = (gameSpaceSizeWithoutFrame / 9).toString()

        // this.globalActivityInfo["meta:otherSize"] * 4 = prev button + change size button + status space + status margin top
        // this.globalActivityInfo["meta:otherSize"] * 2 = swipe button * 2
        this.globalActivityInfo["swipeButtonMargin"] = ((layout.height - ((this.globalActivityInfo["meta:otherSize"]!!.toFloat() * 6) + this.globalActivityInfo["gameSpaceSize"]!!.toFloat())) / 2).toString()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        if (!hasFocus) return
        if (this.globalActivityInfo["meta:otherSize"] != 0.toString()) {
            return
        }

        this.menuActivityIntent = Intent(application, MenuActivity::class.java)
        this.makeGlobalActivityInfo()
        this.convertGlobalActivityInfoToIntentExtra(this.menuActivityIntent!!)

        super.onWindowFocusChanged(hasFocus)
    }

    fun buttonClickListener(view: View) {
        when (view.id) {
            R.id.prev_button -> {
                this.finish()
                return
            }
            R.id.text -> {
                if (view.visibility == View.VISIBLE) {
                    startActivity(this.menuActivityIntent)
                    return
                }
            }
        }
    }

    override fun initialProcess(globalActivityInfo: MutableMap<String, String>) {
        super.initialProcess(globalActivityInfo)

        val that = this
        findViewById<TextView>(R.id.text).apply {
            updateLayoutParams {
                // 戻るボタン分減らす
                height = (globalActivityInfo["meta:rootLayoutShort"]!!.toFloat() - (globalActivityInfo["meta:otherSize"]!!.toFloat() * 2)).toInt()
                width = height
            }
            visibility = TextView.VISIBLE
        }

        findViewById<TextView>(R.id.prev_button_text).apply {
            text = that.getString(R.string.button_text_finish)
        }
    }
}