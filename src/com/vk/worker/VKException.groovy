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

    static final int USER_AUTHORIZATION_FAILED = 5
    static final int TOO_MANY_REQUESTS_PER_SECOND = 6
    static final int CAPTCHA_NEEDED = 14
    static final int USER_WAS_DELETED_OR_BANNED = 18
    static final int ONE_OF_THE_PARAMETERS_SPECIFIED_WAS_MISSING_OR_INVALID = 100
    static final int INVALID_USER_ID = 113
    static final int ACCESS_TO_POST_COMMENTS_DENIED = 212
}
