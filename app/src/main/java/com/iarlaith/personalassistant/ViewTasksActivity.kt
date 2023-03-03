package com.iarlaith.personalassistant

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Build
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*
import kotlin.collections.ArrayList

class ViewTasksActivity : AppCompatActivity() {
    private val SPEECH_REC = 111
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_tasks)

        val homeButton = findViewById<ImageButton>(R.id.vtbtnHome)
        val menuButton = findViewById<ImageButton>(R.id.vtbtnMenu)
        val btnAddTask = findViewById<Button>(R.id.btnAddTask)
        val btnDeleteDone = findViewById<Button>(R.id.btnDeleteTask)
        val taskRecycler = TaskRecycler(mutableListOf())
        val micButton = findViewById<ImageView>(R.id.micButton)


        displayTasks(this)

        btnDeleteDone.setOnClickListener {
            deleteDoneTasks(this)
            displayTasks(this)
        }

        btnAddTask.setOnClickListener {
            val intent = Intent(this, AddTask::class.java)
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
                var taskIsChecked = cursor.getString(4) == "true"
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
        recyclerView.adapter = TaskRecycler(tasks)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun deleteDoneTasks(context : Context) {
        val userId = Firebase.auth.currentUser!!.uid
        val db: SQLiteDatabase = ModuleSQLiteDBHelper(context).writableDatabase
        db.execSQL( "DELETE FROM tasks WHERE is_checked = 'true'")
        val dbRef = FirebaseDatabase.getInstance().getReference("Modules")
        val queryRef: Query =
            dbRef.child(userId).orderByValue()

        queryRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChild: String?) {
                for (postSnapshot in snapshot.children) {
                    for(ps in  postSnapshot.children){
                        if(ps.child("checked").value == true){
                            ps.ref.setValue(null)
                        }
                    }
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
}