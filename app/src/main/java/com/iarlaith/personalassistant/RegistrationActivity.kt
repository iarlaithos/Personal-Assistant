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
        val backButton = findViewById<Button>(R.id.btnBackReg)
        var authentication =  Authentication()
        fireAuth = Firebase.auth

        val sharedPreferences = getSharedPreferences("AuthenticationDB", Context.MODE_PRIVATE)
        val spEditor = sharedPreferences.edit()

        if (sharedPreferences != null) {
            val preferencesMap = sharedPreferences.all
            if (preferencesMap.isNotEmpty()) {
                authentication.loadAuthenications(preferencesMap)
            }
        }

        registerButton.setOnClickListener {
            val registerEmail = email.text.toString()
            val registerPassword1 = password1.text.toString()
            val registerPassword2 = password2.text.toString()

            if(validateName(registerEmail) && validatePass(registerPassword1, registerPassword2)){
                if(authentication.checkEmail(registerEmail)){
                    Toast.makeText(this@RegistrationActivity, "email not available", Toast.LENGTH_SHORT).show()
                } else{
                    authentication.addAuthentication(registerEmail, registerPassword1)
                    spEditor.putString(registerEmail, registerPassword1)
                    spEditor.apply()
                    println("1")
                    fireAuth.createUserWithEmailAndPassword(registerEmail, registerPassword1)
                        .addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                println("2")
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "createUserWithEmail:success")
                                Toast.makeText(this@RegistrationActivity, "Email Registered to DB", Toast.LENGTH_SHORT).show()
                                val user = fireAuth.currentUser
                                updateUI(user)
                            } else {
                                println("3")
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.exception)
                                Toast.makeText(this@RegistrationActivity, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show()
                                updateUI(null)
                            }
                        }
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("authentication", authentication)
                    startActivity(intent)
                }
            }
        }

        backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
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
