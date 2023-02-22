package com.iarlaith.personalassistant

import android.database.sqlite.SQLiteDatabase
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import java.time.LocalTime

class TaskRecycler(private val tasks: List<Task> , activity: ViewTasksActivity): RecyclerView.Adapter<TaskRecycler.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val taskTitle: TextView = view.findViewById(R.id.tvRecTask)
        val taskDueDate : TextView = view.findViewById(R.id.tvRecDate)
        val taskType : TextView = view.findViewById(R.id.tvRecTaskType)
        val taskNote : TextView = view.findViewById(R.id.tvRecNotes)
        val arrow : ImageView = view.findViewById(R.id.smallArrow)
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
        holder.taskType.isVisible = false
        holder.taskNote.isVisible = false

        var taskTitle = tasks[position].title
        var colour = "RED"
        val database: SQLiteDatabase = ModuleSQLiteDBHelper(holder.itemView.context).readableDatabase
        val cursorModule = database.rawQuery("SELECT colour \n" +
                "FROM module \n" +
                "INNER JOIN tasks ON module.module_id = tasks.module_id \n" +
                "WHERE module.module_id IN (SELECT module_id from module_sessions where task_title = '$taskTitle')", null)
        if (cursorModule.moveToFirst()) {
            colour = cursorModule.getString(0)
        }
        cursorModule.close()
        when (colour) {
            "RED" -> {
                holder.taskTitle.setBackgroundColor(
                    ContextCompat.getColor(
                        holder.taskTitle.context,
                        R.color.red
                    )
                )
                holder.taskType.setBackgroundColor(
                    ContextCompat.getColor(
                        holder.taskType.context,
                        R.color.red
                    )
                )
                holder.taskNote.setBackgroundColor(
                    ContextCompat.getColor(
                        holder.taskNote.context,
                        R.color.red
                    )
                )
            }
            "ORANGE" -> {
                holder.taskTitle.setBackgroundColor(
                    ContextCompat.getColor(
                        holder.taskTitle.context,
                        R.color.orange
                    )
                )
                holder.taskType.setBackgroundColor(
                    ContextCompat.getColor(
                        holder.taskType.context,
                        R.color.orange
                    )
                )
                holder.taskNote.setBackgroundColor(
                    ContextCompat.getColor(
                        holder.taskNote.context,
                        R.color.orange
                    )
                )
            }
            "YELLOW" ->{
                holder.taskTitle.setBackgroundColor(
                    ContextCompat.getColor(
                        holder.taskTitle.context,
                        R.color.yellow
                    )
                )
                holder.taskType.setBackgroundColor(
                    ContextCompat.getColor(
                        holder.taskType.context,
                        R.color.yellow
                    )
                )
                holder.taskNote.setBackgroundColor(
                    ContextCompat.getColor(
                        holder.taskNote.context,
                        R.color.yellow
                    )
                )
            }
            "GREEN" -> {
                holder.taskTitle.setBackgroundColor(
                    ContextCompat.getColor(
                        holder.taskTitle.context,
                        R.color.green
                    )
                )
                holder.taskType.setBackgroundColor(
                    ContextCompat.getColor(
                        holder.taskType.context,
                        R.color.green
                    )
                )
                holder.taskNote.setBackgroundColor(
                    ContextCompat.getColor(
                        holder.taskNote.context,
                        R.color.green
                    )
                )
            }
            "BLUE" -> {
                holder.taskTitle.setBackgroundColor(
                    ContextCompat.getColor(
                        holder.taskTitle.context,
                        R.color.blue
                    )
                )
                holder.taskType.setBackgroundColor(
                    ContextCompat.getColor(
                        holder.taskType.context,
                        R.color.blue
                    )
                )
                holder.taskNote.setBackgroundColor(
                    ContextCompat.getColor(
                        holder.taskNote.context,
                        R.color.blue
                    )
                )
            }
            "PURPLE" -> {
                holder.taskTitle.setBackgroundColor(
                    ContextCompat.getColor(
                        holder.taskTitle.context,
                        R.color.purple_500
                    )
                )
                holder.taskType.setBackgroundColor(
                    ContextCompat.getColor(
                        holder.taskType.context,
                        R.color.purple_500
                    )
                )
                holder.taskNote.setBackgroundColor(
                    ContextCompat.getColor(
                        holder.taskNote.context,
                        R.color.purple_500
                    )
                )
            }
            "PINK" -> {
                holder.taskTitle.setBackgroundColor(
                    ContextCompat.getColor(
                        holder.taskTitle.context,
                        R.color.pink
                    )
                )
                holder.taskType.setBackgroundColor(
                    ContextCompat.getColor(
                        holder.taskType.context,
                        R.color.pink
                    )
                )
                holder.taskNote.setBackgroundColor(
                    ContextCompat.getColor(
                        holder.taskNote.context,
                        R.color.pink
                    )
                )
            }
            "WHITE" -> {
                holder.taskTitle.setBackgroundColor(
                    ContextCompat.getColor(
                        holder.taskTitle.context,
                        R.color.white
                    )
                )
                holder.taskType.setBackgroundColor(
                    ContextCompat.getColor(
                        holder.taskType.context,
                        R.color.white
                    )
                )
                holder.taskNote.setBackgroundColor(
                    ContextCompat.getColor(
                        holder.taskNote.context,
                        R.color.white
                    )
                )
            }
            else -> {
                holder.taskTitle.setBackgroundColor(
                    ContextCompat.getColor(
                        holder.taskTitle.context,
                        R.color.white
                    )
                )
                holder.taskType.setBackgroundColor(
                    ContextCompat.getColor(
                        holder.taskType.context,
                        R.color.white
                    )
                )
                holder.taskNote.setBackgroundColor(
                    ContextCompat.getColor(
                        holder.taskNote.context,
                        R.color.white
                    )
                )
            }
        }

        holder.arrow.setOnClickListener {
            if(holder.arrow.rotation == 0F){
                holder.arrow.rotation = 90F
                holder.taskType.isVisible = true
                holder.taskNote.isVisible = true
            }else{
                holder.arrow.rotation = 0F
                holder.taskType.isVisible = false
                holder.taskNote.isVisible = false
            }
        }

    }


    override fun getItemCount() = tasks.size
}