package com.lotus.shortlink.admin.common.enums;

import com.lotus.shortlink.admin.common.convention.errorcode.IErrorCode;

public enum UserCodeEnum implements IErrorCode {
    USER_NULL("B000200", "用户不存在"),
    USER_NAME_EXIST("B000201", "用户名已存在"),
    USER_EXIST("B000202", "用户已存在"),
    USER_SAVE_FAILED("B000203", "用户新增失败"),
    USER_LOGIN_EXIST("B000204", "用户已登录"),

    USER_TOKEN_INVALID("B000205", "用户登录已失效，请重新登录");

    private final String code;

    private final String message;

    UserCodeEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String code() {
        return code;
    }

    @Override
    public String message() {
        return message;
    }
}
