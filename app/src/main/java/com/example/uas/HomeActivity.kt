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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uas.ui.theme.UASTheme
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class HomeActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val token = intent.getStringExtra("TOKEN") ?: ""

        viewModel.fetchData(token)

        setContent {
            UASTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val setoranList by viewModel.setoranList.collectAsState()
                    val ringkasanList by viewModel.ringkasanList.collectAsState()
                    val isLoading by viewModel.isLoading.collectAsState()
                    val isRefreshing by viewModel.isRefreshing.collectAsState()
                    val errorMessage by viewModel.errorMessage.collectAsState()
                    val mahasiswaInfo by viewModel.mahasiswaInfo.collectAsState()

                    when {
                        isLoading -> {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        }

                        errorMessage != null -> {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text(text = errorMessage ?: "Terjadi kesalahan")
                            }
                        }

                        mahasiswaInfo != null -> {
                            MainScreen(
                                mahasiswaInfo = mahasiswaInfo!!,
                                setoranList = setoranList,
                                ringkasanList = ringkasanList,
                                isRefreshing = isRefreshing,
                                onRefresh = { viewModel.refreshData(token) },
                                onLogout = { logout() }
                            )
                        }
                    }
                }
            }
        }
    }

    private fun logout() {
        val sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val idToken = sharedPref.getString("id_token", null)

        if (idToken != null) {
            val loginApi = Retrofit.Builder()
                .baseUrl("https://id.tif.uin-suska.ac.id/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService::class.java)

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = loginApi.logout(idToken = idToken)
                    if (response.isSuccessful) {
                        withContext(Dispatchers.Main) {
                            sharedPref.edit().clear().apply()
                            startLoginActivity()
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
            startLoginActivity()
        }
    }

    private fun startLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}


class MainViewModel : ViewModel() {

    private val _mahasiswaInfo = MutableStateFlow<DataModels.MahasiswaInfo?>(null)
    val mahasiswaInfo: StateFlow<DataModels.MahasiswaInfo?> = _mahasiswaInfo

    private val _setoranList = MutableStateFlow<List<DataModels.SetoranItem>>(emptyList())
    val setoranList: StateFlow<List<DataModels.SetoranItem>> = _setoranList

    private val _ringkasanList = MutableStateFlow<List<DataModels.RingkasanItem>>(emptyList())
    val ringkasanList: StateFlow<List<DataModels.RingkasanItem>> = _ringkasanList

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.tif.uin-suska.ac.id/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    fun refreshData(token: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _isRefreshing.value = true
            try {
                val response = apiService.getMahasiswa("Bearer $token")
                if (response.isSuccessful) {
                    val data = response.body()
                    _mahasiswaInfo.value = data?.data?.info
                    _setoranList.value = data?.data?.setoran?.detail ?: emptyList()
                    _ringkasanList.value = data?.data?.setoran?.ringkasan ?: emptyList()
                }
            } catch (_: Exception) {
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    fun fetchData(token: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val response = apiService.getMahasiswa("Bearer $token")
                if (response.isSuccessful) {
                    val data = response.body()
                    _mahasiswaInfo.value = data?.data?.info
                    _setoranList.value = data?.data?.setoran?.detail ?: emptyList()
                    _ringkasanList.value = data?.data?.setoran?.ringkasan ?: emptyList()
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
