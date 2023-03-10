package com.iarlaith.personalassistant

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Build
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_to_do_list.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.util.*
import kotlin.collections.ArrayList


class HomePageActivity : AppCompatActivity() {
    private val SPEECH_REC = 105
    private lateinit var todoAdaptor: ToDoAdaptor
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)

        val todoActivity = ToDoListActivity()
        val micButton = findViewById<ImageView>(R.id.micButton)

        val mode = findViewById<Switch>(R.id.switchMode)
        val menu = findViewById<ImageButton>(R.id.btnMenu)
        val btnAddTask = findViewById<Button>(R.id.btnAddTask)
        val btnDeleteDone = findViewById<Button>(R.id.btnDeleteTask)
        val taskRecycler = TaskRecycler(mutableListOf())
        btnAddTask.isVisible = true
        btnDeleteDone.isVisible = true


        btnDeleteDone.setOnClickListener {
            deleteDoneTasks(this)
            displayTasks(this)
        }

        btnAddTask.setOnClickListener {
            val intent = Intent(this, AddTask::class.java)
            startActivity(intent)
        }

        todoAdaptor = ToDoAdaptor(mutableListOf())
        rvToDoItems.adapter = todoAdaptor
        rvToDoItems.layoutManager = LinearLayoutManager(this)
        todoAdaptor.clearToDos(this)
        todoActivity.displayToDos(todoAdaptor, this)
        todoActivity.printToDos(this)
        val etToDoTitle = findViewById<EditText>(R.id.etToDoTitle)
        val addTodo = findViewById<Button>(R.id.btnAddToDo)
        val deleteTodo = findViewById<Button>(R.id.btnDeleteDoneToDo)
        etToDoTitle.isVisible = false
        addTodo.isVisible = false
        deleteTodo.isVisible = false
        rvToDoItems.isVisible = false

        val prevDay = findViewById<ImageView>(R.id.btnPrevDay)
        val nextDay = findViewById<ImageView>(R.id.btnNextDay)
        val taskBanner = findViewById<TextView>(R.id.tvUpcomingTasks)
        val sessionBanner = findViewById<TextView>(R.id.tvTodaysSessions)

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
        taskBanner.isVisible = true
        sessionBanner.isVisible = true

        val rvTasks = findViewById<RecyclerView>(R.id.recTasks)
        rvTasks.removeAllViews()
        displayTasks(this)

        menu.setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
        }

        mode.setOnCheckedChangeListener { _, isChecked ->
            rvToDoItems.isVisible = isChecked
            etToDoTitle.isVisible = isChecked
            addTodo.isVisible = isChecked
            deleteTodo.isVisible = isChecked
            nextDay.isVisible = !isChecked
            prevDay.isVisible = !isChecked
            rvModules.isVisible = !isChecked
            rvTasks.isVisible = !isChecked
            tvDay.isVisible = !isChecked
            taskBanner.isVisible = !isChecked
            sessionBanner.isVisible = !isChecked
            btnAddTask.isVisible = !isChecked
            btnDeleteDone.isVisible = !isChecked
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

        addTodo.setOnClickListener {
            val todoTitle = etToDoTitle.text.toString()
            if(todoTitle.isNotEmpty()){
                val todo = ToDo(todoTitle, false)
                todoAdaptor.addToDo(todo)
                val userId = Firebase.auth.currentUser!!.uid
                val todoActivity = ToDoListActivity()
                todoActivity.writetoCloudDB(todo, userId)
                todoActivity.writetoSQLite(todo, this)
                etToDoTitle.text.clear()
            }
        }

        deleteTodo.setOnClickListener {
            todoAdaptor.deleteDoneToDos(this)
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