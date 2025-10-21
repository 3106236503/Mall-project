/*
package com.macro.mall.dto;

public class Result<T> {
    private int code;
    private String message;
    private T data;

    public static <T> Result<T> success(T data, String message) {
        Result<T> result = new Result<>();
        result.code = 200;
        result.message = message;
        result.data = data;
        return result;
    }

    public static <T> Result<T> failed(String message) {
        Result<T> result = new Result<>();
        result.code = 500;
        result.message = message;
        return result;
    }

    // getter and setter
}
*/
