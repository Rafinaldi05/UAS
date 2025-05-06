package com.example.uas

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LoginActivity : AppCompatActivity() {

    private lateinit var etNim: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button

    private val loginService = Retrofit.Builder()
        .baseUrl("https://id.tif.uin-suska.ac.id/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ApiService::class.java)

    private val setoranService = Retrofit.Builder()
        .baseUrl("https://api.tif.uin-suska.ac.id/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ApiService::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        etNim = findViewById(R.id.etNim)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)

        btnLogin.setOnClickListener {
            val nim = etNim.text.toString()
            if (nim.isEmpty()) {
                Toast.makeText(this, "NIM tidak boleh kosong", Toast.LENGTH_SHORT).show()
            } else {
                login(nim)
            }
        }
    }

    private fun login(nim: String) {
        val username = etNim.text.toString()
        val password = etPassword.text.toString()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = loginService.login(username = username, password = password)
                val token = response.access_token

                // Simpan token
                val sharedPref = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
                sharedPref.edit().putString("auth_token", token).apply()

                withContext(Dispatchers.Main) {
                    val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                    intent.putExtra("nama", username)
                    intent.putExtra("nim", username)
                    startActivity(intent)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@LoginActivity,
                        "Login gagal: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}