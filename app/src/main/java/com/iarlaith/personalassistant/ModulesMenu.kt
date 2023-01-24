package com.iarlaith.personalassistant

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class ModulesMenu : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modules_menu)

        val addModule = findViewById<TextView>(R.id.tvAddModule)

        addModule.setOnClickListener {
            val intent = Intent(this, AddModule::class.java)
            startActivity(intent)
        }
    }
}