package com.example.taskmaster.util

import android.content.Context
import android.net.Uri
import java.io.File

fun copiarImagemParaStorage(context: Context, uri: Uri): String {
    val arquivo = File(
        context.filesDir,
        "img_${System.currentTimeMillis()}_${(Math.random() * 10000).toInt()}.jpg"
    )
    context.contentResolver.openInputStream(uri)?.use { input ->
        arquivo.outputStream().use { output ->
            input.copyTo(output)
        }
    }
    return arquivo.absolutePath
}
