package com.iarlaith.personalassistant

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import org.w3c.dom.Text
import java.util.*

class TaskMenu : AppCompatActivity() {
    private val SPEECH_REC = 108
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_menu)

        val addTaskbtn = findViewById<TextView>(R.id.tvAddTask)
        val viewTasksbtn = findViewById<TextView>(R.id.tvViewTasks)
        val editTaskbtn = findViewById<TextView>(R.id.tvEditTask)
        val homeButton = findViewById<ImageButton>(R.id.tmbtnHome)
        val menuButton = findViewById<ImageButton>(R.id.tmbtnMenu)
        val micButton = findViewById<ImageView>(R.id.micButton)


        addTaskbtn.setOnClickListener {
            val intent = Intent(this, AddTask::class.java)
            startActivity(intent)
        }

        viewTasksbtn.setOnClickListener {
            val intent = Intent(this, ViewTasksActivity::class.java)
            startActivity(intent)
        }

        editTaskbtn.setOnClickListener {
            val intent = Intent(this, EditTaskActivity::class.java)
            startActivity(intent)
        }

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
}