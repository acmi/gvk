package com.vk.api.status

import com.vk.api.Identifier
import com.vk.api.VKException
import com.vk.api.VKRequest
import com.vk.api.VKWorkerUser
import groovy.xml.dom.DOMCategory

/**
 * @author acmi
 */
class StatusFull extends StatusCommon {
    /**
     * Получает текст статуса пользователя.
     *
     * @param worker VKWorker
     * @param uid идентификатор пользователя, статус которого необходимо получить. Если параметр не задан, то считается, что он равен идентификатору текущего пользователя.
     * @return В случае успеха возвращает объект, у которого в поле text содержится текст статуса пользователя.
     * @throws IOException
     * @throws VKException
     */
    static Status get(VKWorkerUser worker, int uid = worker.userId) throws IOException, VKException {
        StatusCommon.get(worker, uid)
    }

    static Boolean set(VKWorkerUser worker, String text = null, Identifier audio = null) {
        use(DOMCategory) {
            worker.executeQuery(new VKRequest('status.set', [
                    text: text,
                    audio: audio
            ])).text() == '1'
        }
    }
}
