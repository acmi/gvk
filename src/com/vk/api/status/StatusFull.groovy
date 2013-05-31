package com.vk.api.status

import com.vk.api.Identifier
import com.vk.worker.VKException
import com.vk.worker.VKIdentifiedWorker
import com.vk.worker.VKRequest
import groovy.xml.dom.DOMCategory

/**
 * @author acmi
 */
class StatusFull extends StatusCommon {
    /**
     * {@link StatusCommon#get(com.vk.worker.VKAnonymousWorker, int)}
     *
     * @param worker {@link VKIdentifiedWorker}
     * @param uid идентификатор пользователя, статус которого необходимо получить. Если параметр не задан, то считается, что он равен идентификатору текущего пользователя.
     * @return В случае успеха возвращает объект, у которого в поле text содержится текст статуса пользователя.
     * @throws IOException
     * @throws VKException
     */
    static Status get(VKIdentifiedWorker worker, int uid = worker.userId) throws IOException, VKException {
        StatusCommon.get(worker, uid)
    }

    /**
     * Устанавливает новый статус текущему пользователю.
     *
     * @param worker {@link VKIdentifiedWorker}
     * @param text текст нового статуса.
     * @param audio идентификатор аудиозаписи, которая будет отображаться в статусе.
     * @return В случае успешной установки или очистки статуса возвращает true.     .
     * @throws IOException
     * @throws VKException
     */
    static Boolean set(VKIdentifiedWorker worker, String text = null, Identifier audio = null) throws IOException, VKException{
        use(DOMCategory) {
            worker.executeQuery(new VKRequest('status.set', [
                    text: text,
                    audio: audio
            ])).text() == '1'
        }
    }
}
