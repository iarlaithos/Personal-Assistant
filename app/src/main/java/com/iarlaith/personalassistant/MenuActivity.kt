package com.iarlaith.personalassistant

import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.iarlaith.personalassistant.ModuleSQLiteDBHelper.MODULES_TABLE

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

            val db: SQLiteDatabase = ModuleSQLiteDBHelper(this).writableDatabase
            db.delete(MODULES_TABLE,null,null);
            db.execSQL("delete from "+ MODULES_TABLE);
            db.delete(ModuleSQLiteDBHelper.MODULE_SESSIONS_TABLE,null,null);
            db.execSQL("delete from "+ ModuleSQLiteDBHelper.MODULE_SESSIONS_TABLE);
            db.close()
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