package com.example.androidtest

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private var counter = 0
    private lateinit var textViewCounter: TextView
    private lateinit var editTextName: EditText
    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.my_toolbar))

        // Initialize DatabaseHelper
        databaseHelper = DatabaseHelper(this)

        // If database is empty, insert initial data
        if (databaseHelper.isDatabaseEmpty()) {
            databaseHelper.insertData("Matej", 10)
            databaseHelper.insertData("Nikola", 10)
            databaseHelper.insertData("Bruno", 10)
        }

        textViewCounter = findViewById(R.id.textViewCounter)
        val upButton = findViewById<Button>(R.id.buttonUp)
        val downButton = findViewById<Button>(R.id.buttonDown)

        editTextName = findViewById(R.id.plainTextName)

        // Postavljanje inicijalnog jezika i hint-a
        val sharedPreferences = getSharedPreferences("LanguagePrefs", Context.MODE_PRIVATE)
        val languageCode = sharedPreferences.getString("language", "hr") ?: "hr"

        // Postavljanje hint-a ovisno o jeziku
        editTextName.hint = when(languageCode) {
            "en" -> "Enter your name"
            "hr" -> "Unesite ime"
            else -> "Unesite ime"
        }

        val sharedPref = getPreferences(Context.MODE_PRIVATE)

        // Učitavanje spremljene vrijednosti iz SharedPreferences
        counter = sharedPref.getInt("COUNTER_VALUE", 0)
        textViewCounter.text = counter.toString()

        upButton.setOnClickListener {
            counter++
            textViewCounter.text = counter.toString()

            if(counter == 10) {
                // Dohvaćanje imena, korištenje zadane vrijednosti ako je prazno
                val name = editTextName.text.toString().ifEmpty { "Korisnik" }

                // Insert name into database
                databaseHelper.insertData(name, 10)

                val intent = Intent(this, SuccessActivity::class.java).apply {
                    putExtra("name", name)
                }
                startActivity(intent)

                counter = 0
                textViewCounter.text = counter.toString()
            }
        }

        downButton.setOnClickListener {
            if (counter > 0) {
                counter--
                textViewCounter.text = counter.toString()
            }
        }

        // Registracija dugog pritiska za reset
        registerForContextMenu(textViewCounter)
    }

    override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_float, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_reset -> {
                counter = 0
                textViewCounter.text = counter.toString()
                Toast.makeText(this, "Counter reset", Toast.LENGTH_SHORT).show()
                true
            }
            else -> super.onContextItemSelected(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.english -> {
                changeLanguage("en")
                true
            }
            R.id.croatian -> {
                changeLanguage("hr")
                true
            }
            R.id.restore_counter -> {
                counter = 0
                textViewCounter.text = counter.toString()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun changeLanguage(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val resources = resources
        val configuration = resources.configuration
        configuration.setLocale(locale)

        // Save language preference
        val sharedPreferences = getSharedPreferences("LanguagePrefs", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString("language", languageCode)
            apply()
        }

        // Update resources
        createConfigurationContext(configuration)
        resources.updateConfiguration(configuration, resources.displayMetrics)

        // Restart activity to apply changes
        recreate()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("COUNTER_VALUE", counter)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        counter = savedInstanceState.getInt("COUNTER_VALUE", 0)
        textViewCounter.text = counter.toString()
    }

    override fun onStop() {
        super.onStop()
        val sharedPreferences = getPreferences(Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putInt("COUNTER_VALUE", counter)
            apply()
        }
    }
}