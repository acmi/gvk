package com.vk.api.status

import com.vk.api.VKCaptchaNeededException
import com.vk.api.VKEngine
import com.vk.api.VKException
import com.vk.api.wall.Identifier
import groovy.transform.Immutable
import groovy.xml.dom.DOMCategory

/**
 * @author acmi
 */
@Immutable
class Status {
    String text

    static Status get(VKEngine engine, int uid) throws IOException, VKException {
        use(DOMCategory) {
            def response = engine.executeQuery('status.get', [uid: uid])
            new Status(
                    response.text.text()
            )
        }
    }

    static Boolean set(VKEngine engine, String text, Identifier audio = null) throws IOException, VKCaptchaNeededException, VKException {
        use(DOMCategory) {
            Map params = [text: text]
            if (audio != null)
                params['audio'] = audio
            engine.executeQuery('status.set', params).text() == '1'
        }
    }
}
