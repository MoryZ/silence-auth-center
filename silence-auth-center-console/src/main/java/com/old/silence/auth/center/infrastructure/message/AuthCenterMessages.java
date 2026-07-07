package com.old.silence.auth.center.infrastructure.message;

import org.springframework.http.HttpStatus;
import com.old.silence.core.context.ErrorCodedEnumMessageSourceResolvable;

public enum AuthCenterMessages implements ErrorCodedEnumMessageSourceResolvable {

    USER_NOT_EXIST(HttpStatus.BAD_REQUEST, 51),
    PASSWORD_NOT_CORRECT(HttpStatus.BAD_REQUEST, 52),
    PASSWORD_COMPLEXITY_NOT_MATCHED(HttpStatus.BAD_REQUEST, 53), // "密码必须包含字母、数字和特殊字符"
    MENU_NOT_EXIST(HttpStatus.BAD_REQUEST, 54),
    SUB_MENU_EXIST(HttpStatus.BAD_REQUEST, 55),
    SUB_MENUS_ALREADY_EXIST(HttpStatus.BAD_REQUEST, 56),
    ROLE_NOT_EXIST(HttpStatus.BAD_REQUEST, 57),
    ROLE_CODE_ALREADY_EXIST(HttpStatus.BAD_REQUEST, 58),
    USERNAME_ALREADY_EXIST(HttpStatus.BAD_REQUEST, 59), // "用户名已存在"

    ;

    private final int httpStatusCode;

    private final int errorCode;

    AuthCenterMessages(HttpStatus httpStatus, int errorCode) {
        this.httpStatusCode = httpStatus.value();
        this.errorCode = errorCode;
    }

    public int getHttpStatusCode() {
        return httpStatusCode;
    }

    public int getErrorCode() {
        return errorCode;
    }
}
