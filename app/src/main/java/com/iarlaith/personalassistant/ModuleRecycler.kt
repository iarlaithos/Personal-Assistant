package com.iarlaith.personalassistant

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView

class ModuleRecycler(private val modules: List<Module>) : RecyclerView.Adapter<ModuleRecycler.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val module: TextView = view.findViewById(R.id.tvRecModule)
        val startTime : TextView = view.findViewById(R.id.tvViewStartTime)
        val endTime : TextView = view.findViewById(R.id.tvViewEndTime)
        val arrow : ImageView = view.findViewById(R.id.smallArrow)
        val location : TextView = view.findViewById(R.id.tvRecLocation)
        val type : TextView = view.findViewById(R.id.tvRecType)
        val timeArrow : ImageView = view.findViewById(R.id.timeArrow)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_module_view, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.module.text = modules[position].name
        holder.startTime.text = modules[position].moduleSessions[0].startTime.toString()
        holder.endTime.text = modules[position].moduleSessions[0].endTime.toString()
        holder.location.text = modules[position].moduleSessions[0].location.toString()
        holder.type.text = modules[position].moduleSessions[0].sessionType.toString()

        holder.location.isVisible = false
        holder.type.isVisible = false
        holder.endTime.isVisible = false
        holder.timeArrow.isVisible = false
        val colour = modules[position].colour
        when (colour) {
            "RED" -> {
                holder.module.setBackgroundColor(
                ContextCompat.getColor(
                    holder.module.context,
                    R.color.red
                    )
                )
                holder.location.setBackgroundColor(
                    ContextCompat.getColor(
                        holder.location.context,
                        R.color.red
                    )
                )
                holder.type.setBackgroundColor(
                    ContextCompat.getColor(
                        holder.type.context,
                        R.color.red
                    )
                )
            }
            "ORANGE" -> {
                holder.module.setBackgroundColor(
                    ContextCompat.getColor(
                        holder.module.context,
                        R.color.orange
                    )
                )
                holder.location.setBackgroundColor(
                    ContextCompat.getColor(
                        holder.location.context,
                        R.color.orange
                    )
                )
                holder.type.setBackgroundColor(
                    ContextCompat.getColor(
                        holder.type.context,
                        R.color.orange
                    )
                )
            }
            "YELLOW" ->{
                holder.module.setBackgroundColor(
                    ContextCompat.getColor(
                        holder.module.context,
                        R.color.yellow
                    )
                )
                holder.location.setBackgroundColor(
                    ContextCompat.getColor(
                        holder.location.context,
                        R.color.yellow
                    )
                )
                holder.type.setBackgroundColor(
                    ContextCompat.getColor(
                        holder.type.context,
                        R.color.yellow
                    )
                )
            }
            "GREEN" -> {
                holder.module.setBackgroundColor(
                    ContextCompat.getColor(
                        holder.module.context,
                        R.color.green
                    )
                )
                holder.location.setBackgroundColor(
                    ContextCompat.getColor(
                        holder.location.context,
                        R.color.green
                    )
                )
                holder.type.setBackgroundColor(
                    ContextCompat.getColor(
                        holder.type.context,
                        R.color.green
                    )
                )
            }
            "BLUE" -> {
                holder.module.setBackgroundColor(
                    ContextCompat.getColor(
                        holder.module.context,
                        R.color.blue
                    )
                )
                holder.location.setBackgroundColor(
                    ContextCompat.getColor(
                        holder.location.context,
                        R.color.blue
                    )
                )
                holder.type.setBackgroundColor(
                    ContextCompat.getColor(
                        holder.type.context,
                        R.color.blue
                    )
                )
            }
            "PURPLE" -> {
                holder.module.setBackgroundColor(
                    ContextCompat.getColor(
                        holder.module.context,
                        R.color.purple_500
                    )
                )
                holder.location.setBackgroundColor(
                    ContextCompat.getColor(
                        holder.location.context,
                        R.color.purple_500
                    )
                )
                holder.type.setBackgroundColor(
                    ContextCompat.getColor(
                        holder.type.context,
                        R.color.purple_500
                    )
                )
            }
            "PINK" -> {
                holder.module.setBackgroundColor(
                    ContextCompat.getColor(
                        holder.module.context,
                        R.color.pink
                    )
                )
                holder.location.setBackgroundColor(
                    ContextCompat.getColor(
                        holder.location.context,
                        R.color.pink
                    )
                )
                holder.type.setBackgroundColor(
                    ContextCompat.getColor(
                        holder.type.context,
                        R.color.pink
                    )
                )
            }
            "WHITE" -> {
                holder.module.setBackgroundColor(
                    ContextCompat.getColor(
                        holder.module.context,
                        R.color.white
                    )
                )
                holder.location.setBackgroundColor(
                    ContextCompat.getColor(
                        holder.location.context,
                        R.color.white
                    )
                )
                holder.type.setBackgroundColor(
                    ContextCompat.getColor(
                        holder.type.context,
                        R.color.white
                    )
                )
            }
            else -> {
                holder.module.setBackgroundColor(
                    ContextCompat.getColor(
                        holder.module.context,
                        R.color.white
                    )
                )
                holder.location.setBackgroundColor(
                    ContextCompat.getColor(
                        holder.location.context,
                        R.color.white
                    )
                )
                holder.type.setBackgroundColor(
                    ContextCompat.getColor(
                        holder.type.context,
                        R.color.white
                    )
                )
            }
        }

        holder.arrow.setOnClickListener {
            if(holder.arrow.rotation == 0F){
                holder.arrow.rotation = 90F
                holder.location.isVisible = true
                holder.type.isVisible = true
                holder.endTime.isVisible = true
                holder.timeArrow.isVisible = true
            }else{
                holder.arrow.rotation = 0F
                holder.location.isVisible = false
                holder.type.isVisible = false
                holder.endTime.isVisible = false
                holder.timeArrow.isVisible = false
            }
        }
    }

    override fun getItemCount() = modules.size
}
