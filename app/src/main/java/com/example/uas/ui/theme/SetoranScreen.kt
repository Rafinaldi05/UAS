package com.example.uas.ui.theme

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.example.uas.ApiService
import com.example.uas.DataModels
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import android.content.ContentValues
import android.os.Build
import android.provider.MediaStore
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.Color


@Composable
fun SetoranScreen(
    setoranList: List<DataModels.SetoranItem>,
    mahasiswaInfo: DataModels.MahasiswaInfo
) {
    val context = LocalContext.current
    val searchQuery = remember { mutableStateOf("") }

    val filteredList = setoranList.filter {
        it.nama.contains(searchQuery.value, ignoreCase = true)
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        Text(
            text = "Riwayat Murojaah",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Tombol Download
        Button(
            onClick = { downloadKartuMurojaahPdf(context) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("📄 Download Kartu Murojaah")
        }

        // Search Field
        OutlinedTextField(
            value = searchQuery.value,
            onValueChange = { searchQuery.value = it },
            label = { Text("Cari Nama Surah") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            trailingIcon = {
                Icon(Icons.Default.Search, contentDescription = null)
            }
        )

        if (filteredList.isEmpty()) {
            Text(
                "Tidak ditemukan surah dengan kata '${searchQuery.value}'",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredList) { item ->
                    SetoranItemCard(setoran = item)
                }
            }
        }
    }
}

@Composable
fun SetoranItemCard(setoran: DataModels.SetoranItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = setoran.nama,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text("Kategori: ${setoran.label}", fontSize = 14.sp)
            Text(
                text = if (setoran.sudahSetor) "✅ Sudah Setor" else "❌ Belum Setor",
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = if (setoran.sudahSetor) Color(0xFF2E7D32) else Color(0xFFC62828)
            )
        }
    }
}

fun downloadKartuMurojaahPdf(context: Context) {
    val retrofit = Retrofit.Builder()
        .baseUrl("https://api.tif.uin-suska.ac.id/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService = retrofit.create(ApiService::class.java)
    val token = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        .getString("auth_token", null)

    if (token.isNullOrBlank()) {
        Toast.makeText(context, "Token tidak ditemukan", Toast.LENGTH_SHORT).show()
        return
    }

    CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = apiService.downloadKartuMurojaah("Bearer $token")
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    val fileName = "kartu_murojaah.pdf"

                    val contentValues = ContentValues().apply {
                        put(MediaStore.Downloads.DISPLAY_NAME, fileName)
                        put(MediaStore.Downloads.MIME_TYPE, "application/pdf")
                        put(MediaStore.Downloads.IS_PENDING, 1)
                    }

                    val resolver = context.contentResolver
                    val collection = MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
                    val uri = resolver.insert(collection, contentValues)

                    if (uri != null) {
                        resolver.openOutputStream(uri)?.use { outputStream ->
                            body.byteStream().use { inputStream ->
                                inputStream.copyTo(outputStream)
                            }
                        }

                        contentValues.clear()
                        contentValues.put(MediaStore.Downloads.IS_PENDING, 0)
                        resolver.update(uri, contentValues, null, null)

                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "PDF berhasil disimpan ke Downloads", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "Gagal menyimpan ke Downloads", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "File kosong", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Gagal mengunduh PDF", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
