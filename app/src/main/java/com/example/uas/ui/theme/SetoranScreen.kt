package com.example.uas.ui.theme

import android.content.Context
import android.graphics.Bitmap
import android.os.Environment
import android.graphics.pdf.PdfDocument
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.Canvas
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
import com.example.uas.DataModels
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Rect
import com.example.uas.R
import java.text.SimpleDateFormat
import java.util.*



@Composable
fun SetoranScreen(
    setoranList: List<DataModels.SetoranItem>,
    nama: String,
    nim: String,
    dosenPa: String
) {
    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxSize()) {

        Button(
            onClick = {
                generatePdf(context, nama, nim, dosenPa, setoranList)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text("Download PDF")
        }

        LazyColumn {
            items(setoranList) { item ->
                SetoranItemCard(setoran = item)
            }
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

fun generatePdf(
    context: Context,
    nama: String,
    nim: String,
    dosenPa: String,
    setoranList: List<DataModels.SetoranItem>
) {
    val document = PdfDocument()
    val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
    val page = document.startPage(pageInfo)
    val canvas = page.canvas

    val paint = Paint().apply {
        textSize = 9f
        isAntiAlias = true
        color = Color.BLACK
    }

    val boldPaint = Paint(paint).apply {
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }

    val headerPaint = Paint().apply {
        color = Color.LTGRAY
        style = Paint.Style.FILL
    }

    var y = 40f

    val logo = try {
        BitmapFactory.decodeResource(context.resources, R.drawable.uin)
    } catch (e: Exception) {
        null
    }

    logo?.let {
        val scaledLogo = Bitmap.createScaledBitmap(it, 60, 60, false)
        canvas.drawBitmap(scaledLogo, 20f, y, null)
    }

    canvas.drawText("KARTU MUROJA'AH JUZ 30", 100f, y + 10f, boldPaint)
    canvas.drawText("PROGRAM STUDI TEKNIK INFORMATIKA", 100f, y + 30f, paint)
    canvas.drawText("FAKULTAS SAINS DAN TEKNOLOGI", 100f, y + 45f, paint)
    canvas.drawText("UNIVERSITAS ISLAM NEGERI SULTAN SYARIF KASIM RIAU", 100f, y + 60f, paint)

    y += 90f
    canvas.drawLine(20f, y, 575f, y, paint)
    y += 20f

    canvas.drawText("Nama:", 20f, y, boldPaint)
    canvas.drawText(nama, 80f, y, paint)
    y += 15f
    canvas.drawText("NIM:", 20f, y, boldPaint)
    canvas.drawText(nim, 80f, y, paint)
    y += 15f
    canvas.drawText("Pembimbing Akademik:", 20f, y, boldPaint)
    canvas.drawText(dosenPa, 160f, y, paint)
    y += 25f

    val xStart = 20f
    val colWidths = listOf(25f, 180f, 90f, 130f, 120f)
    val colX = colWidths.runningFold(xStart) { acc, w -> acc + w }
    val headers = listOf("No.", "Surah (Ayat)", "Tanggal Muroja'ah", "Persyaratan Muroja'ah", "Dosen yang Mengesahkan")

    val headerHeight = 18f
    canvas.drawRect(xStart, y, colX.last(), y + headerHeight, headerPaint)
    headers.forEachIndexed { i, h ->
        canvas.drawText(h, colX[i] + 2f, y + 13f, boldPaint)
    }

    y += headerHeight
    canvas.drawLine(xStart, y, colX.last(), y, paint)

    val rowHeight = 16f
    val maxRows = ((842 - y - 160) / rowHeight).toInt().coerceAtMost(setoranList.size)

    for (i in 0 until maxRows) {
        val rowY = y + i * rowHeight
        if (rowY + rowHeight > 842 - 160) break

        val item = setoranList[i]
        val nomor = (i + 1).toString()

        val surah = item.nama.ifBlank { "(belum diisi)" }
        val ayat = "(${item.dariAyat} - ${item.sampaiAyat})"
        val judul = "$surah $ayat"

        val tanggal = try {
            val infoMap = item.infoSetoran as? Map<*, *>
            infoMap?.get("tgl_setoran") as? String ?: "(belum diisi)"
        } catch (e: Exception) {
            "(belum diisi)"
        }

        val kategori = when (item.label.uppercase(Locale.ROOT)) {
            "KP", "SEMKP" -> "Kerja Praktek"
            "DAFTAR_TA" -> "Tugas Akhir"
            "SEMPRO" -> "Seminar Proposal"
            "SIDANG_TA" -> "Sidang Tugas Akhir"
            else -> item.label
        }

        val pengesah = if (item.sudahSetor) "Sudah Diperiksa" else "-"

        val cols = listOf(nomor, judul, tanggal, kategori, pengesah)

        cols.forEachIndexed { j, text ->
            canvas.drawText(text, colX[j] + 2f, rowY + 12f, paint)
        }
    }

    for (x in colX) {
        canvas.drawLine(x, y - 10f, x, y + maxRows * rowHeight, paint)
    }

    for (i in 0..maxRows) {
        val lineY = y + i * rowHeight
        canvas.drawLine(xStart, lineY, colX.last(), lineY, paint)
    }

    val dateFormat = SimpleDateFormat("d MMMM yyyy", Locale("id"))
    val tanggalSekarang = dateFormat.format(Date())
    val signY = y + maxRows * rowHeight + 30f

    canvas.drawText("Pekanbaru, $tanggalSekarang", 370f, signY, paint)
    canvas.drawText("Pembimbing Akademik,", 370f, signY + 15f, paint)
    canvas.drawText(dosenPa, 370f, signY + 60f, boldPaint)
    canvas.drawText("NIP: 197408072009011007", 370f, signY + 75f, paint)

    document.finishPage(page)

    val fileName = "kartu_murojaah_${nama}_$nim.pdf"
    val contentValues = android.content.ContentValues().apply {
        put(android.provider.MediaStore.Downloads.DISPLAY_NAME, fileName)
        put(android.provider.MediaStore.Downloads.MIME_TYPE, "application/pdf")
        put(android.provider.MediaStore.Downloads.IS_PENDING, 1)
    }

    val resolver = context.contentResolver
    val collection = android.provider.MediaStore.Downloads.getContentUri(android.provider.MediaStore.VOLUME_EXTERNAL_PRIMARY)
    val itemUri = resolver.insert(collection, contentValues)

    try {
        itemUri?.let {
            resolver.openOutputStream(it)?.use { outputStream ->
                document.writeTo(outputStream)
            }

            contentValues.clear()
            contentValues.put(android.provider.MediaStore.Downloads.IS_PENDING, 0)
            resolver.update(it, contentValues, null, null)

            Toast.makeText(context, "PDF berhasil disimpan ke folder Downloads", Toast.LENGTH_SHORT).show()
        } ?: run {
            Toast.makeText(context, "Gagal menyimpan PDF", Toast.LENGTH_SHORT).show()
        }
    } catch (e: IOException) {
        e.printStackTrace()
        Toast.makeText(context, "Terjadi kesalahan saat menyimpan PDF", Toast.LENGTH_SHORT).show()
    } finally {
        document.close()
    }
}
