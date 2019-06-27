package com.mdev.revit

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.QuerySnapshot

class ReviewOnlyAdapter(private val documents : QuerySnapshot): RecyclerView.Adapter<ReviewOnlyAdapter.ViewHolder>() {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val view: View = LayoutInflater.from(p0.context).inflate(R.layout.fac_review_card,p0,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = documents.size()

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.reviewOnly?.text = documents.documents[position].get("review").toString()
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val reviewOnly = itemView.findViewById<TextView?>(R.id.reviewOnly)
    }
}