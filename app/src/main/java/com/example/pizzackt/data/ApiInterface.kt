package com.example.pizzackt.data

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiInterface {
    @GET("pizza")
    suspend fun getAllPizza(): Response<ResponseListPizza>

    @POST("pizza")
    suspend fun savePizza(@Body d: NewRequestDataPizza): Response<ResponsePizza>

    @PUT("pizza/{id}")
    suspend fun updatePizza(@Path("id") id: String, @Body d: DataPizza): Response<ResponsePizza>

    @DELETE("pizza/{id}")
    suspend fun deletePizza(@Path("id") id: String): Response<ResponsePizza>
}