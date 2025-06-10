package com.example.uas.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uas.DataModels

@Composable
fun ProfilScreen(
    mahasiswaInfo: DataModels.MahasiswaInfo,
    onLogout: () -> Unit
) {
    val dosenPa = mahasiswaInfo.dosenPa

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1f))

            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(96.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = mahasiswaInfo.nama.take(1).uppercase(),
                            fontSize = 33.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    InfoRow("Nama", mahasiswaInfo.nama)
                    InfoRow("NIM", mahasiswaInfo.nim)
                    InfoRow("Email", mahasiswaInfo.email, fontSize = 15.sp)
                    InfoRow("Angkatan", mahasiswaInfo.angkatan)
                    InfoRow("Semester", mahasiswaInfo.semester.toString())
                    InfoRow("Dosen PA", dosenPa.nama)
                    InfoRow("NIP Dosen PA", dosenPa.nip)
                    InfoRow("Email Dosen PA", dosenPa.email)
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onLogout,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF37474F),
                    contentColor = Color.White
                )
            ) {
                Text("Logout", fontSize = 16.sp, fontWeight = FontWeight.Medium)
            }
        }
    }
}



@Composable
fun InfoRow(label: String, value: String, fontSize: TextUnit = 16.sp) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            fontSize = fontSize,
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
            color = MaterialTheme.colorScheme.primary
        )
    }
}

