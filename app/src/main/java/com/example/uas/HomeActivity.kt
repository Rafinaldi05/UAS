package com.example.uas

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val nama = intent.getStringExtra("nama")
        val nim = intent.getStringExtra("nim")

        val textView = TextView(this).apply {
            textSize = 20f
            text = "Selamat datang, $nama\nNIM: $nim"
            setPadding(24, 24, 24, 24)
        }

        setContentView(textView)
    }
}
