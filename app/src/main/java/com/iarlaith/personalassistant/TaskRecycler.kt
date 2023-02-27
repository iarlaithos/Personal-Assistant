package com.iarlaith.personalassistant

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.item_todo.view.*
import java.time.LocalTime

class TaskRecycler(private val tasks: List<Task>): RecyclerView.Adapter<TaskRecycler.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val taskTitle: TextView = view.findViewById(R.id.tvRecTask)
        val taskDueDate : TextView = view.findViewById(R.id.tvRecDate)
        val taskType : TextView = view.findViewById(R.id.tvRecTaskType)
        val taskNote : TextView = view.findViewById(R.id.tvRecNotes)
        val arrow : ImageView = view.findViewById(R.id.arrow)
        val cbTask : CheckBox = view.findViewById(R.id.cbTask)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_task_view, parent, false)
        return ViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.taskTitle.text = tasks[position].title
        var dueDate = tasks[position].dueDate.toString().split(" ")
        holder.taskDueDate.text = dueDate[0] + "\n" + dueDate[1] + " " + dueDate[2]
        holder.taskType.text = tasks[position].taskType
        holder.taskNote.text = (tasks[position] .note).toString()
        holder.cbTask.isChecked = tasks[position].checked

        holder.taskTitle.isVisible = true
        holder.taskDueDate.isVisible = true
        holder.taskType.isVisible = false
        holder.taskNote.isVisible = false
        holder.arrow.isVisible = true
        holder.cbTask.isVisible = true
        holder.arrow.bringToFront()

        var taskTitle = tasks[position].title
        var colour = ""
        var moduleName = ""
        val database: SQLiteDatabase = ModuleSQLiteDBHelper(holder.itemView.context).readableDatabase
        val cursorModule = database.rawQuery("SELECT colour, module_name \n" +
                "FROM module \n" +
                "INNER JOIN tasks ON module.module_id = tasks.module_id \n" +
                "WHERE module.module_id IN (SELECT module_id from module_sessions where task_title = '$taskTitle')", null)
        if (cursorModule.moveToFirst()) {
            colour = cursorModule.getString(0)
            moduleName = cursorModule.getString(1)
        }
        cursorModule.close()
        when (colour) {
            "RED" -> {
                holder.taskTitle.setBackgroundColor(
                    ContextCompat.getColor(
                        holder.taskTitle.context,
                        R.color.red
                    )
                )
                holder.taskType.setBackgroundColor(
                    ContextCompat.getColor(
                        holder.taskType.context,
                        R.color.red
                    )
                )
                holder.taskNote.setBackgroundColor(
                    ContextCompat.getColor(
                        holder.taskNote.context,
                        R.color.red
                    )
                )
            }
            "ORANGE" -> {
                holder.taskTitle.setBackgroundColor(
                    ContextCompat.getColor(
                        holder.taskTitle.context,
                        R.color.orange
                    )
                )
                holder.taskType.setBackgroundColor(
                    ContextCompat.getColor(
                        holder.taskType.context,
                        R.color.orange
                    )
                )
                holder.taskNote.setBackgroundColor(
                    ContextCompat.getColor(
                        holder.taskNote.context,
                        R.color.orange
                    )
                )
            }
            "YELLOW" ->{
                holder.taskTitle.setBackgroundColor(
                    ContextCompat.getColor(
                        holder.taskTitle.context,
                        R.color.yellow
                    )
                )
                holder.taskType.setBackgroundColor(
                    ContextCompat.getColor(
                        holder.taskType.context,
                        R.color.yellow
                    )
                )
                holder.taskNote.setBackgroundColor(
                    ContextCompat.getColor(
                        holder.taskNote.context,
                        R.color.yellow
                    )
                )
            }
            "GREEN" -> {
                holder.taskTitle.setBackgroundColor(
                    ContextCompat.getColor(
                        holder.taskTitle.context,
                        R.color.green
                    )
                )
                holder.taskType.setBackgroundColor(
                    ContextCompat.getColor(
                        holder.taskType.context,
                        R.color.green
                    )
                )
                holder.taskNote.setBackgroundColor(
                    ContextCompat.getColor(
                        holder.taskNote.context,
                        R.color.green
                    )
                )
            }
            "BLUE" -> {
                holder.taskTitle.setBackgroundColor(
                    ContextCompat.getColor(
                        holder.taskTitle.context,
                        R.color.blue
                    )
                )
                holder.taskType.setBackgroundColor(
                    ContextCompat.getColor(
                        holder.taskType.context,
                        R.color.blue
                    )
                )
                holder.taskNote.setBackgroundColor(
                    ContextCompat.getColor(
                        holder.taskNote.context,
                        R.color.blue
                    )
                )
            }
            "PURPLE" -> {
                holder.taskTitle.setBackgroundColor(
                    ContextCompat.getColor(
                        holder.taskTitle.context,
                        R.color.purple_500
                    )
                )
                holder.taskType.setBackgroundColor(
                    ContextCompat.getColor(
                        holder.taskType.context,
                        R.color.purple_500
                    )
                )
                holder.taskNote.setBackgroundColor(
                    ContextCompat.getColor(
                        holder.taskNote.context,
                        R.color.purple_500
                    )
                )
            }
            "PINK" -> {
                holder.taskTitle.setBackgroundColor(
                    ContextCompat.getColor(
                        holder.taskTitle.context,
                        R.color.pink
                    )
                )
                holder.taskType.setBackgroundColor(
                    ContextCompat.getColor(
                        holder.taskType.context,
                        R.color.pink
                    )
                )
                holder.taskNote.setBackgroundColor(
                    ContextCompat.getColor(
                        holder.taskNote.context,
                        R.color.pink
                    )
                )
            }
            "WHITE" -> {
                holder.taskTitle.setBackgroundColor(
                    ContextCompat.getColor(
                        holder.taskTitle.context,
                        R.color.white
                    )
                )
                holder.taskType.setBackgroundColor(
                    ContextCompat.getColor(
                        holder.taskType.context,
                        R.color.white
                    )
                )
                holder.taskNote.setBackgroundColor(
                    ContextCompat.getColor(
                        holder.taskNote.context,
                        R.color.white
                    )
                )
            }
            else -> {
                holder.taskTitle.setBackgroundColor(
                    ContextCompat.getColor(
                        holder.taskTitle.context,
                        R.color.white
                    )
                )
                holder.taskType.setBackgroundColor(
                    ContextCompat.getColor(
                        holder.taskType.context,
                        R.color.white
                    )
                )
                holder.taskNote.setBackgroundColor(
                    ContextCompat.getColor(
                        holder.taskNote.context,
                        R.color.white
                    )
                )
            }
        }

        holder.arrow.setOnClickListener {
            if(holder.arrow.rotation == 0F){
                holder.arrow.rotation = 90F
                holder.taskType.isVisible = true
                holder.taskNote.isVisible = true
            }else{
                holder.arrow.rotation = 0F
                holder.taskType.isVisible = false
                holder.taskNote.isVisible = false
            }
        }

        holder.cbTask.setOnCheckedChangeListener { _, isChecked ->
            holder.cbTask.isChecked = isChecked
            tasks[position].checked = isChecked
            updateTask(tasks[position],holder, moduleName, isChecked)
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateTask(task : Task, holder : TaskRecycler.ViewHolder, moduleName : String, isChecked : Boolean){
        var title : String = task.title
        val database: SQLiteDatabase = ModuleSQLiteDBHelper(holder.itemView.context).writableDatabase
        val values = ContentValues()
        values.put(
            ModuleSQLiteDBHelper.TASKS_COLUMN_ISCHECKED,
            isChecked.toString()
        )
        val whereClause = "task_title='$title'"
        database.update(ModuleSQLiteDBHelper.TASKS_TABLE, values, whereClause, arrayOf())
        database.close()
        deleteTask(title)

        val userId = Firebase.auth.currentUser!!.uid

        writeNewTaskToDB(userId, task, moduleName)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun deleteTask(taskSelected: String) {
        val userId = Firebase.auth.currentUser!!.uid
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

    @SuppressLint("Range")
    fun writeNewTaskToSQLite(task: Task, moduleName : String, holder : ToDoAdaptor.ToDoViewHolder) {

        val db: SQLiteDatabase = ModuleSQLiteDBHelper(holder.itemView.context).readableDatabase
        val cursor = db.rawQuery( "SELECT module_id FROM module WHERE module_name = '$moduleName'",null)
        cursor.moveToFirst()
        var moduleId = cursor.getInt(0)
        cursor.close()

        println(task.toString())
        //write module
        val database: SQLiteDatabase = ModuleSQLiteDBHelper(holder.itemView.context).writableDatabase
        val values = ContentValues()
        values.put(
            ModuleSQLiteDBHelper.MODULE_COLUMN_ID,
            task.checked
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
    override fun getItemCount() = tasks.size
}