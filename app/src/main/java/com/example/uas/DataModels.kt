package com.example.uas

import com.google.gson.annotations.SerializedName

class DataModels {
    data class LoginResponse(
        @SerializedName("access_token") val accessToken: String,
        @SerializedName("expires_in") val expiresIn: Int,
        @SerializedName("token_type") val tokenType: String
    )
    data class SetoranResponse(val data: MahasiswaData)
    data class MahasiswaData(val mahasiswa: Mahasiswa)
    data class Mahasiswa(val nim: String, val nama: String)

}