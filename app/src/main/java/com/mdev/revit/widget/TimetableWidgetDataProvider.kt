package com.mdev.revit.widget

import android.content.Context
import android.content.Intent
import android.util.Log
import android.util.SparseArray
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.mdev.revit.R
import com.mdev.revit.utils.Slots
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class TimetableWidgetDataProvider(private val mContext: Context, val mIntent: Intent) : RemoteViewsService.RemoteViewsFactory {

    private val schedule = ArrayList<HashMap<String,String>>()

    override fun onCreate() {
        initData()
    }

    override fun getLoadingView() = null

    override fun getItemId(position: Int) = position.toLong()

    override fun onDataSetChanged() {
        initData()
    }

    override fun hasStableIds(): Boolean = true

    override fun getViewAt(position: Int): RemoteViews {
        if (schedule[position]["slot"].isNullOrEmpty()){
            val view = RemoteViews(mContext.packageName, R.layout.timetable_widget_row)
            when {
                schedule[position]["type"]!!.contains("lab",ignoreCase = true) -> view.setImageViewResource(R.id.course_type_icon, R.drawable.ic_robotic_arm)
                schedule[position]["type"]!!.contains("theory",ignoreCase = true) -> view.setImageViewResource(R.id.course_type_icon, R.drawable.ic_board)
                else -> view.setImageViewResource(R.id.course_type_icon, R.drawable.ic_intelligence)
            }
            view.setTextViewText(R.id.title_widget, schedule[position]["title"])
            view.setTextViewText(R.id.time_widget, schedule[position]["time"])
            view.setTextViewText(R.id.venue_widget, schedule[position]["venue"])

            val fillInIntent = Intent()
            view.setOnClickFillInIntent(R.id.widget_row, fillInIntent)
            return view
        }
        else{
            val view = RemoteViews(mContext.packageName, R.layout.timetable_widget_slot)
            view.setTextViewText(R.id.slot_widget, schedule[position]["slot"])
            return view
        }
    }

    override fun getCount(): Int = schedule.size

    override fun getViewTypeCount(): Int = 2

    override fun onDestroy() {
        //
    }

    private fun initData() {
        schedule.clear()
        val sharedPrefFile = "com.revit.schedule"

        val refSlots = Slots.getSlots()
        val refTime = Slots.getTime()

        val sharedPref = mContext.getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)
        val day = getWeekDay()
        for ((index, slots) in refSlots[day].withIndex()){
            var flag = true
            val data = HashMap<String,String>()
            for ((i, slot) in slots.split("/").withIndex()){
                var details = sharedPref.getStringSet(slot,null)
                if (details != null){
                    details = details.toSortedSet()
                    data["title"] = details.elementAt(1).substring(3)
                    data["time"] = refTime[i][index]
                    data["type"] = details.elementAt(2).substring(3)
                    data["venue"] = details.elementAt(3).substring(3)

                    schedule.add(data)
                    flag = false
                    break
                }
            }
            if (flag){
                data["slot"] = slots
                schedule.add(data)
            }
        }
        var emptyFlag = true
        for(i in schedule){
            if (i["slot"] == null){
                emptyFlag = false
            }
        }
        if (emptyFlag){
            schedule.clear()
        }
    }

    private fun getWeekDay(): Int {
        var wd = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 2
        if (wd<0)
            wd+=7
        return wd
    }
}