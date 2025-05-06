package com.example.uas

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val nama = intent.getStringExtra("nama") ?: "Pengguna"
        val nim = intent.getStringExtra("nim") ?: "-"

        val tvWelcome = findViewById<TextView>(R.id.tvWelcome)
        val tvNim = findViewById<TextView>(R.id.tvNim)

        tvWelcome.text = "Selamat datang, $nama"
        tvNim.text = "NIM: $nim"
    }
}
