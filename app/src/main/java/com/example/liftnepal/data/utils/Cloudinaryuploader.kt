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

    // ⚠️ REPLACE WITH YOUR VALUES FROM CLOUDINARY DASHBOARD
    private const val CLOUD_NAME = "dmifam6tj"        // e.g. "dxxxxxxxx"
    private const val UPLOAD_PRESET = "liftnepal_licenses"  // preset you created

    suspend fun uploadImage(context: Context, imageUri: Uri): Result<String> {
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
                        "license.jpg",
                        imageBytes.toRequestBody("image/*".toMediaTypeOrNull())
                    )
                    .addFormDataPart("upload_preset", UPLOAD_PRESET)
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