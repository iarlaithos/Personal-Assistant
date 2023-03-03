package com.iarlaith.personalassistant

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_to_do_list.*
import java.time.LocalTime
import java.util.*
import kotlin.collections.ArrayList

class ToDoListActivity : AppCompatActivity() {
    private lateinit var todoAdaptor: ToDoAdaptor
    private lateinit var dbRef: DatabaseReference
    private val SPEECH_REC = 109

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_to_do_list)
        todoAdaptor = ToDoAdaptor(mutableListOf())
        val menuButton = findViewById<ImageView>(R.id.vmbtnMenu)
        val homeButton = findViewById<ImageView>(R.id.vmbtnHome)
        val micButton = findViewById<ImageView>(R.id.micButton)

        todoAdaptor.clearToDos(this)
        rvToDoItems.adapter = todoAdaptor
        rvToDoItems.layoutManager = LinearLayoutManager(this)
        displayToDos(todoAdaptor, this)
        printToDos(this)

        btnAddToDo.setOnClickListener {
            val todoTitle = etToDoTitle.text.toString()
            if(todoTitle.isNotEmpty()){
                val todo = ToDo(todoTitle, false)
                todoAdaptor.addToDo(todo)
                val userId = Firebase.auth.currentUser!!.uid
                writetoCloudDB(todo, userId)
                writetoSQLite(todo, this)
                etToDoTitle.text.clear()
            }
        }

        btnDeleteDoneToDo.setOnClickListener {
            todoAdaptor.deleteDoneToDos(this)
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

    fun displayToDos(todoAdaptor : ToDoAdaptor, activity : Activity){
        val database: SQLiteDatabase = ToDoListSQLiteDBHelper(activity).readableDatabase
        val cursor = database.rawQuery("SELECT todo_name, todo_checked FROM todo", null)
        if (cursor.moveToFirst()) {
            do {
                var todoTitle = cursor.getString(0)
                var todoIsChecked = cursor.getString(1) == "1"
                println(todoIsChecked)
                var todo  = ToDo(todoTitle, todoIsChecked)
                todoAdaptor.addToDo(todo)
            } while (cursor.moveToNext())
        }
        cursor.close()
        database.close()
    }

    fun printToDos(activity : Activity){
        var list = ArrayList<ToDo>()
        val database: SQLiteDatabase = ToDoListSQLiteDBHelper(activity).readableDatabase
        val cursor = database.rawQuery("SELECT todo_name, todo_checked FROM todo", null)
        if (cursor.moveToFirst()) {
            do {
                var todoTitle = cursor.getString(0)
                var todoIsChecked = cursor.getString(1).toBoolean()
                var todo  = ToDo(todoTitle, todoIsChecked)
                list.add(todo)
            } while (cursor.moveToNext())
        }
        cursor.close()
        database.close()
        println(list.toString())
    }

    fun writetoSQLite(todo : ToDo, activity : Activity){
        val database: SQLiteDatabase = ToDoListSQLiteDBHelper(activity).writableDatabase
        val values = ContentValues()
        values.put(
            ToDoListSQLiteDBHelper.TODO_COLUMN_NAME,
            todo.title
        )
        values.put(
            ToDoListSQLiteDBHelper.TODO_COLUMN_ISCHECKED,
            todo.isChecked
        )
        database.insert(ToDoListSQLiteDBHelper.TODO_TABLE, null, values)
        database.close()
    }

    fun writetoCloudDB(todo : ToDo, userId : String){
        Thread.sleep(2000)
        dbRef = FirebaseDatabase.getInstance().getReference("Todos")
        dbRef.child(userId).push().setValue(todo)
            .addOnCompleteListener{
                println("write to DB Success")
            }.addOnFailureListener{ err ->
                println("write to DB Fail: $err")
            }
    }
}