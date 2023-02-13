package com.iarlaith.personalassistant

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private lateinit var fireAuth: FirebaseAuth
    private var isValid = false
    private var counter = 3
    //lateinit var authentication: Authentication

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val email = findViewById<EditText>(R.id.etEmail)
        val password = findViewById<EditText>(R.id.etPassword)
        val loginButton = findViewById<Button>(R.id.btnLogin)
        val attempts = findViewById<TextView>(R.id.tvAttemps)
        val registerButton = findViewById<Button>(R.id.btnRegisterUser)
        val rememberMeCB = findViewById<CheckBox>(R.id.cbRememberMe)

        val sharedPreferences = getSharedPreferences("AuthenticationDB", Context.MODE_PRIVATE)
        val spEditor = sharedPreferences.edit()

        fireAuth = Firebase.auth
        val authentication = Authentication()

        if (sharedPreferences != null) {
            val spMap: Map<String, *> = sharedPreferences.all

            if (spMap.isNotEmpty()) {
                authentication.loadAuthenications(spMap)
            }

            val persistedEmail = sharedPreferences.getString("MostRecentEmail", "")
            val persistedPassword = sharedPreferences.getString("MostRecentPassword", "")

            authentication.addAuthentication(persistedEmail, persistedPassword)
            intent.putExtra("authentication", authentication)

            if (sharedPreferences.getBoolean("RememberMeCB", false)) {
                email.setText(persistedEmail)
                password.setText(persistedPassword)
                rememberMeCB.isChecked = true
            }

            if (checkUserFromDB(
                    persistedEmail.toString(),
                    persistedPassword.toString()
                ) && rememberMeCB.isChecked
            ) {
                correctSQLDB(this)
                correctSQLToDoDB(this)
                val intent = Intent(this, HomePageActivity::class.java)
                startActivity(intent)
            }
        }

        loginButton.setOnClickListener {
            val inputEmail = email.text.toString()
            val inputPassword = password.text.toString()

            if (inputEmail.isEmpty() || inputPassword.isEmpty()) {

            } else {
                isValid = checkUserFromDB(
                    inputEmail,
                    inputPassword
                ) || authentication.verifyAuthentication(inputEmail, inputPassword)
                if (!isValid) {
                    counter--
                    Toast.makeText(
                        this@MainActivity,
                        "Incorrect Email or Password",
                        Toast.LENGTH_SHORT
                    ).show()

                    attempts.text = "Attempts Remaining: $counter"

                    if (counter == 0) {
                        loginButton.isEnabled = false
                    }
                } else {
                    Toast.makeText(this@MainActivity, "Logged in", Toast.LENGTH_SHORT).show()
                    spEditor.putBoolean("RememberMeCB", rememberMeCB.isChecked)
                    spEditor.putString("MostRecentEmail", inputEmail)
                    spEditor.putString("MostRecentPassword", inputPassword)
                    spEditor.apply()
                    correctSQLDB(this)
                    correctSQLToDoDB(this)
                    val intent = Intent(this, HomePageActivity::class.java)
                    startActivity(intent)
                }
            }
        }

        registerButton.setOnClickListener {
            val intent = Intent(this, RegistrationActivity::class.java)
            startActivity(intent)
        }
    }

    private fun checkUserFromDB(email: String, userPassword: String): Boolean {
        fireAuth = Firebase.auth

        if (email.isEmpty() || userPassword.isEmpty()) {
            return false
        } else {
            fireAuth.signInWithEmailAndPassword(email, userPassword)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithEmail:success")
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail:failure", task.exception)
                    }
                }
        }
        if (fireAuth.currentUser != null) {
            return true
        }
        return false
    }

    private fun updateUI(user: FirebaseUser?) {
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun correctSQLDB(activity: Activity) {
        val addModule = AddModule()
        val menuActivityFunctions = MenuActivity()
        //if(menuActivityFunctions.getLocalModuleData() != null) {
        val userLocalModules = menuActivityFunctions.getLocalModuleData(this)
        menuActivityFunctions.getFirebaseModuleData { userCloudModules ->
            if (userCloudModules == null) {
                Log.d(TAG, "User cloud modules is null")
                return@getFirebaseModuleData
            }
            if (userLocalModules != null) {
                if (userLocalModules.toString() != userCloudModules.toString() && userLocalModules.size <= userCloudModules.size) {
                    val differenceModules = userCloudModules.minus(userLocalModules)
                    for (diffModule in differenceModules) {
                        addModule.writeNewModuleToSQLite(diffModule, activity)
                    }
                }
            } else {
                menuActivityFunctions.getFirebaseModuleData { userCloudModules ->
                    if (userCloudModules != null) {
                        for (module in userCloudModules) {
                            addModule.writeNewModuleToSQLite(module, this)
                        }
                    }
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun correctSQLToDoDB(activity: Activity) {
        val correctToDos = CorrectToDos()
        val toDoListActivity = ToDoListActivity()
        val userLocalTodos = correctToDos.getLocalTodoData(this)

        correctToDos.getFirebaseTodoData { userCloudTodos ->
            if (userCloudTodos == null)
            {
                Log.d(TAG, "User cloud todos is null")
                return@getFirebaseTodoData
            }else{
                if (userLocalTodos != null)
                {
                    if (userLocalTodos.toString() != userCloudTodos.toString() && userLocalTodos.size <= userCloudTodos.size)
                    {
                        val differenceTodos = userCloudTodos.minus(userLocalTodos)
                        for (diffTodo in differenceTodos)
                        {
                            toDoListActivity.writetoSQLite(diffTodo, activity)
                        }
                    }
                } else
                {
                    for (todo in userCloudTodos)
                    {
                        toDoListActivity.writetoSQLite(todo, activity)
                    }
                }
            }
        }// end of get
    }
}
