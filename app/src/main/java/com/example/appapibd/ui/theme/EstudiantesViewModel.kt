package com.example.appapibd.ui.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appapibd.data.EstudiantePayload
import com.example.appapibd.data.EstudianteResponse
import com.example.appapibd.data.EstudiantesRepository
import com.example.appapibd.data.RetrofitClient.api
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class UiState(
    val lista: List<EstudianteResponse> = emptyList(),
    val nombre: String = "",
    val edad: String = "",
    val correo: String = "",
    val carnet: String = "",
    val seleccionadoId: Int? = null,
    val loading: Boolean = false,
    val error: String? = null,
    val info: String? = null
)

class EstudiantesViewModel(
    private val repo: EstudiantesRepository = EstudiantesRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState

    init { refresh() }

    fun setNombre(value: String) { _uiState.value = _uiState.value.copy(nombre = value) }
    fun setEdad(value: String)   { _uiState.value = _uiState.value.copy(edad = value) }
    fun setCorreo(value: String) { _uiState.value = _uiState.value.copy(correo = value) }
    fun setCarnet(value: String) { _uiState.value = _uiState.value.copy(carnet = value) }

    fun seleccionar(est: EstudianteResponse) {
        _uiState.value = _uiState.value.copy(
            seleccionadoId = est.id,
            nombre = est.nombre,
            edad = est.edad.toString(),
            correo = est.correo ?: "",
            carnet = est.carnet ?: "",
            info = "Seleccionado ID ${est.id}"
        )
    }

    fun limpiarSeleccion() {
        _uiState.value = _uiState.value.copy(
            seleccionadoId = null,
            nombre = "",
            edad = "",
            correo = "",
            carnet = "",
            info = "Limpio"
        )
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(loading = true, error = null, info = null)
            try {
                val data = repo.list()
                _uiState.value = _uiState.value.copy(lista = data, loading = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(loading = false, error = e.message)
            }
        }
    }

    fun agregar() {
        val nombre = _uiState.value.nombre.trim()
        val edad = _uiState.value.edad.toIntOrNull()
        val correo = _uiState.value.correo.trim()
        val carnet = _uiState.value.carnet.trim()

        if (nombre.isEmpty() || edad == null || correo.isEmpty() || carnet.isEmpty()) {
            _uiState.value = _uiState.value.copy(error = "Todos los campos son requeridos")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(loading = true, error = null, info = null)
            try {
                repo.add(nombre, edad, correo, carnet)
                refresh()
                _uiState.value = _uiState.value.copy(
                    info = "Insertado",
                    nombre = "",
                    edad = "",
                    correo = "",
                    carnet = ""
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(loading = false, error = e.message)
            }
        }
    }

    fun actualizar() {
        val id = _uiState.value.seleccionadoId ?: run {
            _uiState.value = _uiState.value.copy(error = "Selecciona un estudiante")
            return
        }
        val nombre = _uiState.value.nombre.trim()
        val edad = _uiState.value.edad.toIntOrNull()
        val correo = _uiState.value.correo.trim()
        val carnet = _uiState.value.carnet.trim()

        if (nombre.isEmpty() || edad == null || correo.isEmpty() || carnet.isEmpty()) {
            _uiState.value = _uiState.value.copy(error = "Todos los campos son requeridos")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(loading = true, error = null, info = null)
            try {
                repo.update(id, nombre, edad, correo, carnet)
                refresh()
                _uiState.value = _uiState.value.copy(info = "Actualizado")
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(loading = false, error = e.message)
            }
        }
    }

    fun eliminar(id: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(loading = true, error = null, info = null)
            try {
                repo.delete(id)
                refresh()
                _uiState.value = _uiState.value.copy(info = "Eliminado")
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(loading = false, error = e.message)
            }
        }
    }
}