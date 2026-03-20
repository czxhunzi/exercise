package com.example.springboot.common;

public enum ResultCodeEnum {

    SUCCESS("200", "成功"),
    ERROR("-1","失败"),

    USER_EXIST_ERROR("5001", "用户名已存在");
    public String code;
    public String msg;

    ResultCodeEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
