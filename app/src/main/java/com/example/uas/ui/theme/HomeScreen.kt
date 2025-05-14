package com.example.uas.ui.theme

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uas.DataModels
@Composable
fun HomeScreen(
    nama: String,
    nim: String,
    setoranList: List<DataModels.SetoranItem>
) {
    val total = setoranList.size
    val sudah = setoranList.count { it.sudahSetor }
    val progress = if (total > 0) sudah.toFloat() / total else 0f

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()), // agar semua konten bisa discroll
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Bar
        Text(
            text = "Dashboard",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        // Greeting Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            shape = MaterialTheme.shapes.large
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("👋 Selamat datang,", fontSize = 16.sp)
                Text(text = nama, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Text("NIM: $nim", fontSize = 16.sp)
            }
        }

        // Progress Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            shape = MaterialTheme.shapes.large
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Progress Setoran", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                    Text("$sudah dari $total sudah disetor", style = MaterialTheme.typography.bodyMedium)
                }
                CircularProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.size(60.dp),
                    strokeWidth = 6.dp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        // Riwayat Terbaru
        Text(
            "Riwayat Terbaru",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        if (setoranList.isEmpty()) {
            Text(
                "Belum ada setoran",
                modifier = Modifier.align(Alignment.CenterHorizontally),
                style = MaterialTheme.typography.bodyLarge
            )
        } else {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                setoranList.take(37).forEach { setoran ->
                    SetoranItemCard(setoran=setoran)
                }
            }
        }

    }
}
