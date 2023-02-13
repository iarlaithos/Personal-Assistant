package com.iarlaith.personalassistant

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.io.File

class CorrectToDos {

    @RequiresApi(Build.VERSION_CODES.O)
    fun correctToDoFirebase(activity: Activity) {
        val userLocalTodo = getLocalTodoData(activity)
        getFirebaseTodoData { userCloudTodos ->
            if (userCloudTodos == null) {
                Log.d(ContentValues.TAG, "User cloud todos is null")
                return@getFirebaseTodoData
            }
            if (userLocalTodo != null) {
                if (userLocalTodo.toString() != userCloudTodos.toString() && userLocalTodo.size >= userCloudTodos.size) {
                    println("#############################")
                    println("Not equal")
                    println(userLocalTodo)
                    println(userCloudTodos)
                    println("#############################")
                    val differenceModules = userLocalTodo.minus(userCloudTodos)
                    for (diffModule in differenceModules) {
                        val userId = Firebase.auth.currentUser?.uid
                        val reference = FirebaseDatabase.getInstance().getReference("Todos")
                        if (userId != null) {
                            reference.child(userId).push().setValue(diffModule)
                                .addOnCompleteListener {
                                    println("write to DB Success")
                                }.addOnFailureListener { err ->
                                    println("write to DB Fail: $err")
                                }
                        }else{
                            println("UserId is null")
                        }
                    }

                } else {
                    println("#############################")
                    println("Equal")
                    println(userLocalTodo)
                    println(userCloudTodos)
                    println("#############################")
                }
            }
        }
    }

    private fun checkDataBase(context: Context): Boolean {
        val fullPath = "/data/data/com.iarlaith.personalassistant/databases/todo_database"
        val file: File = context.getDatabasePath(fullPath)
        return file.exists()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getLocalTodoData(activity: Activity): MutableList<ToDo>? {
        if(checkDataBase(activity)){
            //SQLite To-do Data
            val db: SQLiteDatabase = ToDoListSQLiteDBHelper(activity).readableDatabase
            val cursorTodos: Cursor = db.rawQuery("SELECT todo_name, todo_checked FROM ${ToDoListSQLiteDBHelper.TODO_TABLE}", null)
            val userLocalToDos: MutableList<ToDo> = ArrayList()
            if (cursorTodos.moveToFirst()) {
                do {
                    // on below line we are adding the data from cursor to our array list.
                    userLocalToDos.add(
                        ToDo(
                            cursorTodos.getString(0),
                            cursorTodos.getString(1).toBoolean()
                        )
                    )
                } while (cursorTodos.moveToNext())
            }
            cursorTodos.close()
            return userLocalToDos
        }
        return null
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getFirebaseTodoData(callback: (MutableList<ToDo>?) -> Unit) {
        //Firebase Data
        val currentUser = Firebase.auth.currentUser
        if (currentUser == null) {
            callback(null)
            return
        }
        val userId = currentUser.uid
        val rootRef = Firebase.database.reference
        val todosRef = rootRef.child("Todos").child(userId)
        println("Value event listener Test1 ")

        var fetched = false
        var vel = todosRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if ( fetched == false) {
                    fetched = true
                    val userCloudTodos = reformatFirebaseData(dataSnapshot)
                    println("Value event listener Test 3")
                    Log.i("Firebase Todos: ", userCloudTodos.toString())
                    callback(userCloudTodos.toMutableList())
                }

            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("Value event listener Test 4")
                Log.d(ContentValues.TAG, databaseError.message)
            }
        })

        todosRef.addValueEventListener(vel)
        fetched=false;
        todosRef.removeEventListener(vel)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun reformatFirebaseData(it: DataSnapshot): MutableList<ToDo> {
        val userCloudTodos :  MutableList<ToDo> = ArrayList()
        val children = it.children
        for (child in children){
            try {
                val value = child.value as HashMap<String, Any>
                val name = value["title"] as String
                val checked = value["checked"] as Boolean
                val todo = ToDo(name,checked)
                userCloudTodos.add(todo)
            } catch (e: Exception) {
                Log.e("firebase", "Error casting data", e)
            }
        }
        return userCloudTodos
    }
}