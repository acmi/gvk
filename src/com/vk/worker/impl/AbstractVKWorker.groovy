package com.vk.worker.impl

import com.vk.worker.VKAnonymousWorker
import com.vk.worker.VKCaptchaNeededException
import com.vk.worker.VKException
import com.vk.worker.VKRequest
import groovy.transform.PackageScope
import org.w3c.dom.Document
import org.w3c.dom.Element

import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import java.util.concurrent.TimeUnit

/**
 * @author acmi
 */
@PackageScope
abstract class AbstractVKWorker implements VKAnonymousWorker {
    private static final int REQUESTS_PER_SECOND = 3
    protected static final long WAIT_TIME_MILLIS = (TimeUnit.SECONDS.toMillis(1) / REQUESTS_PER_SECOND).longValue()

    protected final Queue<Request> requests = new LinkedList<Request>()

    private final DocumentBuilder xmlBuilder

    AbstractVKWorker() {
        DocumentBuilderFactory f = DocumentBuilderFactory.newInstance()
        f.setValidating(false)
        xmlBuilder = f.newDocumentBuilder()
    }

    int getRequestsCount() {
        requests.size()
    }

    abstract void stop()

    protected String _executeQuery(VKRequest request) throws IOException, InterruptedException {
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
        String xmlString = _executeQuery(request)
        Document document = xmlBuilder.parse(new ByteArrayInputStream(xmlString.getBytes()))
        Element result = document.getDocumentElement()
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

            if (errorCode == VKException.CAPTCHA_NEEDED) {
                String captchaSid = result.getElementsByTagName("captcha_sid").item(0).getTextContent()
                String captchaImg = result.getElementsByTagName("captcha_img").item(0).getTextContent()
                throw new VKCaptchaNeededException(errorMsg, requestParams, captchaSid, captchaImg)
            }

            throw new VKException(errorCode, errorMsg, requestParams)
        }

        return result
    }
}
