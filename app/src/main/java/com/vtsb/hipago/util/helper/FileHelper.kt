package com.vtsb.hipago.util.helper

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import org.apache.commons.io.FileUtils
import java.io.File
import java.io.IOException
import java.io.OutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FileHelper @Inject constructor(

) {

    fun openExternalOutputStream(context: Context, name: String, mimeType: String, new: Boolean = false): OutputStream? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            openExternalOutputStreamQ(context, name, mimeType, new)
        } else {
            openExternalOutputStreamLegacy(name, new)
        }
    }

    private fun openExternalOutputStreamLegacy(name: String, new: Boolean): OutputStream? {
        val externalFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val file = File(externalFolder, name)
        if (new && file.exists()) return null
        return FileUtils.openOutputStream(file)
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private fun openExternalOutputStreamQ(context: Context, name: String, mimeType: String, new: Boolean): OutputStream? {
        val values = ContentValues()
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, name)
        values.put(MediaStore.MediaColumns.MIME_TYPE, mimeType)

        val resolver = context.contentResolver
        val targetFileUri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values) ?: throw IOException("Target URI could not be formed")
        if (targetFileUri.path == null || (new && File(targetFileUri.path!!).exists())) return null
        return resolver.openOutputStream(targetFileUri)
    }



}