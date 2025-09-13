package com.old.silence.auth.center.exception;

import com.old.silence.core.exception.PlatformException;

/**
 * @author moryzang
 */
public class CaptchaExpiredException extends PlatformException {
    public CaptchaExpiredException(String message) {
        super(400, 1, message);
    }


}