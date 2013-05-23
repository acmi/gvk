package com.vk.worker

import groovy.transform.CompileStatic

/**
 * @author acmi
 */
@CompileStatic
class VKException extends Exception {
    final int code
    final Map<String, String> requestParams = [:]

    VKException(int code, String message, Map requestParams) {
        super(message)
        this.code = code
        this.requestParams.putAll(requestParams)
    }

    public static final int USER_AUTHORIZATION_FAILED = 5
    public static final int TOO_MANY_REQUESTS_PER_SECOND = 6
    public static final int CAPTCHA_NEEDED = 14
}
