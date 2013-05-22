package com.vk.api.status

import com.vk.api.VKException
import com.vk.api.VKRequest
import com.vk.api.VKWorker
import groovy.xml.dom.DOMCategory

/**
 * @author acmi
 */
class StatusCommon {
    private final VKWorker worker

    StatusCommon(VKWorker worker) {
        this.worker = worker
    }

    protected VKWorker getWorker() { worker }

    /**
     * Получает текст статуса пользователя.
     *
     * @param uid
     * @return В случае успеха возвращает объект, у которого в поле text содержится текст статуса пользователя.
     * @throws IOException
     * @throws VKException
     */
    Status get(int uid) throws IOException, VKException{
          use(DOMCategory){
              def response = worker.executeQuery(new VKRequest('status.get', [uid: uid]))

              new Status(
                      response.text.text()
              )
          }
    }
}
