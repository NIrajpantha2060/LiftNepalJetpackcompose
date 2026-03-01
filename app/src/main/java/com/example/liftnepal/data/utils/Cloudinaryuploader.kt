package com.example.liftnepal.data.utils

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

object CloudinaryUploader {

    // ⚠️ REPLACE WITH YOUR CLOUD NAME FROM CLOUDINARY DASHBOARD
    private const val CLOUD_NAME = "dmifam6tj"

    // Presets — one per feature
    const val PRESET_LICENSES = "liftnepal_licenses"
    const val PRESET_PROFILES = "liftnepal_profiles"
    const val PRESET_RIDES    = "liftnepal_rides"     // for future use

    const val PRESET_ISSUES   = "liftnepal_issues"

    suspend fun uploadImage(context: Context, imageUri: Uri, preset: String): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val inputStream = context.contentResolver.openInputStream(imageUri)
                    ?: return@withContext Result.Error("Could not read image")

                val imageBytes = inputStream.readBytes()
                inputStream.close()

                val requestBody = MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart(
                        "file",
                        "upload.jpg",
                        imageBytes.toRequestBody("image/*".toMediaTypeOrNull())
                    )
                    .addFormDataPart("upload_preset", preset)
                    .build()

                val request = Request.Builder()
                    .url("https://api.cloudinary.com/v1_1/$CLOUD_NAME/image/upload")
                    .post(requestBody)
                    .build()

                val client = OkHttpClient()
                val response = client.newCall(request).execute()
                val responseBody = response.body?.string()

                if (response.isSuccessful && responseBody != null) {
                    val json = JSONObject(responseBody)
                    Result.Success(json.getString("secure_url"))
                } else {
                    Result.Error("Upload failed: ${response.code}")
                }
            } catch (e: Exception) {
                Result.Error(e.message ?: "Upload failed")
            }
        }
    }
}