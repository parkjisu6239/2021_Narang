package com.exp.narang.api.model.network;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;

@Getter
public class ImgbbResponseData {
    private String url;
    @SerializedName("delete_url")
    private String deleteUrl;
}
