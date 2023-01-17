package com.iarlaith.personalassistant

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


class RegistrationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        val username = findViewById<EditText>(R.id.etRegisterUsernmae)
        val password1 = findViewById<EditText>(R.id.etRegisterPassword1)
        val password2 = findViewById<EditText>(R.id.etRegisterPassword2)
        val registerButton = findViewById<Button>(R.id.btnRegister)

        var authentication =  Authentication()

        val sharedPreferences = getSharedPreferences("AuthenticationDB", Context.MODE_PRIVATE)
        val spEditor = sharedPreferences.edit()

        if (sharedPreferences != null) {
            val preferencesMap = sharedPreferences.all
            if (preferencesMap.size != 0) {
                authentication.loadAuthenications(preferencesMap)
            }
        }

        registerButton.setOnClickListener {
            Toast.makeText(this@RegistrationActivity, "Register button clicked", Toast.LENGTH_SHORT).show()
            val registerUsername = username.text.toString()
            println("1")
            val registerPassword1 = password1.text.toString()
            println("2")
            val registerPassword2 = password2.text.toString()
            println("3")

            if(validateName(registerUsername) && validatePass(registerPassword1, registerPassword2)){
                println("4")
                if (authentication != null) {
                    println("5")
                    if(authentication.checkUsername(registerUsername)){
                        println("6")
                        Toast.makeText(this@RegistrationActivity, "Username not available", Toast.LENGTH_SHORT).show()
                    } else{
                        println("7")
                        authentication.addAuthentication(registerUsername, registerPassword1)
                        spEditor.putString(registerUsername, registerPassword1)
                        spEditor.apply()
                        val intent = Intent(this, MainActivity::class.java)
                        intent.putExtra("authentication", authentication)
                        startActivity(intent)
                    }
                }
            }
        }
    }

    private fun validateName(userName: String): Boolean {
        if(userName.isEmpty()){
            Toast.makeText(this@RegistrationActivity, "Please enter a username", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun validatePass(password1: String, password2: String): Boolean {

        if(password1.isEmpty() || password2.isEmpty()){
            Toast.makeText(this@RegistrationActivity, "Please enter a password", Toast.LENGTH_SHORT).show()
            return false
        }

        if(password1 != password2){
            Toast.makeText(this@RegistrationActivity, "Passwords do not match", Toast.LENGTH_SHORT).show()
            return false
        }

        if(password1.length < 8){
            Toast.makeText(this@RegistrationActivity, "Password must be at least 8 characters", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }
}
