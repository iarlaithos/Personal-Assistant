package com.iarlaith.personalassistant

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        val signOut = findViewById<TextView>(R.id.tvSignOut)
        val homeButton = findViewById<ImageView>(R.id.MbtnHome)
        val modulesButton = findViewById<TextView>(R.id.tvModules)

        signOut.setOnClickListener {
            Firebase.auth.signOut()
            val builder = AlertDialog.Builder(this)
            builder.setMessage("Are you sure you want to Sign Out?")
                .setCancelable(false)
                .setPositiveButton("Sign Out") { dialog, id ->
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }
                .setNegativeButton("Cancel") { dialog, id ->
                    // Dismiss the dialog
                    dialog.dismiss()
                }
            val alert = builder.create()
            alert.show()
        }

        modulesButton.setOnClickListener {
            val intent = Intent(this, ModulesMenu::class.java)
            startActivity(intent)
        }

        homeButton.setOnClickListener {
            val intent = Intent(this, HomePageActivity::class.java)
            startActivity(intent)
        }

    }
}