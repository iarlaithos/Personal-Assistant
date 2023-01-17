package com.iarlaith.personalassistant

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class RegistrationActivity : AppCompatActivity() {

    private lateinit var fireAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        val email = findViewById<EditText>(R.id.etRegisterEmail)
        val password1 = findViewById<EditText>(R.id.etRegisterPassword1)
        val password2 = findViewById<EditText>(R.id.etRegisterPassword2)
        val registerButton = findViewById<Button>(R.id.btnRegister)
        var authentication =  Authentication()
        fireAuth = Firebase.auth

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
            val registerEmail = email.text.toString()
            println("1")
            val registerPassword1 = password1.text.toString()
            println("2")
            val registerPassword2 = password2.text.toString()
            println("3")

            if(validateName(registerEmail) && validatePass(registerPassword1, registerPassword2)){
                println("4")
                if (authentication != null) {
                    println("5")
                    if(authentication.checkEmail(registerEmail)){
                        println("6")
                        Toast.makeText(this@RegistrationActivity, "email not available", Toast.LENGTH_SHORT).show()
                    } else{
                        println("7")
                        authentication.addAuthentication(registerEmail, registerPassword1)
                        spEditor.putString(registerEmail, registerPassword1)
                        spEditor.apply()
                        // TODO Upload data to DB
                        fireAuth.createUserWithEmailAndPassword(registerEmail, registerPassword1)
                            .addOnCompleteListener(this) { task ->
                                if (task.isSuccessful) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "createUserWithEmail:success")
                                    val user = fireAuth.currentUser
                                    updateUI(user)
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                                    Toast.makeText(baseContext, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show()
                                    updateUI(null)
                                }
                            }
                        // TODO Upload data to DB
                        val intent = Intent(this, MainActivity::class.java)
                        intent.putExtra("authentication", authentication)
                        startActivity(intent)
                    }
                }
            }
        }
    }

    private fun validateName(email: String): Boolean {
        if(email.isEmpty()){
            Toast.makeText(this@RegistrationActivity, "Please enter a email", Toast.LENGTH_SHORT).show()
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

    private fun updateUI(user: FirebaseUser?) {
    }
}
