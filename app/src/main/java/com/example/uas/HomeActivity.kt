package com.example.uas

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uas.ui.theme.UASTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class HomeActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val token = intent.getStringExtra("TOKEN") ?: ""
        val nama = intent.getStringExtra("NAMA") ?: "Nama Kosong"
        val nim = intent.getStringExtra("NIM") ?: "NIM Kosong"
        val email = intent.getStringExtra("Email") ?: "Email Kosong"
        val angkatan = intent.getStringExtra("Angkatan") ?: "-"
        val semester = intent.getIntExtra("Semester", 0)
        val dosenPa = intent.getStringExtra("DosenPa") ?: "Nama Kosong"
        val nipPa = intent.getStringExtra("nipDosenPa") ?: "NIP Kosong"
        val emailPa = intent.getStringExtra("emailDosenPa") ?: "Email Kosong"

        viewModel.fetchData(token)

        setContent {
            UASTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val setoranList by viewModel.setoranList.collectAsState()
                    val ringkasanList by viewModel.ringkasanList.collectAsState()
                    val isLoading by viewModel.isLoading.collectAsState()
                    val errorMessage by viewModel.errorMessage.collectAsState()

                    when {
                        isLoading -> {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        }

                        errorMessage != null -> {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text(text = errorMessage ?: "Terjadi kesalahan")
                            }
                        }

                        else -> {
                            MainScreen(
                                nama = nama,
                                nim = nim,
                                email = email,
                                angkatan = angkatan,
                                semester = semester,
                                dosenPa = dosenPa,
                                nipPa = nipPa,
                                emailPa = emailPa,
                                setoranList = setoranList,
                                ringkasanList = ringkasanList,
                                onLogout = {
                                    logout()
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    private fun logout() {
        val sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val refreshToken = sharedPref.getString("refresh_token", null)

        if (refreshToken != null) {
            val loginApi = Retrofit.Builder()
                .baseUrl("https://id.tif.uin-suska.ac.id/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService::class.java)

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = loginApi.logout(refreshToken = refreshToken)
                    if (response.isSuccessful) {
                        withContext(Dispatchers.Main) {
                            sharedPref.edit().clear().apply()

                            val intent = Intent(this@HomeActivity, LoginActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                            finish()
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@HomeActivity, "Gagal logout: ${response.code()}", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@HomeActivity, "Error logout: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } else {
            sharedPref.edit().clear().apply()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

}

class MainViewModel : ViewModel() {

    private val _setoranList = MutableStateFlow<List<DataModels.SetoranItem>>(emptyList())
    val setoranList: StateFlow<List<DataModels.SetoranItem>> = _setoranList

    private val _ringkasanList = MutableStateFlow<List<DataModels.RingkasanItem>>(emptyList())
    val ringkasanList: StateFlow<List<DataModels.RingkasanItem>> = _ringkasanList

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.tif.uin-suska.ac.id/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    fun fetchData(token: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val response = apiService.getMahasiswa("Bearer $token")
                if (response.isSuccessful) {
                    val data = response.body()
                    val list = data?.data?.setoran?.detail ?: emptyList()
                    val ringkasan = data?.data?.setoran?.ringkasan ?: emptyList()
                    _setoranList.value = list
                    _ringkasanList.value = ringkasan
                } else {
                    _errorMessage.value = "Gagal mengambil data: ${response.code()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
