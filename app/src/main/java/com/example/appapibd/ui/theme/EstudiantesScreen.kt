package com.example.appapibd.ui.theme

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.appapibd.storage.FirebaseUploader
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EstudiantesScreen(vm: EstudiantesViewModel = viewModel()) {
    val state by vm.uiState.collectAsState()
    val scope = rememberCoroutineScope()
    val ctx = LocalContext.current

    var pickedUri by remember { mutableStateOf<Uri?>(null) }
    val pickFile = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        pickedUri = uri
        if (uri != null) {
            vm.notificarArchivoSeleccionado() // <-- “Archivo seleccionado”
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("CRUD Estudiantes (FastAPI + Compose)") })
        }
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            // --- INICIO DEL FORMULARIO ---
            Text(
                text = if (state.seleccionadoId == null) "Nuevo estudiante" else "Editar ID ${state.seleccionadoId}",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = state.nombre,
                onValueChange = vm::setNombre,
                label = { Text("Nombre") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = state.edad,
                onValueChange = vm::setEdad,
                label = { Text("Edad") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = state.carnet,
                onValueChange = vm::setCarnet,
                label = { Text("Carnet") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = state.correo,
                onValueChange = vm::setCorreo,
                label = { Text("Correo") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth()
            )

            // --- INICIO SUBIDA A FIREBASE ---
            Spacer(Modifier.height(8.dp))
            Text("Archivo (opcional):", style = MaterialTheme.typography.labelLarge)
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(onClick = { pickFile.launch("*/*") }, enabled = !state.loading) {
                    Text(if (pickedUri == null) "Seleccionar" else "Cambiar")
                }

                // Progress solo si estás subiendo EN agregar/actualizar y aún no hay picUrl
                if (state.loading && state.picUrl == null) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                }

                // Si ya se seleccionó pero no se ha subido aún
                if (pickedUri != null && state.picUrl == null && !state.loading) {
                    AssistChip(onClick = {}, label = { Text("Archivo seleccionado") })
                }

                // Si ya cuentas con una picUrl (p.ej., en edición o después de subir en actualizar)
                if (state.picUrl != null) {
                    AssistChip(onClick = {}, label = { Text("Archivo listo") },
                        leadingIcon = {
                            Icon(
                                Icons.Filled.Check,
                                contentDescription = "Listo",
                                modifier = Modifier.size(AssistChipDefaults.IconSize)
                            )
                        }
                    )
                }
            }

            if (state.picUrl != null) {
                Spacer(Modifier.height(6.dp))
                Text(text = "pic_url: ${state.picUrl}", style = MaterialTheme.typography.bodySmall)
            }

            // --- INICIO BOTONES ---
            Spacer(Modifier.height(16.dp))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        onClick = { vm.agregar(pickedUri, ctx.contentResolver) }, // <-- pasa uri + resolver
                        enabled = !state.loading,
                        modifier = Modifier.weight(1f)
                    ) { Text("Agregar") }

                    Button(
                        onClick = { vm.actualizar(pickedUri, ctx.contentResolver) }, // recomendado
                        enabled = state.seleccionadoId != null && !state.loading,
                        modifier = Modifier.weight(1f)
                    ) { Text("Actualizar") }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = {
                            vm.limpiarSeleccion()
                            pickedUri = null
                        },
                        enabled = !state.loading,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Clear, contentDescription = null)
                        Spacer(Modifier.width(4.dp))
                        Text("Limpiar")
                    }
                    OutlinedButton(
                        onClick = vm::refresh,
                        enabled = !state.loading,
                        modifier = Modifier.weight(1f)
                    ) { Text("Refrescar") }
                }
            }

            // --- INICIO INDICADORES DE ESTADO (ERROR, INFO, LOADING) ---
            if (state.loading) {
                LinearProgressIndicator(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                )
            }
            state.error?.let {
                Spacer(Modifier.height(8.dp))
                AssistChip(onClick = { }, label = { Text("Error: $it") })
            }
            state.info?.let {
                Spacer(Modifier.height(8.dp))
                AssistChip(onClick = { }, label = { Text(it) })
            }
            // --- FIN INDICADORES DE ESTADO ---

            Spacer(Modifier.height(16.dp))
            Text("Listado", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))

            // --- INICIO DE LA LISTA (LAZYCOLUMN) ---
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(state.lista, key = { it.id }) { estudiante ->
                    ElevatedCard(Modifier.fillMaxWidth()) {
                        // Columna principal dentro de la tarjeta
                        Column(modifier = Modifier.padding(12.dp)) {
                            // Fila para la info y la imagen
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Columna para la información textual
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Nombre: ${estudiante.nombre}", style = MaterialTheme.typography.titleSmall)
                                    Text("Edad: ${estudiante.edad}", style = MaterialTheme.typography.bodySmall)
                                    Text("Carnet: ${estudiante.carnet ?: "N/A"}", style = MaterialTheme.typography.bodySmall)
                                    Text("Correo: ${estudiante.correo ?: "N/A"}", style = MaterialTheme.typography.bodySmall)
                                    Text("ID: ${estudiante.id}", style = MaterialTheme.typography.bodySmall)
                                }

                                // Mostrar imagen si la URL existe
                                estudiante.pic_url?.let { url ->
                                    Spacer(Modifier.width(8.dp))
                                    AsyncImage(
                                        model = url,
                                        contentDescription = "Imagen de ${estudiante.nombre}",
                                        modifier = Modifier.size(100.dp)
                                    )
                                }
                            }

                            Spacer(Modifier.height(8.dp))

                            // Fila para los botones de acción (Editar, Eliminar)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                IconButton(onClick = { vm.seleccionar(estudiante) }, enabled = !state.loading) {
                                    Icon(Icons.Default.Edit, contentDescription = "Editar")
                                }
                                IconButton(onClick = { vm.eliminar(estudiante.id) }, enabled = !state.loading) {
                                    Icon(Icons.Default.Delete, contentDescription = "Eliminar")
                                }
                            }
                        }
                    }
                }

                // Espacio al final de la lista para que el último elemento no quede pegado abajo
                item {
                    Spacer(Modifier.height(100.dp))
                }
            }
        }
    }
}
