package com.vk.worker.impl

import com.vk.worker.VKAnonymousWorker
import com.vk.worker.VKCaptchaNeededException
import com.vk.worker.VKException
import com.vk.worker.VKRequest
import groovy.transform.CompileStatic
import groovy.transform.PackageScope
import org.w3c.dom.Document
import org.w3c.dom.Element

import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.ThreadFactory
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import java.util.logging.Level
import java.util.logging.Logger

/**
 * @author acmi
 */
@PackageScope
@CompileStatic
abstract class AbstractVKWorker implements VKAnonymousWorker {
    private static final Logger log = Logger.getLogger(AbstractVKWorker.class.getName())

    private static final int REQUESTS_PER_SECOND = 3
    protected static final long WAIT_TIME_MILLIS = (TimeUnit.SECONDS.toMillis(1) / REQUESTS_PER_SECOND).longValue()

    private final ScheduledExecutorService executors
    private final Map<String, ScheduledFuture> workers = [:]

    protected final Queue<Request> requests = new LinkedList<Request>()

    final Set<String> cacheableMethods = new HashSet<String>()
    private final Map<VKRequest, Element> cache = [:]

    private final DocumentBuilder xmlBuilder

    AbstractVKWorker(int corePoolSize) {
        executors = Executors.newScheduledThreadPool(corePoolSize, new VKWorkerThreadFactory())

        DocumentBuilderFactory f = DocumentBuilderFactory.newInstance()
        f.setValidating(false)
        xmlBuilder = f.newDocumentBuilder()
    }

    protected void addWorker(String token) {
        if (token == null)
            throw new NullPointerException()

        Task task = new Task(token, requests)
        ScheduledFuture futureTask = executors.scheduleAtFixedRate(task, 0, WAIT_TIME_MILLIS, TimeUnit.MILLISECONDS)
        workers.put(token, futureTask)?.cancel(false)
    }

    protected void removeWorker(String token) {
        def worker = workers.remove(token)
        if (worker != null) {
            worker.cancel(false)
            log.log(Level.INFO, "worker removed: $token")
        }
    }

    int getRequestsCount() {
        requests.size()
    }

    protected String _executeQuery(VKRequest request) throws IOException, InterruptedException {
        if (workers.isEmpty())
            log.log(Level.WARNING, 'Workers empty')

        ObjectHolder<Object> holder = new ObjectHolder<Object>()
        requests.offer(new Request(request, holder))

        Object response = holder.get()
        if (response instanceof Throwable) {
            if (response instanceof IOException) throw (IOException) response
            else throw new RuntimeException((Throwable) response)
        }

        return response.toString()
    }

    @Override
    Element executeQuery(VKRequest request) throws IOException, VKException {
        Element result = cache[request]
        if (result != null)
            return result

        String xmlString = _executeQuery(request)
        Document document = xmlBuilder.parse(new ByteArrayInputStream(xmlString.getBytes()))
        result = document.getDocumentElement()
        if (result.getTagName().equals("error")) {
            int errorCode
            String errorMsg
            Map<String, String> requestParams = new LinkedHashMap<String, String>()

            errorCode = Integer.parseInt(result.getElementsByTagName("error_code").item(0).getTextContent())
            errorMsg = result.getElementsByTagName("error_msg").item(0).getTextContent()
            org.w3c.dom.NodeList nl = result.getElementsByTagName("param")
            for (int i = 0; i < nl.getLength(); i++) {
                org.w3c.dom.NodeList ps = nl.item(i).getChildNodes()
                requestParams.put(ps.item(1).getTextContent(), ps.item(3).getTextContent())
            }

            switch (errorCode) {
                case VKException.USER_AUTHORIZATION_FAILED:
                    def token = requestParams['access_token']
                    log.log(Level.INFO, "$errorMsg: $token")
                    removeWorker(token)
                case VKException.TOO_MANY_REQUESTS_PER_SECOND:
                    executeQuery(request)
                    break
                case VKException.CAPTCHA_NEEDED:
                    String captchaSid = result.getElementsByTagName("captcha_sid").item(0).getTextContent()
                    String captchaImg = result.getElementsByTagName("captcha_img").item(0).getTextContent()
                    throw new VKCaptchaNeededException(errorMsg, requestParams, captchaSid, captchaImg)
                default:
                    throw new VKException(errorCode, errorMsg, requestParams)
            }
        }
        if (cacheableMethods.contains(request.method))
            cache[request] = result

        return result
    }

    private static class VKWorkerThreadFactory implements ThreadFactory {
        private static final AtomicInteger poolNumber = new AtomicInteger(1)
        private final AtomicInteger threadNumber = new AtomicInteger(1)
        private final String namePrefix

        VKWorkerThreadFactory() {
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
