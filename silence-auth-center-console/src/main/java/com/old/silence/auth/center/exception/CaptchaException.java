package com.old.silence.auth.center.exception;

import com.old.silence.core.exception.PlatformException;

/**
 * @author moryzang
 */
public class CaptchaException extends PlatformException {
    public CaptchaException(String message) {
        super(400, 2, message);
    }
}
