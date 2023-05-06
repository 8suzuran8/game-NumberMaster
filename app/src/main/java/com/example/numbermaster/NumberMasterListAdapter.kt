package com.example.numbermaster

import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView

// context is activity
class NumberMasterListAdapter(private val context: Context, private val rankingList: ArrayList<NumberMasterRanking>, private val settings: MutableMap<String, Int>): BaseAdapter() {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val that = this

        // view is row
        val view: View? = if (convertView != null) {
            convertView
        } else {
            val layoutInflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val rootLayout = parent!!.findViewById<FrameLayout>(R.id.inner_root_layout)
            layoutInflater.inflate(R.layout.fragment_ranking_detail, rootLayout, false)
        }

        val ranking = rankingList[position]

        return view!!.apply {
            findViewById<TextView>(R.id.ranking_id).apply {
                text = ranking.id.toString()
            }
            findViewById<TextView>(R.id.no).apply {
                text = ranking.no
            }
            findViewById<TextView>(R.id.score).apply {
                text = ranking.score
                contentDescription = "score is $text."
            }
            findViewById<TextView>(R.id.time).apply {
                text = ranking.time
                contentDescription = "time is $text."
            }
            findViewById<TextView>(R.id.tap_count).apply {
                text = ranking.tapCount
                contentDescription = "tap count is $text."
            }
            findViewById<TextView>(R.id.play_date).apply {
                text = ranking.playDate
                contentDescription = "play date is $text."
            }
            findViewById<ImageButton>(R.id.copy_button).apply {
                visibility = ImageButton.VISIBLE
                setOnClickListener {
                    that.createDialog(RankingActivity.DIALOG_TYPE_COPY, ranking.id.toString().toInt(), parent).show()
                }
            }
            findViewById<ImageButton>(R.id.recycle_button).apply {
                if (settings["counter_stop_count"]!!.toInt() > 0) {
                    visibility = ImageButton.VISIBLE
                    isEnabled = true
                }
                setOnClickListener {
                    that.createDialog(RankingActivity.DIALOG_TYPE_DELETE, ranking.id.toString().toInt(), parent).show()
                }
            }
        }
    }

    override fun getItem(position: Int): Any {
        return rankingList[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getCount(): Int {
        return rankingList.size
    }

    fun buttonClickListener() {
    }

    private fun createDialog(dialogType: Int, buttonId: Int, parent: ViewGroup?): AlertDialog.Builder {
        val that = this
        val dbHelper = NumberMasterOpenHelper(this.context)

        return AlertDialog.Builder(that.context).apply {
            setTitle(R.string.message_title_game)
            if (dialogType == RankingActivity.DIALOG_TYPE_COPY) {
                setMessage(R.string.message_title_copy)
                setNegativeButton(R.string.message_copy_start_number) { _, _ ->
                    val startNumber = dbHelper.loadHistoryStartNumber(buttonId)
                    val clipboardManager: ClipboardManager =
                        that.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    clipboardManager.setPrimaryClip(
                        ClipData.newPlainText(
                            "start_numbers",
                            startNumber
                        )
                    )
                }
                setPositiveButton(R.string.message_copy_finish_number) { _, _ ->
                    val finishNumber = dbHelper.loadHistoryFinishNumber(buttonId)
                    val clipboardManager: ClipboardManager =
                        that.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    clipboardManager.setPrimaryClip(
                        ClipData.newPlainText(
                            "start_numbers",
                            finishNumber
                        )
                    )
                }
            } else if (dialogType == RankingActivity.DIALOG_TYPE_DELETE) {
                setMessage(R.string.message_delete_complete_data)
                setNegativeButton(R.string.message_button_text_cancel) { _, _ ->
                }
                setPositiveButton(R.string.message_button_text_ok) { _, _ ->
                    dbHelper.deleteHistory(buttonId)
                    parent!!.rootView.findViewById<ImageView>(R.id.update_button).performClick()
                }
            }

            create()
        }
    }
}