package com.iarlaith.personalassistant

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private lateinit var fireAuth: FirebaseAuth
    private var isValid = false
    private var counter = 3
    //lateinit var authentication: Authentication

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
        var authentication =  Authentication()

        if(sharedPreferences != null){
            val spMap: Map<String, *> = sharedPreferences.all

            if(spMap.isNotEmpty()){
                if (authentication != null) {
                    authentication.loadAuthenications(spMap)
                }
            }

            val persistedEmail = sharedPreferences.getString("MostRecentEmail", "")
            val persistedPassword = sharedPreferences.getString("MostRecentPassword", "")

            authentication.addAuthentication(persistedEmail, persistedPassword)

            intent.putExtra("authentication", authentication)

            if(sharedPreferences.getBoolean("RememberMeCB", false)){
                email.setText(persistedEmail)
                password.setText(persistedPassword)
                rememberMeCB.isChecked = true
            }
        }

        loginButton.setOnClickListener {
            val inputEmail = email.text.toString()
            val inputPassword = password.text.toString()

            if (inputEmail.isEmpty() || inputPassword.isEmpty()){
                Toast.makeText(this@MainActivity, "Please enter both Email & Password", Toast.LENGTH_SHORT).show()
            }
            else{
                isValid = checkUserFromDB(inputEmail, inputPassword) || authentication.verifyAuthentication(inputEmail, inputPassword)
                println(checkUserFromDB(inputEmail, inputPassword))
                println(authentication.verifyAuthentication(inputEmail, inputPassword))
                if(!isValid){
                    counter--
                    Toast.makeText(this@MainActivity, "Incorrect Email or Password", Toast.LENGTH_SHORT).show()

                    attempts.text = "Attempts Remaining: " + counter

                    if(counter == 0){
                        loginButton.isEnabled = false
                    }
                }
                else{
                    Toast.makeText(this@MainActivity, "Logged in", Toast.LENGTH_SHORT).show()
                    spEditor.putBoolean("RememberMeCB", rememberMeCB.isChecked)
                    spEditor.putString("MostRecentEmail", inputEmail)
                    spEditor.putString("MostRecentPassword", inputPassword)
                    spEditor.apply()

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

        fireAuth.signInWithEmailAndPassword(email, userPassword).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                // Sign in success, update UI with the signed-in user's information
                Log.d(TAG, "signInWithEmail:success")
                val user = fireAuth.currentUser
                updateUI(user)
            } else {
                // If sign in fails, display a message to the user.
                Log.w(TAG, "signInWithEmail:failure", task.exception)
                Toast.makeText(baseContext, "Authentication failed.",
                    Toast.LENGTH_SHORT).show()
                updateUI(null)
            }
        }

        if(fireAuth.currentUser != null){
            return true
        }
        return false
    }

    private fun updateUI(user: FirebaseUser?) {
    }
}