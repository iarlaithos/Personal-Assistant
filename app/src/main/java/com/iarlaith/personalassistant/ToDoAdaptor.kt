package com.iarlaith.personalassistant


import android.app.Activity
import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.item_todo.view.*

class ToDoAdaptor(
    private var todos: MutableList<ToDo>
) : RecyclerView.Adapter<ToDoAdaptor.ToDoViewHolder>() {
    private lateinit var dbRef: DatabaseReference

    class ToDoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ToDoViewHolder {
        return ToDoViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_todo,
                parent,
                false
            )
        )
    }

    fun addToDo (todo : ToDo) {
        todos.add(todo)
        notifyItemInserted(todos.size-1)
    }

    fun deleteToDo (todo : ToDo) {
        todos.remove(todo)
        notifyDataSetChanged()
    }

    fun deleteDoneToDos (activity : Activity) {
        var deletedToDo : ArrayList<ToDo> = ArrayList();
        for (todo in todos){
            if(todo.isChecked){
                deletedToDo.add(todo)
            }
        }
        for(ot in deletedToDo){
            DeleteFromSQLite(ot, activity)
            deleteFromCloudDB(ot)
        }

        todos.removeAll { todo ->
            todo.isChecked
        }
        notifyDataSetChanged()
    }

    fun clearToDos (activity : Activity) {
        todos.removeAll { todo ->
            todo.isChecked || !todo.isChecked
        }
        notifyDataSetChanged()
    }

    fun DeleteFromSQLite(todo : ToDo, activity : Activity){
        var title = todo.title
        val database: SQLiteDatabase = ToDoListSQLiteDBHelper(activity).writableDatabase
        database.execSQL("DELETE FROM todo WHERE todo_name = '$title'")
        database.close()
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

    private fun toggleStikeThrough(tvToDoTitle: TextView, isChecked: Boolean){
        if(isChecked) {
            val userId = Firebase.auth.currentUser!!.uid
            val reference = FirebaseDatabase.getInstance().getReference("Todos").child(userId)
            val queryRef: Query = reference.orderByChild("title").equalTo(tvToDoTitle.text.toString())
            tvToDoTitle.paintFlags = tvToDoTitle.paintFlags or STRIKE_THRU_TEXT_FLAG
            queryRef.addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChild: String?) {
                    snapshot.ref.setValue(ToDo(tvToDoTitle.text.toString(), true))
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
        } else {
            val userId = Firebase.auth.currentUser!!.uid
            val reference = FirebaseDatabase.getInstance().getReference("Todos").child(userId)
            val queryRef: Query = reference.orderByChild("title").equalTo(tvToDoTitle.text.toString())
            tvToDoTitle.paintFlags = tvToDoTitle.paintFlags and STRIKE_THRU_TEXT_FLAG.inv()
            queryRef.addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChild: String?) {
                    snapshot.ref.setValue(ToDo(tvToDoTitle.text.toString(), false))
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

    override fun onBindViewHolder(holder: ToDoViewHolder, position: Int) {
        val curTodo = todos[position]
        holder.itemView.apply {
            tvToDoTitle.text = curTodo.title
            cbDone.isChecked = curTodo.isChecked
            toggleStikeThrough(tvToDoTitle, curTodo.isChecked)
            cbDone.setOnCheckedChangeListener { _, isChecked ->
                toggleStikeThrough(tvToDoTitle, isChecked)
                curTodo.isChecked = !curTodo.isChecked
                todos[position] = curTodo
                updateSQL(curTodo, holder)
            }
        }
    }

    override fun getItemCount(): Int {
        return todos.size
    }

    private fun updateSQL(todo : ToDo, holder : ToDoViewHolder){
        var title : String = todo.title
        val database: SQLiteDatabase = ToDoListSQLiteDBHelper(holder.itemView.context).writableDatabase
        database.execSQL("DELETE FROM  todo WHERE todo_name ='$title'")
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
        println(todo.toString())
    }

    private fun deleteFromCloudDB(todo : ToDo){

        val userId = Firebase.auth.currentUser!!.uid
        val reference = FirebaseDatabase.getInstance().getReference("Todos")
        val queryRef: Query = reference.child(userId).orderByChild("title").equalTo(todo.title)

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