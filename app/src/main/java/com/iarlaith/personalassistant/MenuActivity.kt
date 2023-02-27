package com.iarlaith.personalassistant

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.iarlaith.personalassistant.ModuleSQLiteDBHelper.MODULES_TABLE
import com.iarlaith.personalassistant.ModuleSQLiteDBHelper.MODULE_SESSIONS_TABLE
import com.iarlaith.personalassistant.ToDoListSQLiteDBHelper.TODO_TABLE
import java.io.File
import java.text.SimpleDateFormat
import java.time.LocalTime


class MenuActivity : AppCompatActivity() {
    private lateinit var database: DatabaseReference
    private lateinit var userFirebaseModules: List<Module>
    val formatter = SimpleDateFormat("dd/MM/yyyy")

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        val signOut = findViewById<TextView>(R.id.tvSignOut)
        val homeButton = findViewById<ImageView>(R.id.MbtnHome)
        val modulesButton = findViewById<TextView>(R.id.tvModules)
        val toDoListButton = findViewById<TextView>(R.id.tvToDoList)
        val tasksButton = findViewById<TextView>(R.id.tvTasks)

        signOut.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            val builderMessage : String
            val connected : Boolean
            if(checkForInternet(this)){
                builderMessage = "Are you sure you want to Sign Out?"
                connected = true
            }else {
                builderMessage = "Are you sure you want to Sign Out? \nYour Device is not connected to the internet which could result in lost data!"
                connected = false
            }
            builder.setMessage(builderMessage)
                .setCancelable(false)
                .setPositiveButton("Sign Out") { dialog, id ->
                    if(connected){
                        updateUser()
                        Thread.sleep(1000)
                        correctFirebaseDB(this)
                        var correctToDos = CorrectToDos()
                        correctToDos.correctToDoFirebase(this)
                        Thread.sleep(1000)
                        Firebase.auth.signOut()
                    }
                    val sharedPreferences = getSharedPreferences("AuthenticationDB", Context.MODE_PRIVATE)
                    val spEditor = sharedPreferences.edit()
                    spEditor.putBoolean("RememberMeCB", false)
                    spEditor.apply()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }
                .setNegativeButton("Cancel") { dialog, id ->
                    // Dismiss the dialog
                    dialog.dismiss()
                }
            val alert = builder.create()
            alert.show()

            val db: SQLiteDatabase = ModuleSQLiteDBHelper(this).writableDatabase
            db.execSQL("delete from $MODULES_TABLE");
            db.execSQL("delete from $MODULE_SESSIONS_TABLE");
            db.delete(MODULES_TABLE,null,null);
            db.close()

