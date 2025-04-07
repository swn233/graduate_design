package com.example.backend.entity;

import com.alibaba.fastjson2.*;

public record RestBean<T>(int code, T data, String message) {
    public static <T> RestBean<T> success(T data) {
        return new RestBean<>(200, data, "Success");
    }

    public static <T> RestBean<T> failure(int code,String message){
        return new RestBean<>(code,null,message);
    }


    public static <T> RestBean<T> success() {
        return success(null);
    }

    public String asJsonString() {

        return JSONObject.toJSONString(this, JSONWriter.Feature.WriteNulls);
    }


}
