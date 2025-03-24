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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.my_toolbar))

        textViewCounter = findViewById(R.id.textViewCounter)
        val upButton = findViewById<Button>(R.id.buttonUp)
        val downButton = findViewById<Button>(R.id.buttonDown)

        editTextName = findViewById(R.id.plainTextName)

        // Postavljanje inicijalnog jezika i hint-a
        val sharedPreferences = getSharedPreferences("LanguagePrefs", Context.MODE_PRIVATE)
        val languageCode = sharedPreferences.getString("language", "hr") ?: "hr"

        // Postavljanje hint-a ovisno o jeziku
        editTextName.hint = when(languageCode) {
            "en" -> getString(R.string.name_hint_en)
            "hr" -> getString(R.string.name)
            else -> getString(R.string.name)
        }

        val sharedPref = getPreferences(Context.MODE_PRIVATE)

        // Učitavanje spremljene vrijednosti iz SharedPreferences
        counter = sharedPref.getInt("COUNTER_VALUE", 0)
        counter = 0
        textViewCounter.text = counter.toString()

        upButton.setOnClickListener {
            counter++
            if(counter == 10) {
                counter = 0

                // Dohvaćanje imena, korištenje zadane vrijednosti ako je prazno
                val name = editTextName.text.toString().ifEmpty { "Korisnik" }

                val intent = Intent(this, SuccessActivity::class.java).apply {
                    putExtra("name", name)
                }
                startActivity(intent)
            }
            textViewCounter.text = counter.toString()
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

    // Kreiranje kontekstnog menija na dug pritisak
    override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_float, menu)
    }

    // Rukovanje odabirom opcije u meniju
    override fun onContextItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_reset -> {
                counter = 0
                textViewCounter.text = counter.toString()
                Toast.makeText(this, getString(R.string.resetiraj), Toast.LENGTH_SHORT).show()
                true
            }
            else -> super.onContextItemSelected(item)
        }
    }

    // Spremanje trenutnog brojača prilikom promjene orijentacije
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("COUNTER_VALUE", counter)
    }

    // Vraćanje vrijednosti brojača nakon promjene orijentacije
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        counter = savedInstanceState.getInt("COUNTER_VALUE", 0)
        textViewCounter.text = counter.toString()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.english -> {
                changeLanguage(this, "en")
                recreate()
                true
            }
            R.id.croatian -> {
                changeLanguage(this, "hr")
                recreate()
                true
            }
            R.id.restore_counter -> {
                counter = 0
                textViewCounter.text = counter.toString()
                Toast.makeText(this, getString(R.string.resetiraj), Toast.LENGTH_SHORT).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    @Suppress("DEPRECATION")
    fun changeLanguage(context: Context, language: String) {
        val locale = Locale(language)
        Locale.setDefault(locale)

        // Spremanje odabranog jezika u SharedPreferences
        val sharedPreferences = getSharedPreferences("LanguagePrefs", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString("language", language)
            apply()
        }

        val res = context.resources
        val config = Configuration(res.configuration)
        config.setLocale(locale)
        context.createConfigurationContext(config)
        res.updateConfiguration(config, res.displayMetrics)

        // Ažuriranje hint-a za EditText ovisno o jeziku
        editTextName.hint = when(language) {
            "en" -> getString(R.string.name_hint_en)
            "hr" -> getString(R.string.name)
            else -> getString(R.string.name)
        }
    }

    // Lifecycle metode za logging
    override fun onStart() {
        super.onStart()
        Toast.makeText(applicationContext, "onStart", Toast.LENGTH_SHORT).show()
        Log.i("MyLog", "onStart")
    }

    override fun onResume() {
        super.onResume()
        Toast.makeText(applicationContext, "onResume", Toast.LENGTH_SHORT).show()
        Log.i("MyLog", "onResume")
    }

    override fun onPause() {
        super.onPause()
        Toast.makeText(applicationContext, "onPause", Toast.LENGTH_SHORT).show()
        Log.i("MyLog", "onPause")
    }

    override fun onStop() {
        super.onStop()
        val sharedPreferences = getPreferences(Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putInt("COUNTER_VALUE", counter)
            apply()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Toast.makeText(applicationContext, "onDestroy", Toast.LENGTH_SHORT).show()
        Log.i("MyLog", "onDestroy")
    }

    override fun onRestart() {
        super.onRestart()
        Toast.makeText(applicationContext, "onRestart", Toast.LENGTH_SHORT).show()
        Log.i("MyLog", "onRestart")
    }
}