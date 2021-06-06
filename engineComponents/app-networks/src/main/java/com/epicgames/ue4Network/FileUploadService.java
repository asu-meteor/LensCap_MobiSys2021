package com.epicgames.ue4Network;
import android.util.Log;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface FileUploadService {
    @Multipart
    @POST("image")
    Call<ResponseBody> upload(
            @Part("image") RequestBody description,
            @Part MultipartBody.Part file
    );
}
