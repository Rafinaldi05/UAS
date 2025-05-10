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
        val setoran: SetoranContent
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

    data class SetoranContent(
        val log: List<Any>,
        @SerializedName("info_dasar") val infoDasar: InfoDasar,
        val ringkasan: List<RingkasanItem>,
        val detail: List<SetoranItem>
    )

    data class InfoDasar(
        val total_wajib_setor: Int,
        val total_sudah_setor: Int,
        val total_belum_setor: Int,
        val persentase_progres_setor: Int,
        val tgl_terakhir_setor: String?,
        val terakhir_setor: String
    )

    data class RingkasanItem(
        val label: String,
        val total_wajib_setor: Int,
        val total_sudah_setor: Int,
        val total_belum_setor: Int,
        val persentase_progres_setor: Int
    )

    data class SetoranItem(
        val id: String,
        val nama: String,
        val label: String,
        @SerializedName("sudah_setor") val sudahSetor: Boolean,
        @SerializedName("info_setoran") val infoSetoran: Any?
    )
}
