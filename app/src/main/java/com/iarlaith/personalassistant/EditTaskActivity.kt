package com.iarlaith.personalassistant

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Build
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


class EditTaskActivity : AppCompatActivity() {
    private val SPEECH_REC = 104
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_task)

        val homeButton = findViewById<ImageView>(R.id.btnHome)
        val menuButton = findViewById<ImageView>(R.id.btnMenu)
        val deleteTaskButton = findViewById<Button>(R.id.btnDeleteTask)
        val confirmTaskButton = findViewById<Button>(R.id.btnConfirmEditTask)
        val etTaskTitle = findViewById<EditText>(R.id.etEditTaskName)
        val moduleSpinner = findViewById<Spinner>(R.id.moduleSpinner)
        val dueDate = findViewById<TextView>(R.id.tvDueDate)
        val notes = findViewById<EditText>(R.id.editNote)
        val typeSpinner = findViewById<Spinner>(R.id.typeSpinner)
        val micButton = findViewById<ImageView>(R.id.micButton)


        val formatter = SimpleDateFormat("dd/MM/yyyy")
        var inputDate: Date = Date(System.currentTimeMillis())
        val inputDueDate = formatter.format(inputDate)

        val taskEnumArray = ArrayList<String>()
        val db: SQLiteDatabase = ModuleSQLiteDBHelper(this).readableDatabase
        val cursor = db.rawQuery("SELECT task_title FROM tasks", null)
        if (cursor.moveToFirst()) {
            do {
                var taskEnumEntry = cursor.getString(0)
                taskEnumArray.add(taskEnumEntry)
            } while (cursor.moveToNext())
        }
        cursor.close()

        val taskSpinner = findViewById<Spinner>(R.id.selectTaskSpinner)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, taskEnumArray)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        taskSpinner.adapter = adapter

        val moduleEnumArray = ArrayList<String>()
        val moduleCursor = db.rawQuery("SELECT module_name FROM module", null)
        if (moduleCursor.moveToFirst()) {
            do {
                var moduleEnumEntry = moduleCursor.getString(0)
                moduleEnumArray.add(moduleEnumEntry)
            } while (moduleCursor.moveToNext())
        }
        moduleCursor.close()

        val mAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, moduleEnumArray)
        mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        moduleSpinner.adapter = mAdapter

        var taskSelected: String =
            if (taskSpinner.selectedItem != null) taskSpinner.selectedItem.toString() else "No Task Added"
        etTaskTitle.isVisible = false
        moduleSpinner.isVisible = false
        notes.isVisible = false
        dueDate.isVisible = false
        typeSpinner.isVisible = false

        var currentTask = Task(null, null, null, null, false)
        taskSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onItemSelected(
                parentView: AdapterView<*>?,
                selectedItemView: View?,
                position: Int,
                id: Long
            ) {
                taskSelected = taskSpinner.selectedItem.toString()
                if (taskSelected != null) {
                    var task = getTask(taskSelected)
                    var module = getModule(taskSelected)
                    var modulePosition = mAdapter.getPosition(module)
                    moduleSpinner.adapter = mAdapter
                    moduleSpinner.setSelection(modulePosition)

                    currentTask.title = task.title.toString()
                    currentTask.dueDate = task.dueDate
                    currentTask.note = task.note.toString()
                    currentTask.taskType = task.taskType.toString()
                    etTaskTitle.setText(task.title)
                    etTaskTitle.isVisible = true
                    val calendar = Calendar.getInstance()
                    calendar.time = currentTask.dueDate
                    var year = calendar.get(Calendar.YEAR)
                    var month = calendar.get(Calendar.MONTH)
                    var date = calendar.get(Calendar.DAY_OF_MONTH)
                    var monthString = month.toString()
                    var dateString = date.toString()
                    if (month < 9) {
                        monthString = "0$monthString"
                    }
                    if (date < 10) {
                        dateString = "0$dateString"
                    }
                    dueDate.text = "Due Date : $dateString/$monthString/$year"
                    notes.setText(task.note.toString())

                    val typeEnumArray = Task.TaskType.values()
                    val typeEnumNames = typeEnumArray.map { it.taskType }
                    val typeAdapter = ArrayAdapter(
                        this@EditTaskActivity,
                        android.R.layout.simple_spinner_item,
                        typeEnumNames
                    )
                    typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    var typePosition = typeAdapter.getPosition(task.taskType)
                    typeSpinner.adapter = typeAdapter
                    typeSpinner.setSelection(typePosition)

                    currentTask.taskType = typeSpinner.selectedItem.toString()
                    moduleSpinner.isVisible = true
                    typeSpinner.isVisible = true
                    notes.isVisible = true
                    dueDate.isVisible = true
                }
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {

            }
        }

        dueDate.setOnClickListener {
            val oldDate = currentTask.dueDate
            val calendar: Calendar = Calendar.getInstance()
            calendar.time = oldDate
            var year = calendar.get(Calendar.YEAR)
            var month = calendar.get(Calendar.MONTH)
            var date = calendar.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(
                this,
                { _, year, monthOfYear, date ->
                    var monthPicked: String = (monthOfYear + 1).toString()
                    var datePicked: String = (date).toString()
                    if (monthOfYear < 9) {
                        monthPicked = "0$monthPicked"
                    }
                    if (date < 10) {
                        datePicked = "0$datePicked"
                    }
                    dueDate.text = "Due Date : $datePicked/$monthPicked/$year"
                    currentTask.dueDate =
                        formatter.parse("$datePicked/$monthPicked/$year") as Date//.of(year, monthOfYear+1, date)
                    println(inputDueDate)

                },
                year,
                month,
                date
            )
            datePicker.show()
        }


        confirmTaskButton.setOnClickListener {
            var dateString = dueDate.text.toString().split(": ")
            var date : Date =  formatter.parse(dateString[1]) as Date
            confirmTask(taskSelected, Task(etTaskTitle.text.toString(),typeSpinner.selectedItem.toString(), date, notes.text.toString() ,currentTask.checked), moduleSpinner.selectedItem.toString(), this)
        }

        deleteTaskButton.setOnClickListener {
            deleteTask(taskSelected, this)
            val intent = Intent(this, TaskMenu::class.java)
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
    fun getModule(selectedTask: String): String {
        var moduleName = ""
        val db: SQLiteDatabase = ModuleSQLiteDBHelper(this).readableDatabase
        val cursor = db.rawQuery(
            "SELECT module_name \n" +
                    "FROM module \n" +
                    "INNER JOIN tasks ON module.module_id = tasks.module_id \n" +
                    "WHERE module.module_id IN (SELECT module_id from module_sessions where task_title = '$selectedTask')",
            null
        )
        if (cursor.moveToFirst()) {
            moduleName = cursor.getString(0)
        }
        cursor.close()
        return moduleName
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getTask(selectedTask: String): Task {
        var task = Task(null, null, null, null, null)
        val db: SQLiteDatabase = ModuleSQLiteDBHelper(this).readableDatabase
        val cursor = db.rawQuery(
            "SELECT type, due_date, note, is_checked FROM tasks WHERE task_title = '$selectedTask'",
            null
        )
        if (cursor.moveToFirst()) {
            var type = cursor.getString(0)
            var taskDueDate = cursor.getString(1)
            var note = cursor.getString(2)
            var checked = cursor.getString(3) == "1"
            var formatter: DateFormat = SimpleDateFormat("EEE MMM dd HH:mm:ss zzzz yyyy")
            var dueDate = formatter.parse(taskDueDate)
            task = Task(selectedTask, type, dueDate, note, checked)
        }
        cursor.close()
        return task
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun confirmTask(taskSelected: String, task: Task, newModule: String, activity : Activity) {
        val userId = Firebase.auth.currentUser!!.uid
        val addTask = AddTask()
        deleteTask(taskSelected, activity)
        addTask.writeNewTaskToSQLite(task, newModule, activity)
        Thread.sleep(1000)
        writeNewTaskToDB(userId, task, newModule)
        val intent = Intent(activity, TaskMenu::class.java)
        startActivity(intent)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun deleteTask(taskSelected: String, activity : Activity) {
        val userId = Firebase.auth.currentUser!!.uid
        val db: SQLiteDatabase = ModuleSQLiteDBHelper(activity).writableDatabase
        db.execSQL( "DELETE FROM tasks WHERE task_title = '$taskSelected'")
        val dbRef = FirebaseDatabase.getInstance().getReference("Modules")
        val queryRef: Query =
            dbRef.child(userId).orderByValue()//.orderByChild("moduleTasks/title").equalTo(taskSelected)

        queryRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChild: String?) {
                for (postSnapshot in snapshot.children) {
                    for(ps in  postSnapshot.children){
                        if(ps.child("title").value == taskSelected){
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

    private fun writeNewTaskToDB(userId: String, task: Task, moduleName: String) {

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