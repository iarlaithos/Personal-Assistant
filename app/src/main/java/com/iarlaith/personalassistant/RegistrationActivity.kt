package com.iarlaith.personalassistant

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast


class RegistrationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        val name = findViewById<EditText>(R.id.etRegisterName)
        val username = findViewById<EditText>(R.id.etRegisterUsernmae)
        val password1 = findViewById<EditText>(R.id.etRegisterPassword1)
        val password2 = findViewById<EditText>(R.id.etRegisterPassword2)
        val registerButton = findViewById<Button>(R.id.btnRegister)

        val sharedPreferences = getSharedPreferences("AuthenticationDB", Context.MODE_PRIVATE)
        val spEditor = sharedPreferences.edit()

        registerButton.setOnClickListener {
            var registerName = name.text.toString()
            var registerUsername = username.text.toString()
            var registerPassword1 = password1.text.toString()
            var registerPassword2 = password2.text.toString()

            if(validateName(registerName, registerUsername) && validatePass(registerPassword1, registerPassword2)){
                val authentication = Authentication(registerName, registerUsername, registerPassword1)
                //Store info using persistent cookies
                spEditor.putString("Name", registerName)
                spEditor.putString("Username", registerUsername)
                spEditor.putString("Password", registerPassword1)
                spEditor.apply()

                Toast.makeText(this@RegistrationActivity, "Register Success", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("authentication", authentication)
                startActivity(intent)
            }
        }
    }

    private fun validateName(name: String, userName: String): Boolean {

        if (name.isEmpty()){
            Toast.makeText(this@RegistrationActivity, "Please enter your name", Toast.LENGTH_SHORT).show()
            return false
        }

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

        if(password1.length < 8 || password2.length < 8){
            Toast.makeText(this@RegistrationActivity, "Password must be at least 8 characters", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }
}
