package com.mdev.revit.utils

import android.content.Context
import android.os.Bundle
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mdev.revit.R
import kotlinx.android.synthetic.main.tt_day.*

class TimeTableAdapter(childFragmentManager: FragmentManager) : FragmentPagerAdapter(childFragmentManager) {

    private val title = listOf("Mon","Tue","Wed","Thu","Fri","Sat","Sun")
    override fun getItem(position: Int): Fragment = TabFragment.getInstance(position)

    override fun getCount(): Int = title.size

    override fun getPageTitle(position: Int): CharSequence? {
        return title[position]
    }

}

class TabFragment: Fragment(){
    private var position: Int? = 0
    companion object {
        fun getInstance(position: Int): Fragment {
            val bundle = Bundle()
            bundle.putInt("pos", position)
            val tabFragment = TabFragment()
            tabFragment.arguments = bundle
            return tabFragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        position = arguments?.getInt("pos")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.tt_day, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val adapter = TimeTableInflateAdapter()
        adapter.setData(setupTimeTable(position))
        tt_days.layoutManager = LinearLayoutManager(activity)
        tt_days.adapter = adapter
        tt_days.setHasFixedSize(true)
    }

    private fun setupTimeTable(pos: Int?): List<HashMap<String, String>>{

        val refSlots = Slots.getSlots()
        val refTime = Slots.getTime()


        val schedule = mutableListOf(HashMap<String, String>())
        val sharedPref = activity?.getSharedPreferences(getString(R.string.shared_preference_schedule), Context.MODE_PRIVATE)
        val sharedPrefData = sharedPref?.all
        val keys = sharedPrefData?.keys

        for ((i,slot) in refSlots[pos!!].withIndex()){
            val data = HashMap<String, String>()
            for (key in keys!!){
                val slots = slot.split("/")
                if (slots.contains(key)){
                    val type = slots.indexOf(key)
                    val details = sharedPref.getStringSet(key, null)!!.toSortedSet()
                    data["courseTitle"] = details.elementAt(1).substring(3)
                    data["courseTypeFac"] = "${details.elementAt(2).substring(3)} - ${details.elementAt(4).substring(3)}"
                    data["courseVenue"] = details.elementAt(3).substring(3)
                    data["courseTime"] = refTime[type][i]
                    data["flag"]="0"
                    schedule.add(data)
                    break
                }
            }
        }

        return schedule
    }
}

class TimeTableInflateAdapter : RecyclerView.Adapter<TimeTableInflateAdapter.ViewHolder>() {

    private var data: List<HashMap<String, String>> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            when(itemCount==1){
                true->{
                    LayoutInflater.from(parent.context).inflate(R.layout.no_class, parent, false)
                }
                false-> {
                    LayoutInflater.from(parent.context).inflate(R.layout.tt_row, parent, false)
                }

        }
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (itemCount>1) {
            when (data[position]["flag"]) {
                "0" -> {
                    holder.courseName?.text = data[position]["courseTitle"]
                    holder.courseTypeFac?.text = data[position]["courseTypeFac"]
                    holder.courseTime?.text = data[position]["courseTime"]
                    holder.courseVenue?.text = data[position]["courseVenue"]
                }
                else -> {
                    holder.itemView.visibility = View.GONE
                    holder.courseName?.visibility = View.GONE
                    holder.courseTypeFac?.visibility = View.GONE
                    holder.courseTime?.visibility = View.GONE
                    holder.courseVenue?.visibility = View.GONE

                }
            }
        }
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val courseName = itemView.findViewById<TextView?>(R.id.course_name)
        val courseTypeFac = itemView.findViewById<TextView?>(R.id.course_type_fac)
        val courseTime = itemView.findViewById<TextView?>(R.id.course_time)
        val courseVenue = itemView.findViewById<TextView?>(R.id.course_venue)
    }

    fun setData(data: List<HashMap<String, String>>){
        this.data = data
    }

}
