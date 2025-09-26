package com.example.appapibd.data

data class EstudiantesRepository(
    private val api: ApiService = RetrofitClient.api
) {
    suspend fun list(): List<EstudianteResponse> =
        api.getEstudiantes()

    suspend fun add(nombre: String, edad: Int, correo: String, carnet: String): Int {
        val res = api.addEstudiante(
            EstudiantePayload(
                nombre = nombre,
                edad = edad,
                correo = correo,
                carnet = carnet
            )
        )
        return res.data ?: -1
    }

    suspend fun update(
        id: Int,
        nombre: String,
        edad: Int,
        correo: String,
        carnet: String
    ): EstudianteResponse? {
        val res = api.updateEstudiante(
            id,
            EstudiantePayload(
                nombre = nombre,
                edad = edad,
                correo = correo,
                carnet = carnet
            )
        )
        return res.data
    }

    suspend fun delete(id: Int) {
        api.deleteEstudiante(id)
    }
}




