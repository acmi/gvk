package com.vk.api.other

import com.vk.api.Info
import com.vk.api.groups.GroupsCommon
import com.vk.api.status.Status
import com.vk.api.status.StatusCommon
import com.vk.api.users.UsersCommon
import com.vk.api.wall.Filter
import com.vk.api.wall.Post
import com.vk.api.wall.WallCommon
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
            getInfoById(worker, id.intValue())

        try {
            id = Integer.valueOf(id.toString())
            return getInfoById(worker, id)
        } catch (NumberFormatException e) {
            return getInfoByScreenName(worker, id)
        }
    }

    /**
     * {@link com.vk.api.wall.WallCommon#get(com.vk.worker.VKAnonymousWorker, int, int, com.vk.api.wall.Filter)}
     * @param user
     * @param worker
     * @param offset
     * @param filter
     * @return
     */
    static Iterator<Post> getWall(Info user, VKAnonymousWorker worker, int offset = 0, Filter filter = Filter.all){
        WallCommon.get(worker, user.id, offset, filter)
    }

    static Status getStatus(Info user, VKAnonymousWorker worker){
        StatusCommon.get(worker, user.id)
    }

}
