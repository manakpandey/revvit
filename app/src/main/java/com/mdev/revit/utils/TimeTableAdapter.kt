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
        val refSlots = SparseArray<List<String>>()
        refSlots.append(0, listOf(
            "A1/L1","F1/L2","D1/L3","TB1/L4","TG1/L5","/L6",
            "A2/L31","F2/L32","D2/L33","TB2/L34","TG2/L35","/L36",
            "H1/L61","K1/L62"
        ))
        refSlots.append(1,listOf(
            "B1/L7","G1/L8","E1/L9","TC1/L10","TAA1/L11","/L12",
            "B2/L37","G2/L38","E2/L39","TC2/L40","TAA2/L41","/L42",
            "H2/L63","K2/L64"
        ))
        refSlots.append(2, listOf(
            "C1/L13","A1/L14","F1/L15","/L16","/L17","/L18",
            "C2/L43","A2/L44","F2/L45","TD2/L46","TBB2/L47","/L48",
            "H3/L65","K3/L66"
        ))
        refSlots.append(3,listOf(
            "D1/L19","B1/L20","G1/L21","TE1/L22","TCC1/L23","/L24",
            "D2/L49","B2/L50","G2/L51","TE2/L52","TCC2/L53","/L54",
            "H4/L67","K4/L68"
        ))
        refSlots.append(4, listOf(
            "E1/L25","C1/L26","TA1/L27","TF1/L28","TD1/L29","/L30",
            "E2/L55","C2/L56","TA2/L57","TF2/L58","TDD2/L59","/L60",
            "H5/L69","K5/L70"
        ))
        refSlots.append(5, listOf())
        refSlots.append(6, listOf())

        val refTime = listOf(listOf(
            "08:00 - 08:50","08:55 - 09:45","09:50 - 10:40","10:45 - 11:35","11:40 - 12:30","",
            "14:00 - 14:50","14:55 - 15:45","15:50 - 16:40","16:45 - 17:35","17:40 - 18:30","",
            "19:00 - 19:50","20:00 - 20:50"
        )
            , listOf(
                "08:00 - 08:50","08:50 - 09:40","09:50 - 10:40","10:40 - 11:30","11:40 - 12:30","12:30 - 13:20",
                "14:00 - 14:50","14:50 - 15:40","15:50 - 16:40","16:40 - 17:30","17:40 - 18:30","18:30 - 19:20",
                "19:30 - 20:20","20:20 - 21:10"
            ))


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
