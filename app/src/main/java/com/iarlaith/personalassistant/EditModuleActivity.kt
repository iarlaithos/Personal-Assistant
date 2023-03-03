package com.iarlaith.personalassistant

import android.app.Activity
import android.app.Dialog
import android.app.TimePickerDialog
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
import androidx.core.view.get
import androidx.core.view.isVisible
import androidx.core.view.iterator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.iarlaith.personalassistant.Module.ColourEnum
import java.time.LocalTime
import java.util.*


class EditModuleActivity : AppCompatActivity() {
    private val SPEECH_REC = 103
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_module)

        val homeButton = findViewById<ImageView>(R.id.addModulebtnHome)
        val menuButton = findViewById<ImageView>(R.id.addModulebtnMenu)
        val addSessionButton = findViewById<Button>(R.id.addSessionbtn)
        val deleteModuleButton = findViewById<Button>(R.id.btnDeleteModule)
        val confirmModuleButton = findViewById<Button>(R.id.btnConfirmEditModule)
        val micButton = findViewById<ImageView>(R.id.micButton)


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

        var moduleSpinner = findViewById<Spinner>(R.id.selectModuleSpinner)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, moduleEnumArray)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        moduleSpinner.adapter = adapter

        val moduleName = findViewById<EditText>(R.id.etEditModuleName)
        var moduleColour = findViewById<Spinner>(R.id.moduleColourSpinner)
        val recyclerView = findViewById<RecyclerView>(R.id.recSession)
        var moduleSelected : String = if (moduleSpinner.selectedItem != null) moduleSpinner.selectedItem.toString() else "No Module Added"
        moduleName.isVisible = false
        moduleColour.isVisible = false
        recyclerView.isVisible = false

        var currentModule = Module(null, null,null, null)
        moduleSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>?,
                selectedItemView: View?,
                position: Int,
                id: Long
            ) {
                moduleSelected = moduleSpinner.selectedItem.toString()
                if(moduleSelected != null){
                    println(moduleSelected)
                    var module = getModule(moduleSelected)


                    val colourEnumArray = Module.ColourEnum.values()
                    val colourEnumNames = colourEnumArray.map { it.name }
                    val colourAdapter = ArrayAdapter(this@EditModuleActivity, android.R.layout.simple_spinner_item, colourEnumNames)
                    colourAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    var colourPosition = colourAdapter.getPosition(module.colour)
                    moduleColour.adapter = colourAdapter
                    moduleColour.setSelection(colourPosition)

                    recyclerView.adapter = EditSessionRecycler(module.moduleSessions as java.util.ArrayList<ModuleSession>, this@EditModuleActivity)
                    currentModule.name = module.name.toString()
                    currentModule.colour = module.colour.toString()
                    currentModule.moduleSessions = module.moduleSessions
                }
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {

            }
        }


        homeButton.setOnClickListener {
            val intent = Intent(this, HomePageActivity::class.java)
            startActivity(intent)
        }

        menuButton.setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
        }

        addSessionButton.setOnClickListener {
            var module : Module = Module(moduleName.text.toString(), moduleColour.selectedItem.toString(),EditSessionRecycler(currentModule.moduleSessions as java.util.ArrayList<ModuleSession>, this@EditModuleActivity).getNewSessions(), null)
            addSession(module)
        }

        deleteModuleButton.setOnClickListener {
            deleteModule(moduleSelected)
            val intent = Intent(this, ModulesMenu::class.java)
            startActivity(intent)
        }

        confirmModuleButton.setOnClickListener {
            var module : Module = Module(moduleName.text.toString(), moduleColour.selectedItem.toString(),EditSessionRecycler(currentModule.moduleSessions as java.util.ArrayList<ModuleSession>, this@EditModuleActivity).getNewSessions(), null)
            confirmModule(moduleSelected, module)
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
   fun getModule(selectedModule : String) : Module{
       val moduleName = findViewById<EditText>(R.id.etEditModuleName)
       val moduleColour = findViewById<Spinner>(R.id.moduleColourSpinner)
       val recyclerView = findViewById<RecyclerView>(R.id.recSession)
       moduleName.isVisible = true
       moduleColour.isVisible = true
       recyclerView.isVisible = true

       var moduleId : Int? = null
       var module : Module = Module(null, null,null, null)
       val database: SQLiteDatabase = ModuleSQLiteDBHelper(this).readableDatabase
       val cursorModule = database.rawQuery("SELECT module_id, module_name, colour FROM module WHERE module_name = '$selectedModule'",null)
       if (cursorModule.moveToFirst()) {
           do {
               moduleId = cursorModule.getInt(0)
               moduleName.setText(cursorModule.getString(1))
               val selectedColour = ColourEnum.valueOf((cursorModule.getString(2)))
               moduleColour.setSelection(selectedColour.ordinal)

               module.name = moduleName.text.toString()
               module.colour = cursorModule.getString(2)
           } while (cursorModule.moveToNext())
       }
       cursorModule.close()

       val cursorSession = database.rawQuery( "SELECT location, type, day, start_time, end_time FROM module_sessions WHERE module_id = '$moduleId'",null)
       var sessionList = ArrayList<ModuleSession>()
       if (cursorSession.moveToFirst()) {
           do {
               var location = cursorSession.getString(0)
               var type = cursorSession.getString(1)
               var day = cursorSession.getString(2)
               var startTime = cursorSession.getString(3)
               var endTime = cursorSession.getString(4)

               var session = ModuleSession(location, type, day, LocalTime.parse(startTime), LocalTime.parse(endTime))
               sessionList.add(session)
           } while (cursorSession.moveToNext())
       }
       cursorSession.close()
       module.setModuleSessions(sessionList)

       recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
       recyclerView.adapter = EditSessionRecycler(sessionList, this)

       return module
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addSession(module : Module) {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.layout_add_module_session)

        val startTime = dialog.findViewById<TextView>(R.id.tvStartTime)
        val endTime = dialog.findViewById<TextView>(R.id.tvEndTime)
        val location =  dialog.findViewById<EditText>(R.id.etAddLocation)
        lateinit var inputStartTime: LocalTime
        lateinit var inputEndTime: LocalTime
        //Type Enum dropdown
        val typeEnumSpinner = dialog.findViewById<Spinner>(R.id.typeEnumSpinner)
        val typeEnumArray = ModuleSession.Type.values()
        val typeEnumNames = typeEnumArray.map { it.name }
        val typeAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, typeEnumNames)
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        typeEnumSpinner.adapter = typeAdapter

        //Day of week Enum dropdown
        val dayEnumSpinner = dialog.findViewById<Spinner>(R.id.dayEnumSpinner)
        val dayEnumArray = ModuleSession.Day.values()
        val dayEnumNames = dayEnumArray.map { it.name }
        val dayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, dayEnumNames)
        dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        dayEnumSpinner.adapter = dayAdapter

        val selectStartTimeButton = dialog.findViewById<Button>(R.id.btnSelectStartTime)
        selectStartTimeButton.setOnClickListener{
            val now = Calendar.getInstance()
            val  timePicker  = TimePickerDialog(this, { view, hourOfDay, minute ->
                inputStartTime = LocalTime.of(hourOfDay, minute)
                startTime.text = inputStartTime.toString() },
                now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), false )
            timePicker.show()
        }

        val selectEndTimeButton = dialog.findViewById<Button>(R.id.btnSelectEndTime)
        selectEndTimeButton.setOnClickListener{
            val now = Calendar.getInstance()
            val  timePicker  = TimePickerDialog(this, { view, hourOfDay, minute ->
                inputEndTime = LocalTime.of(hourOfDay, minute)
                endTime.text = inputEndTime.toString() },
                now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), false )
            timePicker.show()
        }

        val addButton = dialog.findViewById<Button>(R.id.btnAdd)

        addButton.setOnClickListener{
            val inputLocation = location.text.toString()
            val inputType = typeEnumSpinner.selectedItem.toString()
            val inputDay = dayEnumSpinner.selectedItem.toString()
            val moduleSession = ModuleSession(inputLocation, inputType, inputDay, inputStartTime, inputEndTime)
            module.moduleSessions.add(moduleSession)
            val recyclerView = findViewById<RecyclerView>(R.id.recSession)
            recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            recyclerView.adapter = EditSessionRecycler(module.moduleSessions as java.util.ArrayList<ModuleSession>, this)
            dialog.dismiss()
        }
        dialog.show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun confirmModule(moduleSelected : String, module : Module) {
        val userId = Firebase.auth.currentUser!!.uid
        deleteModule(moduleSelected)
        val addModule = AddModule()
        println(module)
        addModule.writeNewModuleToSQLite(module, this)
        Thread.sleep(1000)
        addModule.writeNewModuleToDB(userId, module)
        val intent = Intent(this, ModulesMenu::class.java)
        startActivity(intent)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun deleteModule(moduleSelected : String) {
        val userId = Firebase.auth.currentUser!!.uid
        val db: SQLiteDatabase = ModuleSQLiteDBHelper(this).writableDatabase
        db.execSQL( "DELETE FROM module_sessions WHERE module_id = (SELECT module_id FROM module WHERE module_name = '$moduleSelected')")
        db.execSQL( "DELETE FROM module WHERE module_name = '$moduleSelected'")
        val dbRef = FirebaseDatabase.getInstance().getReference("Modules")
        val queryRef: Query = dbRef.child(userId).orderByChild("name").equalTo(moduleSelected)

        queryRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChild: String?) {
                snapshot.ref.setValue(null)
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
