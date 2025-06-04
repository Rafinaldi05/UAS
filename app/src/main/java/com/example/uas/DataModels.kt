package com.example.uas

import com.google.gson.annotations.SerializedName

object NavRoutes {
    const val HOME = "home"
    const val SETORAN = "setoran"
    const val PROFIL = "profil"
}

class DataModels {

    data class LoginResponse(
        @SerializedName("access_token") val accessToken: String,
        @SerializedName("refresh_token") val refreshToken: String,
        @SerializedName("id_token") val idToken: String,
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
        val log: List<LogItem>,
        @SerializedName("info_dasar") val infoDasar: InfoDasar,
        val ringkasan: List<RingkasanItem>,
        val detail: List<SetoranItem>
    )

    data class LogItem(
        val id: Int,
        val keterangan: String,
        val aksi: String,
        val ip: String,
        @SerializedName("user_agent") val userAgent: String,
        val timestamp: String,
        val nim: String,
        @SerializedName("dosen_yang_mengesahkan") val dosenYangMengesahkan: DosenPa
    )

    data class InfoDasar(
        @SerializedName("total_wajib_setor") val totalWajibSetor: Int,
        @SerializedName("total_sudah_setor") val totalSudahSetor: Int,
        @SerializedName("total_belum_setor") val totalBelumSetor: Int,
        @SerializedName("persentase_progres_setor") val persentaseProgresSetor: Double,
        @SerializedName("tgl_terakhir_setor") val tglTerakhirSetor: String?,
        @SerializedName("terakhir_setor") val terakhirSetor: String
    )

    data class RingkasanItem(
        val label: String,
        @SerializedName("total_wajib_setor") val totalWajibSetor: Int,
        @SerializedName("total_sudah_setor") val totalSudahSetor: Int,
        @SerializedName("total_belum_setor") val totalBelumSetor: Int,
        @SerializedName("persentase_progres_setor") val persentaseProgresSetor: Double
    )

    data class SetoranItem(
        val id: String,
        val nama: String,
        @SerializedName("external_id") val externalId: String,
        @SerializedName("nama_arab") val namaArab: String,
        val label: String,
        @SerializedName("sudah_setor") val sudahSetor: Boolean,
        @SerializedName("info_setoran") val infoSetoran: InfoSetoran?
    )

    data class InfoSetoran(
        val id: String,
        @SerializedName("tgl_setoran") val tglSetoran: String,
        @SerializedName("tgl_validasi") val tglValidasi: String,
        @SerializedName("dosen_yang_mengesahkan") val dosenYangMengesahkan: DosenPa
    )
}