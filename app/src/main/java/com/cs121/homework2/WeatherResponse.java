package com.cs121.homework2;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class WeatherResponse {

    @SerializedName("response")
    @Expose
    public Response response;
}
