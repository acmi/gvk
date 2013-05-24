package com.vk.api.other

import com.vk.worker.VKException
import com.vk.worker.VKIdentifiedWorker
import com.vk.worker.VKRequest
import groovy.xml.dom.DOMCategory

/**
 * @author acmi
 */
class OtherFull extends OtherCommon {
    /**
     * Данный метод возвращает информацию о том, установил ли текущий пользователь приложение или нет.
     *
     * @param worker VKIdentifiedWorker
     * @param uid ID пользователя.
     * @return Метод isAppUser возвращает true в случае, если пользователь установил у себя данное приложение, иначе false.
     * @throws IOException
     * @throws com.vk.worker.VKException
     */
    static boolean isAppUser(VKIdentifiedWorker worker, int uid = worker.userId) throws IOException, VKException {
        OtherCommon.isAppUser(worker, uid)
    }

    /**
     * Получает настройки текущего пользователя в данном приложении.
     *
     * @param worker VKIdentifiedWorker
     * @param uid ID пользователя. По умолчанию ID текущего пользователя.
     * @return Возвращает битовую маску настроек текущего пользователя в данном приложени.
     * @throws IOException
     * @throws VKException
     */
    static int getUserSettings(VKIdentifiedWorker worker, int uid = worker.userId) throws IOException, VKException {
        OtherCommon.getUserSettings(worker, uid)
    }

    /**
     * Устанавливает короткое название приложения (до 17 символов), которое выводится пользователю в левом меню. Это происходит только в том случае, если пользователь добавил приложение в левое меню со страницы приложения, списка приложений или настроек.
     *
     * @param worker VKIdentifiedWorker
     * @param name короткое название приложения для левого меню, до 17 символов в формате UTF.
     * @return
     * @throws IOException
     * @throws VKException
     */
    static boolean setNameInMenu(VKIdentifiedWorker worker, String name) throws IOException, VKException {
        use(DOMCategory) {
            worker.executeQuery(new VKRequest('setNameInMenu', [name: name])).text() == '1'
        }
    }
}
