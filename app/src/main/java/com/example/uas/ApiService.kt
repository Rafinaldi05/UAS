package com.example.uas

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @FormUrlEncoded
    @POST("realms/dev/protocol/openid-connect/token")
    suspend fun login(
        @Field("client_id") clientId: String = "setoran-mobile-dev",
        @Field("client_secret") clientSecret: String = "aqJp3xnXKudgC7RMOshEQP7ZoVKWzoSl",
        @Field("grant_type") grantType: String = "password",
        @Field("username") username: String,
        @Field("password") password: String
    ): DataModels.LoginResponse

    @GET("setoran-dev/v1/mahasiswa/setoran-saya")
    suspend fun getMahasiswa(
        @Header("Authorization") token: String
    ): Response<DataModels.SetoranResponse>

    @FormUrlEncoded
    @POST("realms/dev/protocol/openid-connect/logout")
    suspend fun logout(
        @Field("client_id") clientId: String = "setoran-mobile-dev",
        @Field("client_secret") clientSecret: String = "aqJp3xnXKudgC7RMOshEQP7ZoVKWzoSl",
        @Field("id_token") idToken: String
    ): Response<Unit>

    @GET("setoran-dev/v1/mahasiswa/kartu-murojaah-saya")
    suspend fun downloadKartuMurojaah(@Header("Authorization") token: String): Response<ResponseBody>

}
