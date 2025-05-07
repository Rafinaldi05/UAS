package com.example.uas

import com.google.gson.annotations.SerializedName

class DataModels {

    data class LoginResponse(
        @SerializedName("access_token") val accessToken: String,
        @SerializedName("expires_in") val expiresIn: Int,
        @SerializedName("token_type") val tokenType: String
    )

    data class SetoranResponse(
        val response: Boolean,
        val message: String,
        val data: SetoranData
    )

    data class SetoranData(
        val info: MahasiswaInfo,
        val setoran: SetoranItem // ✅ Ditambahkan agar tidak null saat parsing
    )

    data class MahasiswaInfo(
        val nama: String,
        val nim: String,
        val email: String,
        val angkatan: String,
        val semester: Int,
        @SerializedName("dosen_pa") val dosenPa: DosenPa
    )

    data class DosenPa(
        val nip: String,
        val nama: String,
        val email: String
    )

    data class SetoranItem(
        @SerializedName("nama_surah") val namaSurah: String,
        @SerializedName("tanggal_setoran") val tanggalSetoran: String?,
        @SerializedName("persyaratan") val persyaratan: String?,
        @SerializedName("dosen") val dosen: String?,
        @SerializedName("status") val status: String?
    )
}
