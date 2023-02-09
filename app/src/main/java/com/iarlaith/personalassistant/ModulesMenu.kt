package com.iarlaith.personalassistant

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


class ModulesMenu : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modules_menu)

        val homeButton = findViewById<ImageButton>(R.id.vmbtnHome)
        val menuButton = findViewById<ImageButton>(R.id.vmbtnMenu)
        val addModule = findViewById<TextView>(R.id.tvAddModule)
        val viewModules = findViewById<TextView>(R.id.tvViewModules)
        val editModules = findViewById<TextView>(R.id.tvEditModules)


        addModule.setOnClickListener {
            val intent = Intent(this, AddModule::class.java)
            startActivity(intent)
        }

        homeButton.setOnClickListener {
            val intent = Intent(this, HomePageActivity::class.java)
            startActivity(intent)
        }

        menuButton.setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
        }

        viewModules.setOnClickListener {
            val intent = Intent(this, ViewModulesActivity::class.java)
            startActivity(intent)
        }

        editModules.setOnClickListener {
            val intent = Intent(this, EditModuleActivity::class.java)
            startActivity(intent)
        }
    }
}