            val tododb: SQLiteDatabase = ToDoListSQLiteDBHelper(this).writableDatabase
            tododb.execSQL("delete from $TODO_TABLE");
            tododb.delete(TODO_TABLE,null,null);
            tododb.close()
        }

        modulesButton.setOnClickListener {
            val intent = Intent(this, ModulesMenu::class.java)
            startActivity(intent)
        }

        homeButton.setOnClickListener {
            val intent = Intent(this, HomePageActivity::class.java)
            startActivity(intent)
        }

        toDoListButton.setOnClickListener {
            val intent = Intent(this, ToDoListActivity::class.java)
            startActivity(intent)
        }

        tasksButton.setOnClickListener {
            val intent = Intent(this, TaskMenu::class.java)
            startActivity(intent)
        }

    }

    private fun checkForInternet(context: Context): Boolean {

        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            val network = connectivityManager.activeNetwork ?: return false
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

            return when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                else -> false
            }
        } else {
            // if the android version is below M
            @Suppress("DEPRECATION") val networkInfo =
                connectivityManager.activeNetworkInfo ?: return false
            @Suppress("DEPRECATION")
            return networkInfo.isConnected
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun correctFirebaseDB(activity: Activity) {
        val userLocalModules = getLocalModuleData(activity)
        getFirebaseModuleData { userCloudModules ->
            if (userCloudModules == null) {
                Log.d(TAG, "User cloud modules is null")
                return@getFirebaseModuleData
            }
            if (userLocalModules != null) {
                if (userLocalModules.toString() != userCloudModules.toString() && userLocalModules.size >= userCloudModules.size) {
                    val differenceModules = userLocalModules?.minus(userCloudModules)
                    if (differenceModules != null) {
                        for (diffModule in differenceModules) {
                            val userId = Firebase.auth.currentUser?.uid
                            val reference = FirebaseDatabase.getInstance().getReference("Modules")
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
                    }

                } else {
                    println("#############################")
                    println("Equal")
                    println(userLocalModules)
                    println(userCloudModules)
                    println("#############################")
                }
            }
        }
    }

    private fun checkDataBase(context: Context): Boolean {
        val fullPath = "/data/data/com.iarlaith.personalassistant/databases/modules_database"
        val file: File = context.getDatabasePath(fullPath)
        return file.exists()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getLocalModuleData(activity: Activity): List<Module>? {
        if(checkDataBase(activity)){
            //SQLite Module Data
            val db: SQLiteDatabase = ModuleSQLiteDBHelper(activity).readableDatabase
            val cursorModules: Cursor = db.rawQuery("SELECT module_name, colour FROM $MODULES_TABLE", null)
            val userLocalModules: ArrayList<Module> = ArrayList()
            if (cursorModules.moveToFirst()) {
                do {
                    // on below line we are adding the data from cursor to our array list.
                    userLocalModules.add(
                        Module(
                            cursorModules.getString(0),
                            cursorModules.getString(1),
                            null,
                            null
                        )
                    )
                } while (cursorModules.moveToNext())
            }
            cursorModules.close()
            for( module in userLocalModules){
                val moduleId : Int =  userLocalModules.indexOf(module) + 1
                val cursorSessions: Cursor = db.rawQuery("SELECT location, type, day, start_time, end_time FROM $MODULE_SESSIONS_TABLE WHERE module_id = $moduleId", null)
                val sessionsArrayList: ArrayList<ModuleSession> = ArrayList()
                if (cursorSessions.moveToFirst()) {
                    do {
                        sessionsArrayList.add(ModuleSession(
                            cursorSessions.getString(0),
                            cursorSessions.getString(1),
                            cursorSessions.getString(2),
                            LocalTime.parse(cursorSessions.getString(3)),
                            LocalTime.parse(cursorSessions.getString(4))
                        )
                        )
                    } while (cursorSessions.moveToNext())
                }
                module.setModuleSessions(sessionsArrayList)
                cursorSessions.close()
                return userLocalModules!!
            }
        }
        return null
    }

    fun getFirebaseModuleData(callback: (List<Module>?) -> Unit) {
        //Firebase Data
        val currentUser = Firebase.auth.currentUser
        if (currentUser == null) {
            callback(null)
            return
        }
        val userId = currentUser.uid
        val rootRef = Firebase.database.reference
        val modulesRef = rootRef.child("Modules").child(userId)

        var fetched = false
        val valueEventListener = object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (!fetched) {
                    fetched = true
                    val userCloudModules = reformatFirebaseData(dataSnapshot)
                    Log.i("Firebase Modules: ", userCloudModules.toString())
                    callback(userCloudModules.toList())
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d(TAG, databaseError.message)
            }
        }
        modulesRef.addListenerForSingleValueEvent(valueEventListener)
        fetched=false;
        modulesRef.removeEventListener(valueEventListener)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun reformatFirebaseData(it : DataSnapshot): ArrayList<Module> {
        val userCloudModules : ArrayList<Module> = ArrayList()
        val children = it.children
        for (child in children){
            try {
                val value = child.value as HashMap<String, Any>
                val name = value["name"] as String
                val colour = value["colour"] as String
                val sessions = value["moduleSessions"] as List<Map<String, Any>>
                val moduleTasks = ArrayList<Task>()
                if(value["moduleTasks"] != null){
                    val tasksMap = value["moduleTasks"] as HashMap<String, Any>
                    val tasks = tasksMap.values.toList() as List<Map<String, Any>>
                    for (task in tasks) {
                        val title = task["title"] as String
                        val type = task["taskType"] as String
                        val note = task["taskType"] as String
                        val isDone = task["checked"] as Boolean
                        val dueDateData : HashMap<String, String> = task["dueDate"] as HashMap<String, String>
                        var dueDateYear = dueDateData["year"].toString()
                        var dueDateDate  = dueDateData["date"].toString()
                        var dueDateMonthInt = ((dueDateData["month"]).toString().toInt() + 1) as Int
                        var dueDateMonth = dueDateMonthInt.toString()
                        if(dueDateMonthInt < 10){
                            dueDateMonth = "0$dueDateMonth"
                        }
                        var dueDateDateInt = ((dueDateData["date"]).toString().toInt() + 1) as Int
                        if(dueDateDateInt < 10){
                            dueDateDate = "0$dueDateDate"
                        }
                        val dueDate = formatter.parse("$dueDateDate/$dueDateMonth/$dueDateYear")

                        val moduleTask = Task(title, type, dueDate, note, isDone)
                        println("*****************************")
                        println(moduleTask.toString())
                        println("*****************************")
                        moduleTasks.add(moduleTask)
                    }
                }

                val moduleSessions = ArrayList<ModuleSession>()
                for (session in sessions) {
                    val location = session["location"] as String
                    val sessionType = session["sessionType"] as String
                    val dayOfTheWeek = session["dayOfTheWeek"] as String
                    val startTimeData : HashMap<String, String> = session["startTime"] as HashMap<String, String>
                    val endTimeData : HashMap<String, String> = session["endTime"] as HashMap<String, String>
                    var startTimeHour = startTimeData["hour"].toString()
                    var startTimeMinute = startTimeData["minute"].toString()
                    val startHourLong : Long = startTimeData["hour"] as Long
                    if(startHourLong < 10){
                        startTimeHour = "0"+startTimeHour
                    }
                    val startMinuteLong : Long = startTimeData["minute"] as Long
                    if(startMinuteLong < 10){
                        startTimeMinute = "0"+startTimeMinute
                    }
                    val startTime = "$startTimeHour:$startTimeMinute"
                    var endTimeHour = endTimeData["hour"].toString()
                    var endTimeMinute = endTimeData["minute"].toString()
                    val endHourLong : Long = endTimeData["hour"] as Long
                    if(endHourLong < 10){
                        endTimeHour = "0"+endTimeHour
                    }
                    val endMinuteLong : Long = endTimeData["minute"] as Long
                    if(endMinuteLong < 10){
                        endTimeMinute = "0"+endTimeMinute
                    }
                    val endTime = "$endTimeHour:$endTimeMinute"
                    val moduleSession = ModuleSession(location, sessionType, dayOfTheWeek, LocalTime.parse(startTime), LocalTime.parse(endTime))
                    moduleSessions.add(moduleSession)
                }
                val module = Module(name,colour,moduleSessions, moduleTasks)
                userCloudModules.add(module)
            } catch (e: Exception) {
                Log.e("firebase", "Error casting data", e)
            }
        }
        return userCloudModules
    }

    /**
     * Get sharedpreferences
     * Get UserSQLite
     * CreateUserWithemailandPassword
     * CreatenewUser
     */
    private fun updateUser() {
        var name : String? = null
        var email: String?
        var password: String?

        //Get sharedpreferences
        val sharedPreferences = getSharedPreferences("AuthenticationDB", Context.MODE_PRIVATE)
        var authentication = Authentication()
        val spMap: Map<String, *> = sharedPreferences.all

        if (spMap.isNotEmpty()) {
            if (authentication != null) {
                authentication.loadAuthenications(spMap)
            }
        }
        email = sharedPreferences.getString("MostRecentEmail", "").toString()
        password = sharedPreferences.getString("MostRecentPassword", "").toString()


        //Get UserSQLite
        val database: SQLiteDatabase = UserSQLiteDBHelper(this).getReadableDatabase()
        val cursorUser = database.rawQuery("SELECT user_name FROM user", null)
        if (cursorUser.moveToFirst()) {
            name = cursorUser.getString(0)
        }
        cursorUser.close()

        //CreateUserWithemailandPassword
        val fireAuth = Firebase.auth
        fireAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    println("Email Registered to DB")
                    Log.d(TAG, "createUserWithEmail:success")
                    Toast.makeText(this@MenuActivity, "Email Registered to DB", Toast.LENGTH_SHORT).show()
                    val user = fireAuth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    println("Authentication failed.")
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(this@MenuActivity, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }
            }
            //CreatenewUser
            Thread.sleep(2000)
            fireAuth.signInWithEmailAndPassword(email, password)
            var userId = Firebase.auth.currentUser?.uid
            if (userId != null && email != null && name != null){
                val user = Profile(userId, email, name)
                var dbRef = FirebaseDatabase.getInstance().getReference("users")
                if (userId != null) {
                    dbRef.child(userId).setValue(user)
                        .addOnCompleteListener {
                            println("write to DB Success")
                        }.addOnFailureListener { err ->
                            println("write to DB Fail: $err")
                        }
                }
            }
    }

    private fun updateUI(user: FirebaseUser?) {
    }
}