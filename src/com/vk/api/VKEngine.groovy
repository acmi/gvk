package com.vk.api

import groovy.transform.CompileStatic
import groovy.transform.ToString
import org.thavam.util.concurrent.BlockingHashMap
import org.thavam.util.concurrent.BlockingMap

import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

/**
 * @author acmi
 */
@CompileStatic
class VKEngine {
    private static final String PROTOCOL = 'https'
    private static final String HOST = 'api.vk.com'
    private static final int REQUESTS_PER_SECOND = 3
    private static final long WAIT_TIME_MILLIS = (TimeUnit.SECONDS.toMillis(1) / REQUESTS_PER_SECOND).longValue()

    protected final ScheduledExecutorService executor = new ScheduledThreadPoolExecutor(2)
    protected final List<VKWorker> workers = []
    protected final Queue<RequestWrapper> requests = new PriorityQueue<>()
    protected final BlockingMap<RequestWrapper, Object> responses = new BlockingHashMap<>()

    VKEngine() {
        executor.scheduleAtFixedRate(new CleanTask(), 0, 1, TimeUnit.MINUTES)
    }

    private class VKWorker implements Runnable {
        final String token
        final int userId

        private ScheduledFuture task

        VKWorker(String token, int userId) {
            this.token = token
            this.userId = userId
        }

        void start() {
            task = executor.scheduleAtFixedRate(this, 0, WAIT_TIME_MILLIS, TimeUnit.MILLISECONDS)
        }

        void stop() {
            task.cancel(false)
        }

        @Override
        void run() {
            RequestWrapper request = requests.poll()
            if (request != null && System.currentTimeMillis() < request.expireTime) {
                Object response
                Map params = [access_token: token]
                params.putAll(request.request.params)
                String method = "/method/${request.request.method}${request.request.xml ? '.xml' : ''}?${params.collect { k, v -> "$k=$v" }.join('&')}"

                URL url = new URL(PROTOCOL, HOST, method)
                StringBuilder sb = new StringBuilder()
                try {
                    url.openConnection().inputStream.withReader('UTF-8') { Reader reader ->
                        reader.readLines().each { String line ->
                            sb.append(line)
                        }
                    }
                    response = sb.toString()
                } catch (Exception e) {
                    response = e
                }
                responses.put(request, response)
            }
        }
    }

    void addWorker(AccessToken accessToken) {
        removeWorkerById(accessToken.userId)

        VKWorker worker = new VKWorker(accessToken.token, accessToken.userId)
        worker.start()
        workers << worker
    }

    void removeWorkerById(int userId) {
        for (Iterator<VKWorker> it = workers.iterator(); it.hasNext();) {
            VKWorker worker = it.next()
            if (worker.userId == userId) {
                it.remove()
                worker.stop()
            }
        }
    }

    void removeWorkerByToken(String token) {
        for (Iterator<VKWorker> it = workers.iterator(); it.hasNext();) {
            VKWorker worker = it.next()
            if (worker.token == token) {
                it.remove()
                worker.stop()
            }
        }
    }

    private class CleanTask implements Runnable {
        @Override
        void run() {
            for (Iterator<Map.Entry<RequestWrapper, Object>> it = responses.entrySet().iterator(); it.hasNext();) {
                RequestWrapper request = it.next().key
                if (System.currentTimeMillis() > request.expireTime)
                    it.remove()
            }
        }
    }

    void shutdown() {
        executor.shutdown()
    }

    @ToString(includePackage = false, includeNames = true)
    private static class RequestWrapper implements Comparable<RequestWrapper>{
        final VKRequest request
        final long creationTime
        final long expireTime

        RequestWrapper(VKRequest request, long timeout, TimeUnit unit){
            this.request = request
            this.creationTime = System.currentTimeMillis()
            long millis = unit.toMillis(timeout)
            if (Long.MAX_VALUE - millis - this.creationTime > 0)
                this.expireTime = this.creationTime+millis
            else
                this.expireTime = Long.MAX_VALUE
        }

        @Override
        int compareTo(RequestWrapper o) {
            Long.compare(expireTime, o.expireTime)
        }
    }

    String executeQuery(VKRequest query, long timeout=Long.MAX_VALUE, TimeUnit unit=TimeUnit.MILLISECONDS) throws IOException, TimeoutException {
        if (timeout <= 0)
            throw new TimeoutException()

        RequestWrapper request = new RequestWrapper(query, timeout, unit)
        requests.add(request)

        Object response = responses.take(request, timeout, unit)
        if (response == null)
            throw new TimeoutException()

        if (response instanceof Exception) {
            if (response instanceof IOException)
                throw response
            else
                throw new RuntimeException(response)
        }
        response.toString()
    }
}
