package com.iarlaith.personalassistant

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_to_do_list.*


class HomePageActivity : AppCompatActivity() {
    private lateinit var todoAdaptor: ToDoAdaptor
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)

        val todoActivity = ToDoListActivity()

        val mode = findViewById<Switch>(R.id.switchMode)
        val menu = findViewById<ImageButton>(R.id.btnMenu)

        todoAdaptor = ToDoAdaptor(mutableListOf())
        rvToDoItems.adapter = todoAdaptor
        rvToDoItems.layoutManager = LinearLayoutManager(this)
        todoAdaptor.clearToDos(this)
        todoActivity.displayToDos(todoAdaptor, this)
        todoActivity.printToDos(this)
        rvToDoItems.isVisible = false

        menu.setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
        }

        mode.setOnCheckedChangeListener { buttonView, isChecked ->
            rvToDoItems.isVisible = isChecked
        }

    }
}