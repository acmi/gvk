package com.vk.api

import groovy.util.logging.Log
import groovy.util.slurpersupport.GPathResult

import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import java.util.logging.Level

/**
 * @author acmi
 */
@Log
class VKEngineGPath extends VKEngine {
    private XmlSlurper xmlSlurper = new XmlSlurper()

    GPathResult executeQuery(String method, Map params, long timeout = Long.MAX_VALUE, TimeUnit unit = TimeUnit.SECONDS) throws IOException, TimeoutException, VKException {
        long startTime = System.currentTimeMillis()
        String xml = executeQuery(new VKRequest(method, params, true), timeout, unit)
        if (xml == null)
            return null

        GPathResult result = xmlSlurper.parseText(xml)
        if (result.name() == 'error') {
            int errorCode = result.error_code.text().toInteger()
            String errorMsg = result.error_msg.text()
            Map requestParams = [:]
            result.request_params.param.each {
                requestParams.put(it.key.text(), it.value.text())
            }
            VKException exception = new VKException(errorCode, errorMsg, requestParams)
            switch (exception.code) {
                case VKException.USER_AUTHORIZATION_FAILED:
                    removeWorkerByToken(exception.requestParams['access_token'])
                case VKException.TOO_MANY_REQUESTS_PER_SECOND:
                    log.log(Level.WARNING, exception.toString())
                    return executeQuery(exception.requestParams['method'], exception.requestParams.findAll { k, v ->
                        !['oauth', 'method', 'access_token'].contains(k)
                    }, unit.toMillis(timeout) - System.currentTimeMillis() + startTime, TimeUnit.MILLISECONDS)
                default: throw exception
            }
        }
        result
    }
}
