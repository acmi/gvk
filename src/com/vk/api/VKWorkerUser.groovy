package com.vk.api

import groovy.transform.Canonical
import groovy.transform.CompileStatic
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.NodeList
import org.xml.sax.SAXException

import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.parsers.ParserConfigurationException
import java.util.concurrent.BlockingQueue
import java.util.concurrent.CountDownLatch
import java.util.concurrent.LinkedBlockingDeque
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.logging.Level
import java.util.logging.Logger

/**
 * @author acmi
 */
@CompileStatic
final class VKWorkerUser implements VKWorker {
    private static final Logger log = Logger.getLogger(VKWorkerUser.class.getName())

    private static final String PROTOCOL = "https"
    private static final String HOST = "api.vk.com"
    private static final int VKRequestS_PER_SECOND = 3
    private static final long WAIT_TIME_MILLIS = (TimeUnit.SECONDS.toMillis(1) / VKRequestS_PER_SECOND).longValue()

    final int userId
    final String token

    private Timer executionTimer
    private final BlockingQueue<Request> requests = new LinkedBlockingDeque<Request>()

    private final DocumentBuilder xmlBuilder

    VKWorkerUser(int userId, String token) {
        this.userId = userId
        this.token = token

        try {
            DocumentBuilderFactory f = DocumentBuilderFactory.newInstance()
            f.setValidating(false)
            xmlBuilder = f.newDocumentBuilder()
        } catch (ParserConfigurationException pce) {
            throw new RuntimeException(pce)
        }

    }

    synchronized void start() {
        executionTimer = new Timer()
        executionTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
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
                            method.append(param.getValue())
                        }
                    }

                    log.log(Level.FINE, method.toString())

                    URL url = new URL(PROTOCOL, HOST, method.toString())
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection()

                    response = connection.inputStream.getText('UTF-8')

                    log.log(Level.FINE, response.toString())
                } catch (Exception e) {
                    response = e
                }

                request.response.set(response)
            }

        }, 0, WAIT_TIME_MILLIS)
    }

    synchronized void stop() {
        if (executionTimer != null) {
            executionTimer.cancel()
            executionTimer = null
        }
    }

    private String _executeQuery(VKRequest request) throws IOException, InterruptedException {
        if (executionTimer == null)
            log.log(Level.WARNING, "Executor not started")

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
    Element executeQuery(VKRequest request) throws IOException, VKException, InterruptedException, SAXException {
        String xmlString = _executeQuery(request)
        Document document = xmlBuilder.parse(new ByteArrayInputStream(xmlString.getBytes()))
        Element result = document.getDocumentElement()
        if (result.getTagName().equals("error")) {
            int errorCode
            String errorMsg
            Map<String, String> requestParams = new LinkedHashMap<String, String>()

            errorCode = Integer.parseInt(result.getElementsByTagName("error_code").item(0).getTextContent())
            errorMsg = result.getElementsByTagName("error_msg").item(0).getTextContent()
            NodeList nl = result.getElementsByTagName("param")
            for (int i = 0; i < nl.getLength(); i++) {
                NodeList ps = nl.item(i).getChildNodes()
                requestParams.put(ps.item(1).getTextContent(), ps.item(3).getTextContent())
            }

            switch(errorCode){
                case VKException.TOO_MANY_REQUESTS_PER_SECOND:
                    return executeQuery(request);
                case VKException.CAPTCHA_NEEDED:
                    String captchaSid = result.getElementsByTagName("captcha_sid").item(0).getTextContent()
                    String captchaImg = result.getElementsByTagName("captcha_img").item(0).getTextContent()
                    throw new VKCaptchaNeededException(errorMsg, requestParams, captchaSid, captchaImg)
                default:
                    throw new VKException(errorCode, errorMsg, requestParams)
            }
        }

        return result
    }

    private static class ObjectHolder<V> {
        private final CountDownLatch latch = new CountDownLatch(1)
        private final AtomicBoolean isSet = new AtomicBoolean()
        private V slot

        V get() throws InterruptedException {
            latch.await()
            slot
        }

        void set(V value) {
            if (!isSet.get() && isSet.compareAndSet(false, true)) {
                slot = value
                latch.countDown()
            }
        }
    }

    @Canonical
    private static class Request {
        VKRequest request
        ObjectHolder response
    }
}
