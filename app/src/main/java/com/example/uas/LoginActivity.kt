package com.example.uas

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
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LoginActivity : AppCompatActivity() {

    private lateinit var etNim: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button

    private val loginService: ApiService by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        Retrofit.Builder()
            .baseUrl("https://id.tif.uin-suska.ac.id/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    private val setoranService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.tif.uin-suska.ac.id/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        etNim = findViewById(R.id.etNim)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)

        btnLogin.setOnClickListener {
            val nim = etNim.text.toString()
            val password = etPassword.text.toString()

            if (nim.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "NIM dan password tidak boleh kosong", Toast.LENGTH_SHORT).show()
            } else {
                login(nim, password)
            }
        }
    }

    private fun login(nim: String, password: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = loginService.login(
                    username = nim,
                    password = password
                )

                val token = response.accessToken
                if (token.isNullOrBlank()) {
                    throw Exception("Token tidak ditemukan dalam respons.")
                }

                val mahasiswaResponse = setoranService.getMahasiswa("Bearer $token")

                if (mahasiswaResponse.isSuccessful) {
                    val data = mahasiswaResponse.body()
                    val nama = data?.data?.info?.nama
                    val nimResmi = data?.data?.info?.nim

                    val sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
                    sharedPref.edit().putString("auth_token", token).apply()

                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@LoginActivity, "Selamat datang $nama ($nimResmi)", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
                        finish()
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@LoginActivity, "Gagal mendapatkan data mahasiswa", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@LoginActivity, "Login gagal: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
