package com.example.uas

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import com.example.uas.ui.theme.LoginScreen
import com.example.uas.ui.theme.UASTheme
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LoginActivity : ComponentActivity() {

    private var isLoading = mutableStateOf(false)
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
                    LoginScreen(
                        isLoading = isLoading.value,
                        onLoginClick = { nim, password -> performLogin(nim, password) },
                        onLoginStart = { isLoading.value = true },
                        onLoginFailed = { isLoading.value = false }
                    )
                }
            }
        }
    }

    private fun performLogin(nim: String, password: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = loginService.login(username = nim, password = password)
                val token = response.accessToken
                val refreshToken = response.refreshToken
                val idToken = response.idToken

                if (token.isNullOrBlank()) {
                    throw Exception("Token tidak ditemukan dalam respons.")
                }

                val mahasiswaResponse = setoranService.getMahasiswa("Bearer $token")

                if (mahasiswaResponse.isSuccessful) {
                    val info: DataModels.MahasiswaInfo? = mahasiswaResponse.body()?.data?.info

                    if (info != null) {
                        val sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
                        sharedPref.edit()
                            .putString("auth_token", token)
                            .putString("refresh_token", refreshToken)
                            .putString("id_token", idToken)
                            .apply()

                        withContext(Dispatchers.Main) {
                            isLoading.value = false
//                            showSuccessLoading.value = false

                            Toast.makeText(this@LoginActivity, "Selamat datang ${info.nama} (${info.nim})", Toast.LENGTH_SHORT).show()

                            val intent = Intent(this@LoginActivity, LoginSuccessActivity::class.java).apply {
                                putExtra("TOKEN", token)
                            }
                            startActivity(intent)
                            finish()
                        }
                    } else {
                        throw Exception("Data info mahasiswa kosong")
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        isLoading.value = false
                        Toast.makeText(this@LoginActivity, "Gagal mendapatkan data mahasiswa", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    isLoading.value = false
                    Toast.makeText(this@LoginActivity, "Login gagal: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
