package com.vk.worker.impl

import groovy.transform.PackageScope

import java.util.logging.Level
import java.util.logging.Logger

/**
 * @author acmi
 */
@PackageScope
class Task implements Runnable {
    private static Logger log = Logger.getLogger(Task.class.name)

    private static final String PROTOCOL = "https"
    private static final String HOST = "api.vk.com"

    private final String token
    private final Queue<Request> requests

    Task(String token, Queue<Request> requests) {
        this.token = token
        this.requests = requests
    }

    @Override
    void run() {
        Request request = requests.poll()
        if (request == null)
            return

        Object response
        try {
            StringBuilder method = new StringBuilder("/method/")
            method.append(request.request.getMethod())
            method.append(".xml?")
            method.append("access_token=")
            method.append(token)
            if (request.request.getParams() != null) {
                Set<Map.Entry> params = request.request.getParams().entrySet()
                for (Map.Entry param : params) {
                    method.append("&")
                    method.append(param.getKey())
                    method.append("=")
//                    method.append(param.getValue())
                    method.append(URLEncoder.encode(param.getValue().toString(), 'UTF-8'))
                }
            }

            def urlStr = method.toString() //URLEncoder.encode(method.toString(), 'UTF-8')

            log.log(Level.FINE, urlStr)

            URL url = new URL(PROTOCOL, HOST, urlStr)
            response = url.openConnection().inputStream.getText('UTF-8')

            log.log(Level.FINE, response.toString())
        } catch (Exception e) {
            response = e
        }

        request.response.set(response)
    }
}
