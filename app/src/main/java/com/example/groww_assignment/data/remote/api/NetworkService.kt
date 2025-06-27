package com.example.groww_assignment.data.remote.api
import com.example.groww_assignment.domain.util.Result
import com.example.groww_assignment.domain.util.NetworkException
import retrofit2.Response
import java.io.IOException
import java.net.HttpURLConnection

class NetworkService {
    suspend fun <T> safeApiCall(
        apiCall: suspend () -> Response<T>
    ): Result<T> {
        return try {
            val response = apiCall()
            when {
                response.isSuccessful -> {
                    response.body()?.let { data ->
                        Result.Success(data)
                    } ?: Result.Error(NetworkException.ServerError)
                }
                response.code() == HttpURLConnection.HTTP_FORBIDDEN ||
                        response.code() == 429 -> {
                    Result.Error(NetworkException.ApiLimitExceeded)
                }
                response.code() >= 500 -> {
                    Result.Error(NetworkException.ServerError)
                }
                else -> {
                    Result.Error(
                        NetworkException.ApiError(
                            response.code(),
                            response.message()
                        )
                    )
                }
            }
        } catch (e: IOException) {
            Result.Error(NetworkException.NetworkError)
        } catch (e: Exception) {
            Result.Error(NetworkException.UnknownError)
        }
    }
}