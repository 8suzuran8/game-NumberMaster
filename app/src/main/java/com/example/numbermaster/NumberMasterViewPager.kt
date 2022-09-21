package com.example.numbermaster

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.setPadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2

class NumberMasterViewPager constructor(activity: FragmentActivity, pageName: String, rootLayoutWidth: Float, otherSize: Int, itemCount: Int) {
    var viewPager: ViewPager2 = activity.findViewById(R.id.view_pager2)
    var adapter: ScreenSlidePagerAdapter? = null

    init {
        // Instantiate a ViewPager2 and a PagerAdapter.
        // The pager adapter, which provides the pages to the view pager widget.
        this.adapter = ScreenSlidePagerAdapter(activity, pageName, rootLayoutWidth, otherSize, itemCount)
        this.viewPager.adapter = this.adapter
    }

    /**
     * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
     * sequence.
     */
    inner class ScreenSlidePagerAdapter(activity: FragmentActivity,
                                                private var pageName: String,
                                                private val rootLayoutWidth: Float,
                                                private val otherSize: Int,
                                                private val itemCount: Int
    ) : FragmentStateAdapter(activity) {
        var fragment: NumberMasterFragment? = null

        override fun getItemCount(): Int = this.itemCount

        override fun createFragment(position: Int): Fragment {
            val that = this
            this.fragment = NumberMasterFragment(this.rootLayoutWidth, this.otherSize)

            this.fragment!!.arguments = Bundle().apply {
                putInt("pageNumber", position)
                putString("pageName", that.pageName)
            }

            return this.fragment!!
        }
    }
}

class NumberMasterFragment(private val rootLayoutWidth: Float, private val otherSize: Int) : Fragment() {
    var layout: View? = null

    private fun getResourceLayout(name: String): Int {
        when (name) {
            "fragment_howto_page0" -> {
                return R.layout.fragment_howto_page0
            }

            "fragment_howto_page1" -> {
                return R.layout.fragment_howto_page1
            }

            "fragment_howto_page2" -> {
                return R.layout.fragment_howto_page2
            }

            "fragment_howto_page3" -> {
                return R.layout.fragment_howto_page3
            }

            "fragment_ranking_detail" -> {
                return R.layout.fragment_ranking_detail
            }

            "fragment_ranking_page" -> {
                return R.layout.fragment_ranking_page
            }
        }

        return -1
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        var pageNumber = arguments?.getInt("pageNumber").toString()
        if (pageNumber == null.toString()) {
            pageNumber = 0.toString()
        }

        var pageName = arguments?.getString("pageName")
        if (pageName == null) {
            pageName = ""
        }

        if (pageName == "_ranking") {
            pageNumber = ""
        }

        val layoutId = this.getResourceLayout("fragment$pageName" + "_page$pageNumber")

        val layout = inflater.inflate(layoutId, container, false)
        this.layout = layout

        val swipeImage = layout.findViewById<ImageView>(R.id.swipe)
        if (arguments?.getInt("pageNumber") == 0) {
            if (swipeImage != null) {
                swipeImage.x = (this.rootLayoutWidth - (swipeImage.layoutParams.width * 5))
            }
        } else {
            if (swipeImage != null) {
                swipeImage.alpha = 0F
            }
        }

        try {
            val innerRootLayout = layout.findViewById<ScrollView>(R.id.inner_root_layout)
            innerRootLayout.setPadding(this.otherSize)
        } catch (exception: Exception) {
            try {
                val innerRootLayout = layout.findViewById<FrameLayout>(R.id.inner_root_layout)
                innerRootLayout.setPadding(this.otherSize)
            } catch (exception: Exception) {
                try {
                    val innerRootLayout = layout.findViewById<RelativeLayout>(R.id.inner_root_layout)
                    innerRootLayout.setPadding(this.otherSize)
                } catch (exception: Exception) {
                    return layout
                }
            }
        }

        return layout
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (this.requireActivity().javaClass.name != "com.example.numbermaster.RankingActivity") {
            return
        }

        val rankingContainer = this.layout!!.findViewById<ListView>(R.id.ranking_container)
        this.loadRanking(rankingContainer)
    }

    fun loadRanking(rankingContainer: ListView, startNumber: String = "", activity: FragmentActivity = this.requireActivity()) {
        val dbHelper = NumberMasterOpenHelper(activity)
        var successHistories: List<Map<String, String>> = listOf()
        val that = this

        val settings = dbHelper.loadSettings()

        when (arguments?.getInt("pageNumber")) {
            0 -> {
                successHistories = dbHelper.loadHistory("score", "DESC", startNumber) // scoreが大きい順
                this.layout!!.findViewById<TextView>(R.id.title).apply {
                    text = getText(R.string.ranking_type1)
                    layoutParams.height = that.otherSize
                }
            }

            1 -> {
                successHistories = dbHelper.loadHistory("time", "ASC", startNumber) // timeが小さい順
                this.layout!!.findViewById<TextView>(R.id.title).apply {
                    text = getText(R.string.ranking_type2)
                    layoutParams.height = that.otherSize
                }
            }

            2 -> {
                successHistories = dbHelper.loadHistory("created_at", "DESC", startNumber) // 登録日時が大きい準備
                this.layout!!.findViewById<TextView>(R.id.title).apply {
                    text = getText(R.string.ranking_type3)
                    layoutParams.height = that.otherSize
                }
            }
        }

        val rankingList: ArrayList<NumberMasterRanking> = arrayListOf()

        var successHistoryIndex = -1
        for (childIndex in successHistories.indices) {
            val idText = successHistories[childIndex]["id"]!!.toInt()
            val noText = "No. " + (successHistoryIndex + 1).toString()
            val scoreText = "SCORE " + successHistories[childIndex]["score"]!!.toInt()
            val timeText = "TIME " + this.timeToString(successHistories[childIndex]["time"]!!.toInt())
            val playDateText = successHistories[childIndex]["created_at"].toString()
            rankingList.add(NumberMasterRanking(idText, noText, scoreText, timeText, playDateText))

            successHistoryIndex += 1
        }

        //リストビューにアダプターを設定
        rankingContainer.adapter = NumberMasterListAdapter(activity, rankingList, settings)
    }

    private fun timeToString(time: Int): String {
        val hour: Int
        val minutes: Int
        if (time > 60 * 60 * 10) {
            hour = time / (60 * 60)
            minutes = time % (60 * 60)

            return String.format("%02dh%02dm", hour, minutes)
        }

        minutes = time / 60
        val seconds: Int = time % 60

        return String.format("%02dm%02ds", minutes, seconds)
    }
}