package com.example.appapibd.data

// Lo que tu API devuelve al listar
data class EstudianteResponse(
    val id: Int,
    val nombre: String,
    val edad: Int,
    val correo: String,
    val carnet: String,
    val pic_url: String?
)

// Lo que envías al crear/actualizar
data class EstudiantePayload(
    val nombre: String,
    val edad: Int,
    val correo: String,
    val carnet: String,
    val pic_url: String?
)

// Envoltorio que usa tu API para POST/PUT/DELETE
// Ejemplos:
//  POST -> { "mensaje": "...", "data": 3 }
//  PUT  -> { "mensaje": "...", "data": {id, nombre, edad, correo, carnet} }
//  DEL  -> { "mensaje": "..." }
data class ApiResult<T>(
    val mensaje: String,
    val data: T?
)


