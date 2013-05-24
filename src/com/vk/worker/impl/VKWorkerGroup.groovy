package com.vk.worker.impl

import com.vk.worker.VKException
import com.vk.worker.VKRequest
import groovy.transform.CompileStatic
import org.w3c.dom.Element

import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import java.util.logging.Level
import java.util.logging.Logger

/**
 * @author acmi
 */
@CompileStatic
final class VKWorkerGroup extends AbstractVKWorker {
    private static final Logger log = Logger.getLogger(VKWorkerGroup.class.getName())

    private ScheduledExecutorService executors = Executors.newScheduledThreadPool(2)

    private final Map<String, ScheduledFuture> workers = [:]

    VKWorkerGroup(String... tokens) {
        tokens.each { String token ->
            addWorker(token)
        }
    }

    void addWorker(String token) {
        Task task = new Task(token, requests)
        ScheduledFuture futureTask = executors.scheduleAtFixedRate(task, 0, WAIT_TIME_MILLIS, TimeUnit.MILLISECONDS)
        workers.put(token, futureTask)?.cancel(false)
    }

    void removeWorker(String token) {
        def worker = workers.remove(token)
        if (worker != null){
            worker.cancel(false)
            log.log(Level.INFO, "worker removed: $token")
        }
    }

    @Override
    void stop() {
        executors.shutdownNow()
    }

    @Override
    protected String _executeQuery(VKRequest request) throws IOException, InterruptedException {
        if (workers.isEmpty())
            log.log(Level.WARNING, 'Workers empty')

        return super._executeQuery(request)
    }

    @Override
    Element executeQuery(VKRequest request) throws IOException, VKException {
        try {
            return super.executeQuery(request)
        } catch (VKException vke) {
            switch (vke.code) {
                case VKException.USER_AUTHORIZATION_FAILED:
                    def token = vke.requestParams['access_token']
                    log.log(Level.INFO, "$vke.message: $token")
                    removeWorker(token)
                case VKException.TOO_MANY_REQUESTS_PER_SECOND:
                    executeQuery(request)
                    break;
                default:
                    throw vke
            }
        }
    }
}
