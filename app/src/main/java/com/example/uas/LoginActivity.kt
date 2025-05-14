package com.example.uas

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.uas.ui.theme.UASTheme
import com.example.uas.ui.theme.LoginScreen
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LoginActivity : ComponentActivity() {

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

        setContent {
            UASTheme {
                Surface(modifier = Modifier) {
                    LoginScreen { nim, password ->
                        performLogin(nim, password)
                    }
                }
            }
        }
    }

    private fun performLogin(nim: String, password: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = loginService.login(username = nim, password = password)
                val token = response.accessToken

                if (token.isNullOrBlank()) {
                    throw Exception("Token tidak ditemukan dalam respons.")
                }

                val mahasiswaResponse = setoranService.getMahasiswa("Bearer $token")

                if (mahasiswaResponse.isSuccessful) {
                    val data = mahasiswaResponse.body()
                    val nama = data?.data?.info?.nama ?: "Nama Tidak Diketahui"
                    val nimResmi = data?.data?.info?.nim ?: "NIM Tidak Diketahui"
                    val email =data?.data?.info?.email ?: "Email Tidak Diketahui"

                    val sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
                    sharedPref.edit().putString("auth_token", token).apply()

                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@LoginActivity, "Selamat datang $nama ($nimResmi)", Toast.LENGTH_SHORT).show()

                        val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                        intent.putExtra("TOKEN", token)
                        intent.putExtra("NAMA", nama)
                        intent.putExtra("NIM", nimResmi)
                        intent.putExtra("Email", email)
                        startActivity(intent)
                        finish()
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@LoginActivity, "Gagal mendapatkan data mahasiswa", Toast.LENGTH_SHORT).show()
                    }
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@LoginActivity, "Login gagal: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
