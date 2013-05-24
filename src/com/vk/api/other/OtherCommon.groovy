package com.vk.api.other

import com.vk.api.Info
import com.vk.api.groups.GroupsCommon
import com.vk.api.users.UsersCommon
import com.vk.worker.VKAnonymousWorker
import com.vk.worker.VKException
import com.vk.worker.VKRequest
import groovy.xml.dom.DOMCategory

import java.util.concurrent.TimeUnit

/**
 * @author acmi
 */
class OtherCommon {
    /**
     * Данный метод возвращает информацию о том, установил ли текущий пользователь приложение или нет.
     *
     * @param worker VKAnonymousWorker
     * @param uid ID пользователя.
     * @return Метод isAppUser возвращает true в случае, если пользователь установил у себя данное приложение, иначе false.
     * @throws IOException
     * @throws VKException
     */
    static boolean isAppUser(VKAnonymousWorker worker, int uid) throws IOException, VKException {
        use(DOMCategory) {
            worker.executeQuery(new VKRequest('isAppUser', [uid: uid])).text() == '1'
        }
    }

    /**
     * Получает настройки текущего пользователя в данном приложении.
     *
     * @param worker VKAnonymousWorker
     * @param uid ID пользователя.
     * @return Возвращает битовую маску настроек текущего пользователя в данном приложени.
     * @throws IOException
     * @throws VKException
     */
    static int getUserSettings(VKAnonymousWorker worker, int uid) throws IOException, VKException {
        use(DOMCategory) {
            worker.executeQuery(new VKRequest('getUserSettings', [uid: uid])).settings.text().toInteger()
        }
    }

    /**
     * Возвращает текущую дату на сервере ВКонтакте
     *
     * @param worker VKAnonymousWorker
     * @return
     * @throws IOException
     * @throws VKException
     */
    static Date getServerTime(VKAnonymousWorker worker) throws IOException, VKException {
        use(DOMCategory) {
            new Date(TimeUnit.SECONDS.toMillis(worker.executeQuery(new VKRequest('getServerTime', [:])).text().toLong()))
        }
    }

    static Info getInfoById(VKAnonymousWorker worker, int id) throws IOException, VKException {
        if (id < 0) {
            return GroupsCommon.getById(worker, -id)
        } else if (id > 0) {
            return UsersCommon.get(worker, id)
        }
        null
    }

    static Info getInfoByScreenName(VKAnonymousWorker worker, String screenName) throws IOException, VKException {
        def group = GroupsCommon.getById(worker, screenName)
        if (group == null) {
            return UsersCommon.get(worker, screenName)
        } else {
            return group
        }
    }

    static Info getInfo(VKAnonymousWorker worker, id) throws IOException, VKException {
        if (id instanceof Number)
            getInfoById(worker, id)

        try {
            id = Integer.valueOf(id.toString())
            return getInfoById(worker, id)
        } catch (NumberFormatException e) {
            return getInfoByScreenName(worker, id)
        }
    }
}
