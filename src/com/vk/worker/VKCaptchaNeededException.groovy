package com.vk.worker

import groovy.transform.CompileStatic

/**
 * @author acmi
 */
@CompileStatic
final class VKCaptchaNeededException extends VKException {
    final String captchaSid
    final String captchaImg

    VKCaptchaNeededException(String message, Map<String, String> requestParams, String captchaSid, String captchaImg) {
        super(CAPTCHA_NEEDED, message, requestParams)

        this.captchaSid = captchaSid
        this.captchaImg = captchaImg
    }
}
