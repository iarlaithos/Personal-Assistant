package com.iarlaith.personalassistant

import android.app.Activity
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Build
import android.os.Bundle
import android.widget.ImageButton
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*
import kotlin.collections.ArrayList

class ViewTasksActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_tasks)

        val homeButton = findViewById<ImageButton>(R.id.vtbtnHome)
        val menuButton = findViewById<ImageButton>(R.id.vtbtnMenu)

        displayTasks(this)


        homeButton.setOnClickListener {
            val intent = Intent(this, HomePageActivity::class.java)
            startActivity(intent)
        }

        menuButton.setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun displayTasks(activity : Activity){
        val recyclerView = findViewById<RecyclerView>(R.id.recTasks)

        var tasks : ArrayList<Task> =ArrayList()
        val database: SQLiteDatabase = ModuleSQLiteDBHelper(activity).readableDatabase
        val cursor = database.rawQuery("SELECT task_title, type, due_date, note, is_checked FROM tasks ", null)
        if (cursor.moveToFirst()) {
            do {
                var taskTitle = cursor.getString(0)
                var taskType = cursor.getString(1)
                var taskDueDate = cursor.getString(2)
                var taskNote = cursor.getString(3)
                var taskIsChecked = cursor.getString(4) == "1"
                var formatter: DateFormat = SimpleDateFormat("EEE MMM dd HH:mm:ss zzzz yyyy")
                var dueDate = formatter.parse(taskDueDate)
                tasks.add(Task(taskTitle,taskType,dueDate,taskNote,taskIsChecked))
            } while (cursor.moveToNext())
        }
        cursor.close()
        println("#######     VIEW TASKS     #######")
        if (tasks != null) {
            for (task in tasks) {
                println(task.toString())
            }
        }
        println("####################################")

        recyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = TaskRecycler(tasks, this)

    }
}