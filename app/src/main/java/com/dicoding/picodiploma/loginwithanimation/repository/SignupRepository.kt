package com.dicoding.picodiploma.loginwithanimation.repository

import com.dicoding.picodiploma.loginwithanimation.data.api.ApiConfig
import com.dicoding.picodiploma.loginwithanimation.data.api.ApiService
import com.dicoding.picodiploma.loginwithanimation.data.api.SignupResponse

class SignupRepository(token: String) {
    private val apiService: ApiService = ApiConfig.getApiService(token)

    suspend fun register(name: String, email: String, password: String): SignupResponse {
        return apiService.register(name, email, password)
    }
}
