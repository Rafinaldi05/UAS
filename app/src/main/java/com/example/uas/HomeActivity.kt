package com.example.uas

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class HomeActivity : AppCompatActivity() {

    private lateinit var tvWelcome: TextView
    private lateinit var tvNim: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: SetoranAdapter

    private val setoranService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.tif.uin-suska.ac.id/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        tvWelcome = findViewById(R.id.tvWelcome)
        tvNim = findViewById(R.id.tvNim)
        recyclerView = findViewById(R.id.recyclerViewSetoran)

        adapter = SetoranAdapter()
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        val token = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
            .getString("auth_token", null)

        if (token != null) {
            fetchSetoranData("Bearer $token")
        } else {
            Toast.makeText(this, "Token tidak ditemukan", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchSetoranData(authHeader: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = setoranService.getMahasiswa(authHeader)

                if (response.isSuccessful) {
                    val responseBody = response.body()

                    if (responseBody != null) {
                        val nama = responseBody.data.info.nama
                        val nim = responseBody.data.info.nim

                        // 🔧 Bungkus SetoranItem jadi List
                        val setoranList = listOf(responseBody.data.setoran)

                        withContext(Dispatchers.Main) {
                            tvWelcome.text = "Selamat datang, $nama"
                            tvNim.text = "NIM: $nim"
                            adapter.submitList(setoranList)
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                this@HomeActivity,
                                "Data kosong dari server.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                } else {
                    val errorText = response.errorBody()?.string()
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@HomeActivity, "Gagal: $errorText", Toast.LENGTH_LONG)
                            .show()
                    }
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@HomeActivity, "Kesalahan: ${e.message}", Toast.LENGTH_LONG)
                        .show()
                }
            }
        }
    }
}
