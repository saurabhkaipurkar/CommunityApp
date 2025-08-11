package com.example.communityapp.apiservices

import com.example.communityapp.models.ApiResponse
import com.example.communityapp.models.DistrictResponse
import com.example.communityapp.models.GetPostResponse
import com.example.communityapp.models.LoginResponse
import com.example.communityapp.models.PostComments
import com.example.communityapp.models.PostLikes
import com.example.communityapp.models.PostResponse
import com.example.communityapp.models.StateResponse
import com.example.communityapp.models.TalukaResponse
import com.example.communityapp.models.UserProfileResponse
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {

    @POST("index.php/api/AccountsController/users")
    @FormUrlEncoded
    suspend fun userInfo(
        @FieldMap userInfo: Map<String, String>
    ): ApiResponse

    @POST("index.php/api/Logincontroller/login")
    @FormUrlEncoded
    suspend fun loginAuth(
        @FieldMap userinfo: Map<String, String>
    ): LoginResponse

    @PUT("index.php/api/AccountsController/update_users")
    @Headers("Content-Type: application/json")
    suspend fun updateUserInfo(
        @Body requestBody: RequestBody
    ): UserProfileResponse

    @GET("index.php/api/AccountsController/get_state")
    suspend fun state() : StateResponse

    @GET("index.php/api/AccountsController/get_district/{state_id}")
    suspend fun district(
        @Path("state_id") stateId: Int
    ) : DistrictResponse

    @GET("index.php/api/AccountsController/get_taluka/{district_id}/{state_id}")
    suspend fun taluka(
        @Path("district_id") districtId: Int,
        @Path("state_id") stateId: Int
    ) : TalukaResponse

    @POST("index.php/api/AccountsController/new_content")
    @FormUrlEncoded
    suspend fun createPost(
        @FieldMap postInfo: Map<String, String>
    ): PostResponse

    @GET("index.php/api/AccountsController/get_new_content")
    suspend fun getPosts(): GetPostResponse

    @POST("index.php/api/AccountsController/likes")
    @FormUrlEncoded
    suspend fun likes(
        @FieldMap postInfo: Map<String, String>
    ): PostLikes

    @POST("index.php/api/AccountsController/comments")
    @FormUrlEncoded
    suspend fun comments(
        @FieldMap postInfo: Map<String, String>
    ): PostComments

}