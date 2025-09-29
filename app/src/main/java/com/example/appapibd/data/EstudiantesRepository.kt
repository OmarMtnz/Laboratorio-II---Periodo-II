package com.example.appapibd.data

data class EstudiantesRepository(
    private val api: ApiService = RetrofitClient.api
) {
    suspend fun list(): List<EstudianteResponse> =
        api.getEstudiantes()

    suspend fun add(nombre: String, edad: Int, correo: String, carnet: String, picUrl: String?):
            Int {
        val res = api.addEstudiante(
            EstudiantePayload(
                nombre = nombre,
                edad = edad,
                correo = correo,
                carnet = carnet,
                pic_url = picUrl,
            )
        )
        return res.data ?: -1
    }

    // Crear CON imagen
    suspend fun addWithPic(
        nombre: String, edad: Int, correo: String, carnet: String, picUrl: String?
    ): Int {
        val res = api.addEstudiante(
            EstudiantePayload(nombre, edad, correo, carnet, pic_url = picUrl)
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
                carnet = carnet,
                pic_url = null
            )
        )
        return res.data
    }

    // Update CON imagen
    suspend fun updateWithPic(
        id: Int, nombre: String, edad: Int, correo: String, carnet: String, picUrl: String?
    ): EstudianteResponse? {
        val res = api.updateEstudiante(
            id, EstudiantePayload(nombre, edad, correo, carnet, pic_url = picUrl)
        )
        return res.data
    }

    suspend fun delete(id: Int) {
        api.deleteEstudiante(id)
    }


}




