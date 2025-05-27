package com.example.uas.ui.theme

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uas.DataModels
import androidx.compose.ui.graphics.Color

@Composable
fun HomeScreen(
    nama: String,
    nim: String,
    setoranList: List<DataModels.SetoranItem>,
    ringkasanList: List<DataModels.RingkasanItem>
) {
    val total = setoranList.size
    val sudah = setoranList.count { it.sudahSetor }
    val progress = if (total > 0) sudah.toFloat() / total else 0f

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Dashboard",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

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
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Progress Setoran", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                    Text(
                        "$sudah dari $total sudah disetor",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                CircularProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.size(58.dp),
                    strokeWidth = 4.dp,
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = Color.LightGray

                )
            }
        }

        Text(
            "Progres",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        if (ringkasanList.isEmpty()) {
            Text(
                "Belum ada data progres",
                modifier = Modifier.align(Alignment.CenterHorizontally),
                style = MaterialTheme.typography.bodyLarge
            )

        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ringkasanList.forEach { ringkasan ->
                    val progressValue = if (ringkasan.total_wajib_setor > 0)
                        ringkasan.total_sudah_setor.toFloat() / ringkasan.total_wajib_setor
                    else 0f
                    val progressPercent = (progressValue * 100).toInt()

                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = if (progressValue == 0f) Color.Gray else MaterialTheme.colorScheme.primary,
                                modifier = Modifier
                                    .padding(end = 12.dp)
                                    .size(20.dp)
                            )

                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        ringkasan.label,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                    Text("~ $progressPercent%", fontSize = 14.sp)
                                }

                                Text(
                                    "Wajib: ${ringkasan.total_wajib_setor}, Sudah: ${ringkasan.total_sudah_setor}, Belum: ${ringkasan.total_belum_setor}",
                                    fontSize = 14.sp
                                )

                                LinearProgressIndicator(
                                    progress = { progressValue },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 8.dp)
                                        .height(8.dp),
                                    color = if (progressValue == 0f) Color.Gray else MaterialTheme.colorScheme.primary,
                                    trackColor = Color.LightGray
                                )

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 4.dp),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    Text(
                                        "${ringkasan.total_sudah_setor}/${ringkasan.total_wajib_setor}",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}