package com.iarlaith.personalassistant

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.ContentValues
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Build
import android.os.Bundle
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import java.time.LocalTime
import java.util.*


class AddModule : AppCompatActivity() {

    private lateinit var dbRef: DatabaseReference

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_module)

        val enterModName = findViewById<EditText>(R.id.etEnterModName)
        val colourEnumSpinner = findViewById<Spinner>(R.id.colourSpinner)
        val colourEnumArray = Module.ColourEnum.values()
        val colourEnumNames = colourEnumArray.map { it.name }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, colourEnumNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        colourEnumSpinner.adapter = adapter
        val addSessionButton = findViewById<Button>(R.id.btnAddSession)
        val addedSessions = findViewById<TextView>(R.id.tvModuleSessions)
        val addModuleButton = findViewById<Button>(R.id.btnAddModule)
        val moduleSessionList : MutableList<ModuleSession> = mutableListOf()

        addSessionButton.setOnClickListener{
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
                moduleSessionList.add(moduleSession)
                addedSessions.append(moduleSession.location.toString() + ", " +
                        moduleSession.sessionType.toString() + ", " +
                        moduleSession.dayOfTheWeek.toString() + ", " +
                        moduleSession.startTime.toString()  + ", " +
                        moduleSession.endTime.toString() + System.lineSeparator()
                )

                dialog.dismiss()
            }
            dialog.show()
        }

        addModuleButton.setOnClickListener{
            val module = Module(enterModName.text.toString(), colourEnumSpinner.selectedItem.toString(), moduleSessionList)
            writeNewModuleToSQLite(module, this)
            if(Firebase.auth.currentUser?.uid != null){
                val userId = Firebase.auth.currentUser!!.uid
                writeNewModuleToDB(userId, module)
            }
            val intent = Intent(this, ModulesMenu::class.java)
            startActivity(intent)
        }
    }

    private fun writeNewModuleToDB(userId: String, module: Module) {
        Thread.sleep(2000)

        dbRef = FirebaseDatabase.getInstance().getReference("Modules")
        dbRef.child(userId).push().setValue(module)
            .addOnCompleteListener{
                println("write to DB Success")
            }.addOnFailureListener{ err ->
                println("write to DB Fail: $err")
            }
    }

    @SuppressLint("Range")
    fun writeNewModuleToSQLite(module: Module, activity: Activity) {
        //write module
        val database: SQLiteDatabase = ModuleSQLiteDBHelper(activity).writableDatabase
        val values = ContentValues()
        values.put(
            ModuleSQLiteDBHelper.MODULE_COLUMN_NAME,
            module.name.toString()
        )
        values.put(
            ModuleSQLiteDBHelper.MODULE_COLUMN_COLOUR,
            module.colour.toString()
        )


        val newRowId = database.insert(ModuleSQLiteDBHelper.MODULES_TABLE, null, values)
        Toast.makeText(activity, "The new Module Row Id is $newRowId", Toast.LENGTH_LONG).show()

        //write module sessions
        for(session in module.moduleSessions) {
            println(session.toString())
            val sessionValues = ContentValues()
            sessionValues.put(
                ModuleSQLiteDBHelper.MODULE_COLUMN_ID,
                newRowId
            )
            sessionValues.put(
                ModuleSQLiteDBHelper.SESSION_COLUMN_LOCATION,
                session.location.toString()
            )
            sessionValues.put(
                ModuleSQLiteDBHelper.SESSION_COLUMN_TYPE,
                session.sessionType.toString()
            )
            sessionValues.put(
                ModuleSQLiteDBHelper.SESSION_COLUMN_DAY,
                session.dayOfTheWeek.toString()
            )
            sessionValues.put(
                ModuleSQLiteDBHelper.SESSION_COLUMN_START_TIME,
                session.startTime.toString()
            )
            sessionValues.put(
                ModuleSQLiteDBHelper.SESSION_COLUMN_END_TIME,
                session.endTime.toString()
            )
            val newSessionRowId = database.insert(ModuleSQLiteDBHelper.MODULE_SESSIONS_TABLE, null, sessionValues)
            Toast.makeText(activity, "The new Row Id is $newSessionRowId", Toast.LENGTH_LONG).show()
        }

    }
}