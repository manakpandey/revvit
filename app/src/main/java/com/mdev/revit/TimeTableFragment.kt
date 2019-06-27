package com.mdev.revit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_time_table.view.*
import java.util.*

class TimeTableFragment: Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_time_table,container,false)

        view.view_pager_tt.adapter = TimeTableAdapter(childFragmentManager)
        view.tabs_tt.setupWithViewPager(view.view_pager_tt)
        view.view_pager_tt.currentItem = getWeekDay()

        return view
    }

    private fun getWeekDay(): Int {
        var wd = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 2
        if (wd<0)
            wd+=7
        return wd
    }
}