package com.vk.worker.impl

import com.vk.worker.VKException
import com.vk.worker.VKRequest
import groovy.transform.CompileStatic
import org.w3c.dom.Element

import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.ThreadFactory
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import java.util.logging.Level
import java.util.logging.Logger

/**
 * @author acmi
 */
@CompileStatic
final class VKWorkerGroup extends AbstractVKWorker {
    private static final Logger log = Logger.getLogger(VKWorkerGroup.class.getName())

    private ScheduledExecutorService executors = new ScheduledThreadPoolExecutor(1, new VKWorkerThreadFactory())

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
    private static class VKWorkerThreadFactory implements ThreadFactory{
        private static final AtomicInteger poolNumber = new AtomicInteger(1)
        private final AtomicInteger threadNumber = new AtomicInteger(1)
        private final String namePrefix

        VKWorkerThreadFactory(){
            namePrefix = "VKWorkerPool-${poolNumber.getAndIncrement()}-thread-"
        }

        @Override
        Thread newThread(Runnable r) {
            Thread thread = new Thread(null, r, namePrefix + threadNumber.getAndIncrement())
            thread.daemon = true
            thread.priority = Thread.NORM_PRIORITY
            thread
        }
    }
}
