package com.exp.narang.api.model.network;

import okhttp3.MultipartBody;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

@Component
public interface RetrofitService {
    String UPLOAD_URL = "1/upload";

    @Multipart
    @POST(UPLOAD_URL)
    Call<ImgbbResponse> postUploadImage(@Part("key") String key, @Part("image") MultipartFile image);
}
