package com.iarlaith.personalassistant

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.ContentValues
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Build
import android.os.Bundle
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.*

class AddTask : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_task)

        val taskTitle = findViewById<EditText>(R.id.etAddTaskTitle)
        val dueDate = findViewById<TextView>(R.id.tvDueDate)
        val notes = findViewById<EditText>(R.id.addNote)
        val addTaskbtn = findViewById<Button>(R.id.addTaskbtn)
        val taskType = findViewById<Spinner>(R.id.taskTypeSpinner)

        val formatter = SimpleDateFormat("dd/MM/yyyy")
        var inputDate: Date = Date(System.currentTimeMillis())
        val inputDueDate = formatter.format(inputDate)
        var dueDateText = inputDueDate.toString()
        dueDate.text = "Due Date : $dueDateText"
        val moduleEnumArray = ArrayList<String>()
        val db: SQLiteDatabase = ModuleSQLiteDBHelper(this).readableDatabase
        val cursor = db.rawQuery( "SELECT module_name FROM module",null)
        if (cursor.moveToFirst()) {
            do {
                var moduleEnumEntry = cursor.getString(0)
                moduleEnumArray.add(moduleEnumEntry)
            } while (cursor.moveToNext())
        }
        cursor.close()

        var moduleSpinner = findViewById<Spinner>(R.id.selModuleSpinner)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, moduleEnumArray)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        moduleSpinner.adapter = adapter

        val taskTypeArray = Task.TaskType.values()
        val taskTypeNames = taskTypeArray.map { it.name }
        val taskTypeAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, taskTypeNames)
        taskTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        taskType.adapter = taskTypeAdapter

        dueDate.setOnClickListener {
            val calendar : Calendar = Calendar.getInstance()
            var year = calendar.get(Calendar.YEAR)
            var month = calendar.get(Calendar.MONTH)
            var date = calendar.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, date ->
                var monthPicked : String = (monthOfYear + 1).toString()
                var datePicked :String = (date).toString()
                if(monthOfYear < 9){
                    monthPicked = "0$monthPicked"
                }
                if(date < 10){
                    datePicked = "0$datePicked"
                }
                dueDate.text = "Due Date : $datePicked/$monthPicked/$year"
                println(monthOfYear)
                inputDate =
                    formatter.parse("$datePicked/$monthPicked/$year") as Date//.of(year, monthOfYear+1, date)
                println(inputDueDate)

            }, year, month, date)
            datePicker.show()
        }

        addTaskbtn.setOnClickListener{
            val task = Task(taskTitle.text.toString(), taskType.selectedItem.toString(), inputDate, notes.text.toString(), false)
            val moduleName = moduleSpinner.selectedItem.toString()
            writeNewTaskToSQLite(task, moduleName,this)
            if(Firebase.auth.currentUser?.uid != null){
                val userId = Firebase.auth.currentUser!!.uid
                writeNewTaskToDB(userId, task, moduleName)
            }
            val intent = Intent(this, TaskMenu::class.java)
            startActivity(intent)
        }

    }

    @SuppressLint("Range")
    fun writeNewTaskToSQLite(task: Task, moduleName : String, activity: Activity) {

        val db: SQLiteDatabase = ModuleSQLiteDBHelper(activity).readableDatabase
        val cursor = db.rawQuery( "SELECT module_id FROM module WHERE module_name = '$moduleName'",null)
        cursor.moveToFirst()
        var moduleId = cursor.getInt(0)
        cursor.close()

        println(task.toString())
        //write module
        val database: SQLiteDatabase = ModuleSQLiteDBHelper(activity).writableDatabase
        val values = ContentValues()
        values.put(
            ModuleSQLiteDBHelper.TASKS_COLUMN_TITLE,
            task.title.toString()
        )
        values.put(
            ModuleSQLiteDBHelper.TASKS_COLUMN_TYPE,
            task.taskType.toString()
        )
        values.put(
            ModuleSQLiteDBHelper.TASKS_COLUMN_DATE,
            task.dueDate.toString()
        )
        values.put(
            ModuleSQLiteDBHelper.TASKS_COLUMN_NOTE,
            task.note.toString()
        )
        values.put(
            ModuleSQLiteDBHelper.TASKS_COLUMN_ISCHECKED,
            task.checked.toString()
        )
        values.put(
            ModuleSQLiteDBHelper.MODULE_COLUMN_ID,
            moduleId
        )
        val newRowId = database.insert(ModuleSQLiteDBHelper.TASKS_TABLE, null, values)
    }

    fun writeNewTaskToDB(userId: String, task: Task, moduleName: String) {

        val ref = FirebaseDatabase.getInstance().getReference("Modules").child(userId)
        val queryRef: Query = ref.orderByChild("name").equalTo(moduleName)
        queryRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChild: String?) {
                snapshot.ref.child("moduleTasks").push().setValue(task).addOnCompleteListener{
                    println("write Task to DB Success")
                }.addOnFailureListener{ err ->
                    println("write Task to DB Fail: $err")
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