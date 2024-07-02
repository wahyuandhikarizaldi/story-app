package com.dicoding.picodiploma.loginwithanimation.view.addStory

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.dicoding.picodiploma.loginwithanimation.data.api.ApiConfig
import com.dicoding.picodiploma.loginwithanimation.data.api.FileUploadResponse
import com.dicoding.picodiploma.loginwithanimation.utils.Utils
import com.dicoding.picodiploma.loginwithanimation.utils.reduceFileImage
import com.google.gson.Gson
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.IOException

class AddStoryViewModel(application: Application) : AndroidViewModel(application) {

    private val _uploadResult = MutableLiveData<String>()
    val uploadResult: LiveData<String> = _uploadResult

    fun uploadImage(token: String, description: String, imageUri: Uri, latitude: Double, longitude: Double) {
        val utils = Utils()
        val imageFile = utils.uriToFile(imageUri, getApplication())
        val reducedImageFile = imageFile.reduceFileImage()

        val requestBody = description.toRequestBody("text/plain".toMediaType())
        val latitudePart = latitude.toString().toRequestBody("text/plain".toMediaType())
        val longitudePart = longitude.toString().toRequestBody("text/plain".toMediaType())
        val requestImageFile = reducedImageFile.asRequestBody("image/jpeg".toMediaType())
        val multipartBody = MultipartBody.Part.createFormData(
            "photo",
            reducedImageFile.name,
            requestImageFile
        )

        viewModelScope.launch {
            try {
                val apiService = ApiConfig.getApiService(token)
                val response = apiService.uploadImage(multipartBody, requestBody, latitudePart, longitudePart)
                _uploadResult.value = response.message
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                val errorResponse = Gson().fromJson(errorBody, FileUploadResponse::class.java)
                _uploadResult.value = errorResponse.message
            } catch (e: IOException) {
                _uploadResult.value = "Network error: ${e.message}"
            }
        }
    }
}
