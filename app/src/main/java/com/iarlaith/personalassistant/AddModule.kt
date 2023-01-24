package com.iarlaith.personalassistant

import android.annotation.SuppressLint
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.iarlaith.personalassistant.ModuleSession.Day
import com.iarlaith.personalassistant.ModuleSession.Type
import java.sql.Time
import java.time.LocalTime
import java.util.Calendar

class AddModule : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_module)

        val sharedPreferences = getSharedPreferences("ModulesDB", Context.MODE_PRIVATE)
        val spEditor = sharedPreferences.edit()

        val etEnterModName = findViewById<EditText>(R.id.etEnterModName)
        val colourEnumSpinner = findViewById<Spinner>(R.id.colourSpinner)
        val colourEnumArray = Module.ColourEnum.values()
        val colourEnumNames = colourEnumArray.map { it.name }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, colourEnumNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        colourEnumSpinner.adapter = adapter
        val addSessionButton = findViewById<Button>(R.id.btnAddSession)
        val addedSessions = findViewById<TextView>(R.id.tvModuleSessions)

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
                val  timePicker  = TimePickerDialog(this, TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                    inputStartTime = LocalTime.of(hourOfDay, minute)
                    startTime.text = inputStartTime.toString() },
                    now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), false )
                timePicker.show()
            }

            val selectEndTimeButton = dialog.findViewById<Button>(R.id.btnSelectEndTime)
            selectEndTimeButton.setOnClickListener{
                val now = Calendar.getInstance()
                val  timePicker  = TimePickerDialog(this, TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
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
    }
}