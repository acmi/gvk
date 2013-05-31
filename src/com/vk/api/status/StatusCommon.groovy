package com.vk.api.status

import com.vk.api.Identifier
import com.vk.api.audio.Audio
import com.vk.worker.VKAnonymousWorker
import com.vk.worker.VKException
import com.vk.worker.VKRequest
import groovy.xml.dom.DOMCategory

/**
 * @author acmi
 */
class StatusCommon {
    /**
     * Получает текст статуса пользователя.
     *
     * @param worker {@link VKAnonymousWorker}
     * @param uid идентификатор пользователя, статус которого необходимо получить.
     * @return В случае успеха возвращает объект, у которого в поле text содержится текст статуса пользователя.
     * @throws IOException
     * @throws VKException
     */
    static Status get(VKAnonymousWorker worker, int uid) throws IOException, VKException {
        use(DOMCategory) {
            def response = worker.executeQuery(new VKRequest('status.get', [uid: uid]))

            new Status(
                    response.text.text(),
                    response.audio.size() == 0 ? null :
                        new Audio(
                                new Identifier(
                                        response.audio.owner_id.text().toInteger(),
                                        response.audio.aid.text().toInteger(),
                                        Identifier.Type.audio
                                ),
                                response.audio.artist.text(),
                                response.audio.title.text(),
                                response.audio.duration.text().toInteger(),
                                response.audio.url.text(),
                                response.audio.performer.text(),
                                response.audio.lyrics_id.text().toInteger()
                        )
            )
        }
    }
}
