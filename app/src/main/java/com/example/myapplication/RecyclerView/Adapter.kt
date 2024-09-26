package com.example.myapplication.RecyclerView

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.ColorItem
import com.example.myapplication.R

class ColorAdapter(private val colorItems: List<ColorItem>) :
    RecyclerView.Adapter<ColorAdapter.ColorViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_color, parent, false)
        return ColorViewHolder(view)
    }

    override fun onBindViewHolder(holder: ColorViewHolder, position: Int) {
        holder.colorView.setBackgroundColor(colorItems[position].color)
    }

    override fun getItemCount(): Int = colorItems.size

    inner class ColorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val colorView: View = itemView.findViewById(R.id.color_view)
    }
}