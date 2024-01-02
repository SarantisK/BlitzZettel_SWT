package com.example.blitzzettel

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

/**
 * ZettelAdapter ist zuständig für das Anzeigen von Zettel-Objekten in einer RecyclerView.
 * Er verbindet die Datenliste mit den ViewHoldern, die die individuellen Elemente darstellen.
 *
 * @param zettelList Liste der Zettel, die in der RecyclerView angezeigt werden.
 * @param onClick Lambda-Funktion, die bei Klick auf ein Zettel-Element aufgerufen wird.
 */
class ZettelAdapter(var zettelList: List<Zettel>, private val onClick: (Zettel) -> Unit) : RecyclerView.Adapter<ZettelAdapter.ViewHolder>() {

    /**
     * ViewHolder ist eine innere Klasse, die die Views für ein einzelnes Zettel-Element hält.
     * Sie definiert, wie jedes Element der RecyclerView aussehen soll.
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleView: TextView = view.findViewById(R.id.text_zettel_title)
    }

    // onCreateViewHolder wird aufgerufen, um einen neuen ViewHolder zu erstellen,
    // der ein einzelnes Element der Liste repräsentiert.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Layout für einzelne Zettel wird inflated
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_zettel, parent, false)
        return ViewHolder(view)
    }

    // onBindViewHolder bindet die Daten eines einzelnen Zettel-Objekts an einen ViewHolder
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Holt den Zettel, der an dieser Position dargestellt wird
        val zettel = zettelList[position]

        holder.titleView.text = "${zettel.title}"

        // Setzt einen Click-Listener auf die titleView-View, um die onClick-Funktion auszulösen.
        // Das entsprechenden Zettel-Objekt wird der onClick Funktion als Argument übergeben.
        holder.titleView.setOnClickListener { onClick(zettel) }
    }

    // Gibt die Anzahl der Elemente in der Datenliste zurück
    override fun getItemCount() = zettelList.size
}