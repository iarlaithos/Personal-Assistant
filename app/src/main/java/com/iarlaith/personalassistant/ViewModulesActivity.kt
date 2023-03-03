package com.iarlaith.personalassistant

import android.app.Activity
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.time.LocalDate
import java.time.LocalTime
import java.util.*
import kotlin.collections.ArrayList

class ViewModulesActivity : AppCompatActivity() {
    private val SPEECH_REC = 110
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_modules)

        val homeButton = findViewById<ImageButton>(R.id.vmbtnHome)
        val menuButton = findViewById<ImageButton>(R.id.vmbtnMenu)
        val prevDay = findViewById<ImageView>(R.id.btnPrevDay)
        val nextDay = findViewById<ImageView>(R.id.btnNextDay)
        val micButton = findViewById<ImageView>(R.id.micButton)


        var currentDay = LocalDate.now().dayOfWeek.name.uppercase()
        var daysList = listOf<String>("MONDAY","TUESDAY","WEDNESDAY","THURSDAY","FRIDAY","SATURDAY","SUNDAY")
        var dayIndex = daysList.indexOf(currentDay)
        var day = daysList[dayIndex]

        displayModules(day, this)


        homeButton.setOnClickListener {
            val intent = Intent(this, HomePageActivity::class.java)
            startActivity(intent)
        }

        menuButton.setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
        }

        micButton.setOnClickListener {
            askSpeechInput()
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == SPEECH_REC && resultCode == Activity.RESULT_OK){
            val result = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val input = result?.get(0).toString()
            println(input)
            val filenames = listOf(
                "AddModule.txt",
                "AddTask.txt",
                "EditModuleActivity.txt",
                "EditTaskActivity.txt",
                "HomePageActivity.txt",
                "MenuActivity.txt",
                "ModulesMenu.txt",
                "TaskMenu.txt",
                "ToDoListActivity.txt",
                "ViewModulesActivity.txt",
                "ViewTasksActivity.txt",
            )

            val counts = SpeechInputHandler.countOccurrences(this, input, filenames)
            val maxFilename = SpeechInputHandler.getMaxCountFilename(counts)
            println("File with most occurrences of input string: $maxFilename")
            println("**************** SPEECH INPUT ********************")

            if(maxFilename.equals("Not Sure")){
                Toast.makeText(
                    this,
                    maxFilename,
                    Toast.LENGTH_SHORT
                ).show()
            }else{
                val destination = maxFilename
                var intent = Intent(this, this::class.java)
                when (destination) {
                    "AddModule.txt" -> intent = Intent(this, AddModule::class.java)
                    "AddTask.txt" -> intent = Intent(this, AddTask::class.java)
                    "EditModuleActivity.txt" -> intent = Intent(this, EditModuleActivity::class.java)
                    "EditTaskActivity.txt" -> intent = Intent(this, EditTaskActivity::class.java)
                    "HomePageActivity.txt" -> intent = Intent(this, HomePageActivity::class.java)
                    "MenuActivity.txt" -> intent = Intent(this, MenuActivity::class.java)
                    "ModulesMenu.txt" -> intent = Intent(this, ModulesMenu::class.java)
                    "TaskMenu.txt" -> intent = Intent(this, TaskMenu::class.java)
                    "ToDoListActivity.txt" -> intent = Intent(this, ToDoListActivity::class.java)
                    "ViewModulesActivity.txt" -> intent = Intent(this, ViewModulesActivity::class.java)
                    "ViewTasksActivity.txt" -> intent = Intent(this, ViewTasksActivity::class.java)
                    else -> { // Note the block
                        Toast.makeText(
                            this,
                            maxFilename,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                startActivity(intent)
            }

        }
    }

    private fun askSpeechInput() {
        if(!SpeechRecognizer.isRecognitionAvailable(this)) {
            println("Speech recognition is not available")
        }else{
            val i = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            i.putExtra(RecognizerIntent.EXTRA_PROMPT, "What can I help you with?")
            startActivityForResult(i, SPEECH_REC)
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
                    moduleList.add(Module(moduleName,moduleColour,listOf(ModuleSession(location, type, day, LocalTime.parse(startTime),LocalTime.parse(endTime))), null))
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