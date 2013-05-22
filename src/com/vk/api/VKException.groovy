package com.vk.api

import groovy.transform.CompileStatic

/**
 * @author acmi
 */
@CompileStatic
class VKException extends Exception{
    final Integer code
    final Map<String, String> requestParams = [:]

    VKException(Integer code, String message, Map requestParams){
        super(message)
        this.code = code
        this.requestParams.putAll(requestParams)
    }

    public static final int USER_AUTHORIZATION_FAILED = 5
    public static final int TOO_MANY_REQUESTS_PER_SECOND = 6
    public static final int CAPTCHA_NEEDED = 14
}
