package com.example.uas.ui.theme

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uas.DataModels

@Composable
fun SetoranScreen(setoranList: List<DataModels.SetoranItem>) {
    LazyColumn {
        items(setoranList) { item ->
            SetoranItemCard(setoran = item)
        }
    }
}

@Composable
fun SetoranItemCard(setoran: DataModels.SetoranItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = setoran.nama,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Kategori: ${setoran.label}",
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (setoran.sudahSetor) "✅ Sudah Setor" else "❌ Belum Setor",
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )
        }
    }
}
