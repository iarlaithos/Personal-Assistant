package com.iarlaith.personalassistant

import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import java.time.*
import java.util.*

class EditSessionRecycler(private var sessions: ArrayList<ModuleSession>, val context : Context) : RecyclerView.Adapter<EditSessionRecycler.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val sessionView : TextView = view.findViewById(R.id.tvEditSession)
        val editButton : Button = view.findViewById(R.id.btnEditSession)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_module_edit, parent, false)
        return ViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.sessionView.text = sessions[position].location + ", " +  sessions[position].sessionType + "\n" + sessions[position].dayOfTheWeek + ", "  + sessions[position].startTime + "  ->  " + sessions[position].endTime
        holder.editButton.setOnClickListener {
            val dialog = Dialog(context)
            dialog.setContentView(R.layout.layout_session_edit)

            val startTime = dialog.findViewById<TextView>(R.id.tvEditStartTime)
            val endTime = dialog.findViewById<TextView>(R.id.tvEditEndTime)
            val location =  dialog.findViewById<EditText>(R.id.etEditLocation)
            startTime.text = sessions[position].startTime.toString()
            endTime.text = sessions[position].endTime.toString()
            location.setText(sessions[position].location)
            lateinit var inputStartTime: LocalTime
            lateinit var inputEndTime: LocalTime
            //Type Enum dropdown
            var typeEnumSpinner = dialog.findViewById<Spinner>(R.id.editTypeEnumSpinner)
            val typeEnumArray = ModuleSession.Type.values()
            val typeEnumNames = typeEnumArray.map { it.name }
            val typeAdapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, typeEnumNames)
            typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            var typePosition = typeAdapter.getPosition(sessions[position].sessionType)
            typeEnumSpinner.adapter = typeAdapter
            typeEnumSpinner.setSelection(typePosition)

            //Day of week Enum dropdown
            val dayEnumSpinner = dialog.findViewById<Spinner>(R.id.editDayEnumSpinner)
            val dayEnumArray = ModuleSession.Day.values()
            val dayEnumNames = dayEnumArray.map { it.name }
            val dayAdapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, dayEnumNames)
            dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            var dayPosition = dayAdapter.getPosition(sessions[position].dayOfTheWeek)
            dayEnumSpinner.adapter = dayAdapter
            dayEnumSpinner.setSelection(dayPosition)

            val selectStartTimeButton = dialog.findViewById<Button>(R.id.btnEditStartTime)
            selectStartTimeButton.setOnClickListener{
                val oldStart = sessions[position].startTime
                var localDateTime = oldStart.atDate(LocalDate.now())
                val instant: Instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant()
                val date = Date.from(instant)
                val calendar = Calendar.getInstance()
                calendar.time = date
                val  timePicker  = TimePickerDialog(context, { view, hourOfDay, minute ->
                    inputStartTime = LocalTime.of(hourOfDay, minute)
                    sessions[position].startTime = inputStartTime
                    startTime.text = inputStartTime.toString() },
                    calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false )
                timePicker.show()
            }

            val selectEndTimeButton = dialog.findViewById<Button>(R.id.btnEditEndTime)
            selectEndTimeButton.setOnClickListener{
                val oldEnd = sessions[position].endTime
                var localDateTime = oldEnd.atDate(LocalDate.now())
                val instant: Instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant()
                val date = Date.from(instant)
                val calendar = Calendar.getInstance()
                calendar.time = date
                val  timePicker  = TimePickerDialog(context, { view, hourOfDay, minute ->
                    inputEndTime = LocalTime.of(hourOfDay, minute)
                    sessions[position].endTime = inputEndTime
                    endTime.text = inputEndTime.toString() },
                    calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false )
                timePicker.show()
            }

            val confirmSessionButton = dialog.findViewById<Button>(R.id.btnConfirmSession)

            confirmSessionButton.setOnClickListener{
                val inputLocation = location.text.toString()
                val inputType = typeEnumSpinner.selectedItem.toString()
                val inputDay = dayEnumSpinner.selectedItem.toString()
                //val moduleSession = ModuleSession(inputLocation, inputType, inputDay, inputStartTime, inputEndTime)

                sessions[position].location = inputLocation
                sessions[position].sessionType = inputType
                sessions[position].dayOfTheWeek = inputDay
                holder.sessionView.text = sessions[position].location + ", " +  sessions[position].sessionType + "\n" + sessions[position].dayOfTheWeek + ", "  + sessions[position].startTime + "  ->  " + sessions[position].endTime
                dialog.dismiss()
            }
            dialog.show()
        }
    }

    override fun getItemCount(): Int {
        return sessions.size
    }

    fun getNewSessions(): List<ModuleSession> {
        return sessions
    }

}