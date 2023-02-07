package com.iarlaith.personalassistant

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class ModuleRecycler(private val modules: List<Module>) : RecyclerView.Adapter<ModuleRecycler.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val module: TextView = view.findViewById(R.id.tvRecModule)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_module_view, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.module.text = modules[position].name
        val colour = modules[position].colour
        when (colour) {
            "RED" -> holder.module.setBackgroundColor(
                ContextCompat.getColor(
                    holder.module.context,
                    R.color.red
                )
            )
            "ORANGE" -> holder.module.setBackgroundColor(
                ContextCompat.getColor(
                    holder.module.context,
                    R.color.orange
                )
            )
            "YELLOW" -> holder.module.setBackgroundColor(
                ContextCompat.getColor(
                    holder.module.context,
                    R.color.yellow
                )
            )
            "GREEN" -> holder.module.setBackgroundColor(
                ContextCompat.getColor(
                    holder.module.context,
                    R.color.green
                )
            )
            "BLUE" -> holder.module.setBackgroundColor(
                ContextCompat.getColor(
                    holder.module.context,
                    R.color.blue
                )
            )
            "PURPLE" -> holder.module.setBackgroundColor(
                ContextCompat.getColor(
                    holder.module.context,
                    R.color.purple_500
                )
            )
            "PINK" -> holder.module.setBackgroundColor(
                ContextCompat.getColor(
                    holder.module.context,
                    R.color.pink
                )
            )
            "WHITE" -> holder.module.setBackgroundColor(
                ContextCompat.getColor(
                    holder.module.context,
                    R.color.white
                )
            )
            else -> holder.module.setBackgroundColor(
                ContextCompat.getColor(
                    holder.module.context,
                    R.color.white
                )
            )
        }
    }

    override fun getItemCount() = modules.size
}
