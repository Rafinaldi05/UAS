package com.example.uas

class DataModels {
    data class LoginResponse(val access_token: String)

    data class SetoranResponse(val data: MahasiswaData)
    data class MahasiswaData(val mahasiswa: Mahasiswa)
    data class Mahasiswa(val nim: String, val nama: String)

}