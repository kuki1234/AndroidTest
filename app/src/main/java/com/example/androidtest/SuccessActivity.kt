package com.example.androidtest

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SuccessActivity : AppCompatActivity() {
    lateinit var textView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_success)

        textView = findViewById(R.id.textView)
        val btnSendSMS = findViewById<Button>(R.id.btnSendSMS)

        // Preuzimanje imena korisnika iz Intenta
        val name = intent.getStringExtra("name") ?: "Korisnik"  // Ako ime nije proslijeđeno, koristi "Korisnik"

        // Kreiraj tekst pomoću getString i formatiranja
        val successMessage = getString(R.string.success, name)

        // Postavi tekst u TextView
        textView.text = successMessage

        btnSendSMS.setOnClickListener {
            val phoneNumber = "0917829521"
            val message = "$name je upravo završio trčanje i prešao 10 koraka! 🏃‍♂️💪"

            val smsIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("smsto:$phoneNumber")
                putExtra("sms_body", message)
            }

            startActivity(smsIntent)
        }
    }
}



