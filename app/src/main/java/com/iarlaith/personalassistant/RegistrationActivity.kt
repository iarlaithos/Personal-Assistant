package com.iarlaith.personalassistant

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.delay
import java.util.concurrent.TimeUnit


class RegistrationActivity : AppCompatActivity() {

    private lateinit var fireAuth: FirebaseAuth
    private lateinit var dbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        val name = findViewById<EditText>(R.id.etRegisterName)
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
            val registerName = name.text.toString()
            val registerEmail = email.text.toString()
            val registerPassword1 = password1.text.toString()
            val registerPassword2 = password2.text.toString()

            if(validateName(registerEmail) && validatePass(registerPassword1, registerPassword2)){
                if(authentication.checkEmail(registerEmail)){
                    Toast.makeText(this@RegistrationActivity, "email not available", Toast.LENGTH_SHORT).show()
                } else{
                    writeNewUserToSQLite(registerEmail,registerName)
                    authentication.addAuthentication(registerEmail, registerPassword1)
                    spEditor.putString(registerEmail, registerPassword1)
                    spEditor.apply()
                    fireAuth.createUserWithEmailAndPassword(registerEmail, registerPassword1)
                        .addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                // Sign in success, update UI with the signed-in user's information
                                println("Email Registered to DB")
                                Log.d(TAG, "createUserWithEmail:success")
                                Toast.makeText(this@RegistrationActivity, "Email Registered to DB", Toast.LENGTH_SHORT).show()
                                val user = fireAuth.currentUser
                                updateUI(user)
                                writeNewUser(registerEmail, registerName)
                            } else {
                                // If sign in fails, display a message to the user.
                                println("Authentication failed.")
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

    private fun writeNewUser(email: String, name: String) {
        Thread.sleep(2000)

        var userId = Firebase.auth.currentUser!!.uid
        val user = Profile(userId, email, name)

        dbRef = FirebaseDatabase.getInstance().getReference("users")
        dbRef.child(userId).setValue(user)
            .addOnCompleteListener{
                println("write to DB Success")
            }.addOnFailureListener{ err ->
                println("write to DB Fail: $err")
            }
    }

    private fun writeNewUserToSQLite(email: String, name: String) {
        //write module
        val database: SQLiteDatabase = UserSQLiteDBHelper(this).writableDatabase
        val values = ContentValues()
        values.put(
            UserSQLiteDBHelper.USER_COLUMN_NAME,
            name.toString()
        )
        values.put(
            UserSQLiteDBHelper.USER_COLUMN_EMAIL,
            email.toString()
        )

        val newRowId = database.insert(UserSQLiteDBHelper.USER_TABLE, null, values)
    }
}
