package com.example.appapibd.data

import retrofit2.http.*

interface ApiService {

    @GET("estudiantes/")
    suspend fun getEstudiantes(): List<EstudianteResponse>

    @POST("estudiantes/")
    suspend fun addEstudiante(@Body payload: EstudiantePayload): ApiResult<Int>

    @PUT("estudiantes/{id}")
    suspend fun updateEstudiante(
        @Path("id") id: Int,
        @Body payload: EstudiantePayload
    ): ApiResult<EstudianteResponse>

    @DELETE("estudiantes/{id}")
    suspend fun deleteEstudiante(@Path("id") id: Int): ApiResult<Unit>
}