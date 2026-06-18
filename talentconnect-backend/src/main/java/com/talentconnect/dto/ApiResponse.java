package com.talentconnect.dto;
import java.time.Instant;
public record ApiResponse<T>(T data,String timestamp,int status){
    public static <T> ApiResponse<T> ok(T data){return new ApiResponse<>(data,Instant.now().toString(),200);}
    public static <T> ApiResponse<T> created(T data){return new ApiResponse<>(data,Instant.now().toString(),201);}
}
