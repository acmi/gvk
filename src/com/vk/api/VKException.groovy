package com.vk.api

/**
 * @author acmi
 */
class VKException extends Exception{
    int code
    Map<String, String> requestParams = [:]

    VKException(int code, String message, Map requestParams){
        super(message)
        setCode(code)
        setRequestParams(requestParams)
    }

    private void setCode(int code){
        this.code = code
    }

    private void setRequestParams(Map requestParams){
        this.requestParams.putAll(requestParams)
    }

    public static final int USER_AUTHORIZATION_FAILED = 5
    public static final int TOO_MANY_REQUESTS_PER_SECOND = 6
}
