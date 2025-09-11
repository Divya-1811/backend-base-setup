package com.practice.base_setup.util;

import com.practice.base_setup.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class CommonUtil {
    private CommonUtil(){
    }

    public static ApiResponse getApiResponse(int status, String message){
        return new ApiResponse(status,message,null);
    }

    public static <T> ResponseEntity<ApiResponse> getOkResponse(String message, T data) {
        return ResponseEntity.ok(getApiResponse(HttpStatus.OK.value(), message,data));
    }

    public static <T> ApiResponse getApiResponse(int status, String message,T data){
        return new ApiResponse(status,message,data);
    }
}
