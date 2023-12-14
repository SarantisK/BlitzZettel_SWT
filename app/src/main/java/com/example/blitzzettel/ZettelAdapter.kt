package com.example.blitzzettel

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ZettelAdapter(var zettelList: List<Zettel>, private val onClick: (Zettel) -> Unit) : RecyclerView.Adapter<ZettelAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleView: TextView = view.findViewById(R.id.text_zettel_title)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_zettel, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val zettel = zettelList[position]
        holder.titleView.text = zettel.title
        holder.titleView.setOnClickListener { onClick(zettel) }
        //holder.itemView.setOnClickListener { onClick(zettel) }
    }

    override fun getItemCount() = zettelList.size
}