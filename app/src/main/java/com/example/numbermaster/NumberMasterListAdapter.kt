package com.example.numbermaster

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.TextView

class NumberMasterListAdapter(val context: Context, private val rankingList: ArrayList<NumberMasterRanking>, private val settings: MutableMap<String, Int>): BaseAdapter() {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View? = if (convertView != null) {
            convertView
        } else {
            val layoutInflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val rootLayout = parent!!.findViewById<FrameLayout>(R.id.inner_root_layout)
            layoutInflater.inflate(R.layout.fragment_ranking_detail, rootLayout, false)
        }

        val ranking = rankingList[position]

        view!!.findViewById<TextView>(R.id.no).apply {
            text = ranking.no
        }
        view.findViewById<TextView>(R.id.score).apply {
            text = ranking.score
        }
        view.findViewById<TextView>(R.id.time).apply {
            text = ranking.time
        }
        view.findViewById<TextView>(R.id.play_date).apply {
            text = ranking.playDate
        }
        view.findViewById<ImageButton>(R.id.copy_button).apply {
            contentDescription = ranking.id.toString()
            visibility = ImageButton.VISIBLE
        }
        view.findViewById<ImageButton>(R.id.recycle_button).apply {
            contentDescription = ranking.id.toString()
            if (settings["counter_stop_count"]!!.toInt() > 0) {
                visibility = ImageButton.VISIBLE
                isEnabled = true
            }
        }

        return view
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
}