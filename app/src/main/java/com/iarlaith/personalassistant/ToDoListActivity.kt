package com.iarlaith.personalassistant

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_to_do_list.*
import java.time.LocalTime

class ToDoListActivity : AppCompatActivity() {
    private lateinit var todoAdaptor: ToDoAdaptor
    private lateinit var dbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_to_do_list)
        todoAdaptor = ToDoAdaptor(mutableListOf())
        val menuButton = findViewById<ImageView>(R.id.vmbtnMenu)
        val homeButton = findViewById<ImageView>(R.id.vmbtnHome)
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
    }

    fun checkCloudToDos(todoAdaptor : ToDoAdaptor, activity : Activity){

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