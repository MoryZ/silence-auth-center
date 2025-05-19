package com.old.silence.auth.center.infrastructure.message;

import org.springframework.http.HttpStatus;
import com.old.silence.core.context.ErrorCodedEnumMessageSourceResolvable;

public enum AuthCenterMessages implements ErrorCodedEnumMessageSourceResolvable {


    MENU_NOT_EXIST(HttpStatus.BAD_REQUEST, 30001),
    SUB_MENUS_ALREADY_EXIST(HttpStatus.BAD_REQUEST, 30002),
    ROLE_NOT_EXIST(HttpStatus.BAD_REQUEST, 30003),
    ROLE_ALREADY_EXIST(HttpStatus.BAD_REQUEST, 30004),

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
