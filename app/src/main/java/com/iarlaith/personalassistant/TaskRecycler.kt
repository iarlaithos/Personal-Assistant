package com.iarlaith.personalassistant

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView

class TaskRecycler(private val tasks: List<Task>): RecyclerView.Adapter<TaskRecycler.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val taskTitle: TextView = view.findViewById(R.id.tvRecTitle)
        val taskDueDate : TextView = view.findViewById(R.id.tvRecDate)
        val taskType : TextView = view.findViewById(R.id.tvRecTaskType)
        val taskNote : TextView = view.findViewById(R.id.tvRecNotes)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_task_view, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.taskTitle.text = tasks[position].title
        var dueDate = tasks[position].dueDate.toString().split(" ")
        holder.taskDueDate.text = dueDate[0] + " " + dueDate[1] + " " + dueDate[2]
        holder.taskType.text = tasks[position].taskType
        holder.taskNote.text = (tasks[position].note).toString()

        holder.taskTitle.isVisible = true
        holder.taskDueDate.isVisible = true
        holder.taskType.isVisible = true
        holder.taskNote.isVisible = true

        }


    override fun getItemCount() = tasks.size
}