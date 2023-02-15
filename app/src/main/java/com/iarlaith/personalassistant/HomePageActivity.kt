package com.iarlaith.personalassistant

import android.app.Activity
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Build
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_to_do_list.*
import java.time.LocalDate
import java.time.LocalTime


class HomePageActivity : AppCompatActivity() {
    private lateinit var todoAdaptor: ToDoAdaptor
    @RequiresApi(Build.VERSION_CODES.O)
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

        val prevDay = findViewById<ImageView>(R.id.btnPrevDay)
        val nextDay = findViewById<ImageView>(R.id.btnNextDay)

        val tvDay = findViewById<TextView>(R.id.tvDay)
        val rvModules = findViewById<RecyclerView>(R.id.recModules)
        rvModules.removeAllViews()
        var currentDay = LocalDate.now().dayOfWeek.name.uppercase()
        var daysList = listOf<String>("MONDAY","TUESDAY","WEDNESDAY","THURSDAY","FRIDAY","SATURDAY","SUNDAY")
        var dayIndex = daysList.indexOf(currentDay)
        var day = daysList[dayIndex]
        displayModules(day, this)
        nextDay.isVisible = true
        prevDay.isVisible = true
        rvModules.isVisible = true
        tvDay.isVisible = true

        menu.setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
        }

        mode.setOnCheckedChangeListener { _, isChecked ->
            rvToDoItems.isVisible = isChecked
            nextDay.isVisible = !isChecked
            prevDay.isVisible = !isChecked
            rvModules.isVisible = !isChecked
            tvDay.isVisible = !isChecked
        }

        prevDay.setOnClickListener{
            dayIndex --
            if(dayIndex == -1){
                dayIndex = 6
            }
            day = daysList[dayIndex]
            displayModules(day, this)
        }

        nextDay.setOnClickListener{
            dayIndex ++
            if(dayIndex == 7){
                dayIndex = 0
            }
            day = daysList[dayIndex]
            displayModules(day, this)
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun displayModules(day : String, activity : Activity){
        var viewDay = findViewById<TextView>(R.id.tvDay)
        val recyclerView = findViewById<RecyclerView>(R.id.recModules)

        viewDay.text = day
        var moduleList : ArrayList<Module> =ArrayList()
        val database: SQLiteDatabase = ModuleSQLiteDBHelper(activity).readableDatabase
        val cursorModule = database.rawQuery("SELECT module_name, colour, location, type, day, start_time, end_time FROM module INNER JOIN module_sessions ON module.module_id = module_sessions.module_id WHERE module.module_id IN (SELECT module_id from module_sessions where day = '$day') ORDER BY module_sessions.start_time", null)
        if (cursorModule.moveToFirst()) {
            do {
                var moduleName = cursorModule.getString(0)
                var moduleColour = cursorModule.getString(1)
                var location = cursorModule.getString(2)
                var type = cursorModule.getString(3)
                var dayOfTheWeek = cursorModule.getString(4)
                var startTime = cursorModule.getString(5)
                var endTime = cursorModule.getString(6)
                if(dayOfTheWeek == day){
                    moduleList.add(Module(moduleName,moduleColour,listOf(ModuleSession(location, type, day, LocalTime.parse(startTime),
                        LocalTime.parse(endTime))),null))
                }
            } while (cursorModule.moveToNext())
        }
        cursorModule.close()
        println("#######     VIEW MODULES     #######")
        if (moduleList != null) {
            for (module in moduleList) {
                println(module.toString())
            }
        }
        println("####################################")

        recyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = ModuleRecycler(moduleList)
    }
}