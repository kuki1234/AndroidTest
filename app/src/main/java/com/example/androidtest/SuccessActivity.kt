package com.example.androidtest

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.Locale

class SuccessActivity : AppCompatActivity() {
    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        // Postavljanje jezika prije inflacije layout-a
        updateBaseContextLocale(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_success)

        // Initialize DatabaseHelper
        databaseHelper = DatabaseHelper(this)



        // Dohvaćanje View elemenata
        val textView: TextView = findViewById(R.id.textView)
        val btnSendSMS: Button = findViewById(R.id.btnSendSMS)
        val phoneNumberRadioGroup: RadioGroup = findViewById(R.id.phoneNumberRadioGroup)

        // Dohvaćanje korisničkog imena izIntent-a, s mogućnošću fallback vrijednosti
        val name = intent.getStringExtra("name")
            ?.replace("Name:", "")  // Uklanjanje "Name:" prefiksa ako postoji
            ?.trim()
            ?: "Korisnik"  // Zadana vrijednost ako je null

        // Dohvaćanje liste uspješnih korisnika iz baze podataka
        val successfulUsers = databaseHelper.getSuccessfulUsers()

        // Kreiranje comprehensive poruke koja uključuje osobnu poruku i listu uspješnih korisnika
        val completeMessage = """
            Čestitamo, $name! Uspješno ste napravili 10 koraka!
            
            Successful users:
            ${successfulUsers.joinToString("\n")}
        """.trimIndent()

        textView.text = completeMessage

        val buttonDeleteData = findViewById<Button>(R.id.buttonDeleteData)
        buttonDeleteData.setOnClickListener {
            databaseHelper.deleteAllData()
            findViewById<TextView>(R.id.textView).text = ""
        }

        // Funkcionalnost slanja SMS-a s lokalizacijom
        btnSendSMS.setOnClickListener {
            // Odabir broja telefona
            val selectedPhoneNumber = when (phoneNumberRadioGroup.checkedRadioButtonId) {
                R.id.radioNumber1 -> "0917829521"
                R.id.radioNumber2 -> "0998765432"
                R.id.radioNumber3 -> "0951234567"
                else -> "0917829521"  // Zadani broj
            }

            // Dohvaćanje trenutnog jezika
            val sharedPreferences = getSharedPreferences("LanguagePrefs", Context.MODE_PRIVATE)
            val languageCode = sharedPreferences.getString("language", "hr") ?: "hr"

            // Odabir odgovarajuće SMS poruke ovisno o jeziku
            val message = when (languageCode) {
                "en" -> "Congratulations, $name! You've completed 10 steps!"
                "hr" -> "Čestitamo, $name! Uspješno ste napravili 10 koraka!"
                else -> "Čestitamo, $name! Uspješno ste napravili 10 koraka!"  // Zadani tekst
            }

            try {
                // Kreiranje SMS intent-a
                val smsIntent = Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("smsto:$selectedPhoneNumber")
                    putExtra("sms_body", message)
                }

                // Pokretanje SMS aktivnosti
                startActivity(smsIntent)
            } catch (e: Exception) {
                // Prikaz greške ako slanje SMS-a nije uspjelo
                Toast.makeText(this, "Slanje SMS-a nije uspjelo", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Metoda za ažuriranje konteksta s trenutnim jezikom
    private fun updateBaseContextLocale(context: Context): Context {
        val sharedPreferences = context.getSharedPreferences("LanguagePrefs", Context.MODE_PRIVATE)
        val languageCode = sharedPreferences.getString("language", "hr") ?: "hr"

        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val configuration = Configuration()
        configuration.setLocale(locale)

        return context.createConfigurationContext(configuration)
    }
}