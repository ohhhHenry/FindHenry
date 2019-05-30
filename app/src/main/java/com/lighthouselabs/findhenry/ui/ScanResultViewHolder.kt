package com.lighthouselabs.findhenry.ui

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ScanResultViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val device: TextView = itemView.findViewById(android.R.id.text1)
    val rssi: TextView = itemView.findViewById(android.R.id.text2)
}