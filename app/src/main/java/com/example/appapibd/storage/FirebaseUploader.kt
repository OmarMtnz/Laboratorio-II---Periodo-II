package com.example.appapibd.storage

import android.content.ContentResolver
import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.util.UUID

object FirebaseUploader {

    private val storage by lazy { FirebaseStorage.getInstance() }

    /**
     * Sube un archivo (imagen/video/pdf/etc) a Firebase Storage y retorna el downloadURL.
     * @param uri: Uri del archivo local (ACTION_OPEN_DOCUMENT / GET_CONTENT)
     * @param resolver: ContentResolver para abrir el stream
     * @param pathPrefix: carpeta lógica, p.ej. "estudiantes"
     */
    suspend fun uploadAndGetUrl(
        uri: Uri,
        resolver: ContentResolver,
        pathPrefix: String = "estudiantes"
    ): String {
        val fileName = "${UUID.randomUUID()}"
        val ref = storage.reference.child("$pathPrefix/$fileName")

        resolver.openInputStream(uri).use { input ->
            requireNotNull(input) { "No se pudo abrir el InputStream del archivo" }
            val uploadTask = ref.putStream(input)
            uploadTask.await()
        }
        return ref.downloadUrl.await().toString()
    }
}


