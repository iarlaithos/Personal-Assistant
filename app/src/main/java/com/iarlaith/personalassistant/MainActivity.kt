package com.iarlaith.personalassistant

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    private var isValid = false
    private var counter = 3


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val username = findViewById<EditText>(R.id.etUsername)
        val password = findViewById<EditText>(R.id.etPassword)
        val loginButton = findViewById<Button>(R.id.btnLogin)
        val attempts = findViewById<TextView>(R.id.tvAttemps)
        val registerButton = findViewById<Button>(R.id.btnRegisterUser)
        val rememberMeCB = findViewById<CheckBox>(R.id.cbRememberMe)

        val sharedPreferences = getSharedPreferences("AuthenticationDB", Context.MODE_PRIVATE)
        val spEditor = sharedPreferences.edit()

        if(sharedPreferences != null){
            val persistedName = sharedPreferences.getString("Name", "")
            val persistedUsername = sharedPreferences.getString("Username", "")
            val persistedPassword = sharedPreferences.getString("Password", "")
            val authentication = Authentication(persistedName, persistedUsername, persistedPassword)
            intent.putExtra("authentication", authentication)

            if(sharedPreferences.getBoolean("RememberMeCB", false)){
                username.setText(persistedUsername)
                password.setText(persistedPassword)
                rememberMeCB.isChecked = true
            }
        }
        loginButton.setOnClickListener {
            var inputUsername = username.text.toString()
            var inputPassword = password.text.toString()

            if (inputUsername.isEmpty() || inputPassword.isEmpty()){
                Toast.makeText(this@MainActivity, "Please enter both Username & Password", Toast.LENGTH_SHORT).show()
            }
            else{
                isValid = validate(inputUsername, inputPassword)

                if(!isValid){
                    counter--
                    Toast.makeText(this@MainActivity, "Incorrect Username or Password", Toast.LENGTH_SHORT).show()

                    attempts.setText("Attempts Remaining: " + counter)

                    if(counter == 0){
                        loginButton.isEnabled = false
                    }
                }
                else{
                    Toast.makeText(this@MainActivity, "Logged in", Toast.LENGTH_SHORT).show()

                    spEditor.putBoolean("RememberMeCB", rememberMeCB.isChecked)
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

    private fun validate(userName: String, userPassword: String): Boolean {
        val auth = intent.getSerializableExtra("authentication") as? Authentication
        if (auth != null) {
            if (userName.equals(auth.username) && userPassword.equals(auth.password)){
                return true
            }
        }

        return false
    }
}