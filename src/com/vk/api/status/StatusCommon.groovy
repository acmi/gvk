package com.vk.api.status

import com.vk.api.VKException
import com.vk.api.VKRequest
import com.vk.api.VKWorker
import groovy.xml.dom.DOMCategory

/**
 * @author acmi
 */
class StatusCommon {
    /**
     * Получает текст статуса пользователя.
     *
     * @param worker VKWorker
     * @param uid идентификатор пользователя, статус которого необходимо получить.
     * @return В случае успеха возвращает объект, у которого в поле text содержится текст статуса пользователя.
     * @throws IOException
     * @throws VKException
     */
    static Status get(VKWorker worker, int uid) throws IOException, VKException {
        use(DOMCategory) {
            def response = worker.executeQuery(new VKRequest('status.get', [uid: uid]))

            new Status(
                    response.text.text()
            )
        }
    }
}
