package com.iarlaith.personalassistant

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import org.w3c.dom.Text

class TaskMenu : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_menu)

        val addTaskbtn = findViewById<TextView>(R.id.tvAddTask)


        addTaskbtn.setOnClickListener {
            val intent = Intent(this, AddTask::class.java)
            startActivity(intent)
        }
    }
}