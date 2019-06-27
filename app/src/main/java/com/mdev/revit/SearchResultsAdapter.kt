package com.mdev.revit

import android.app.ActivityOptions
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mdev.revit.roomDatabase.FacDetails

class SearchResultsAdapter : RecyclerView.Adapter<SearchResultsAdapter.ViewHolder>() {

    private var results = emptyList<FacDetails>()
    lateinit var activity: SearchActivity
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.search_result, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = results.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (results.isNotEmpty()){
            holder.facName?.text = results[position].name
            holder.facSchool?.text = results[position].school
            holder.itemView.setOnClickListener{v ->

                val bundle = ActivityOptions.makeSceneTransitionAnimation(activity).toBundle()
                val intent = Intent(v.context, FacDetailsActivity::class.java)
                intent.putExtra("faculty",results[position].name)
                intent.putExtra("school",results[position].school)
                intent.putExtra("designation",results[position].designation)
                v.context.startActivity(intent,bundle)
//                v.context.startActivity<FacDetailsActivity>("faculty" to results[position].name,
//                    "school" to results[position].school,
//                    "designation" to results[position].designation)
            }
        }
    }


    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val facName = itemView.findViewById<TextView?>(R.id.fac_name_search)
        val facSchool = itemView.findViewById<TextView?>(R.id.fac_school_search)
    }

    fun setActivityForTransition(activity: SearchActivity){
        this.activity = activity
    }

    fun setResults(results: List<FacDetails>){
        this.results = results
        notifyDataSetChanged()
    }
}
