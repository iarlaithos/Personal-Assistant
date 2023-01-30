package com.iarlaith.personalassistant

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson


class ModulesMenu : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modules_menu)

        val addModule = findViewById<TextView>(R.id.tvAddModule)
        val viewModule = findViewById<TextView>(R.id.tvViewModules)

        addModule.setOnClickListener {
            val intent = Intent(this, AddModule::class.java)
            startActivity(intent)
        }

        viewModule.setOnClickListener{
            val getSharedPreferences = getSharedPreferences("ModulesDB", Context.MODE_PRIVATE)
            val gson = Gson()
            val json = getSharedPreferences.getString("Module", "")
            val module: List<Module> = gson.fromJson(json, List::class.java) as List<Module>
            println(module.toString())
        }
    }
}