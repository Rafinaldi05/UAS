package com.example.uas

import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // Endpoint untuk login dan mendapatkan access token
    @FormUrlEncoded
    @POST("realms/dev/protocol/openid-connect/token")
    suspend fun login(
        @Field("client_id") clientId: String = "setoran-mobile-dev",
        @Field("client_secret") clientSecret: String = "aqJp3xnXKudgC7RMOshEQP7ZoVKWzoSl",
        @Field("grant_type") grantType: String = "password",
        @Field("username") username: String,
        @Field("password") password: String
    ): DataModels.LoginResponse

    // Endpoint untuk mengambil data info mahasiswa dan (opsional) setoran
    @GET("setoran-dev/v1/mahasiswa/setoran-saya")
    suspend fun getMahasiswa(
        @Header("Authorization") token: String
    ): Response<DataModels.SetoranResponse>
}
