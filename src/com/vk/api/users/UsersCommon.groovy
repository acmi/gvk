package com.vk.api.users

import com.vk.worker.VKAnonymousWorker
import com.vk.worker.VKException
import com.vk.worker.VKRequest
import groovy.xml.dom.DOMCategory

/**
 * @author acmi
 */
class UsersCommon {
    /**
     * Возвращает расширенную информацию о пользователях.
     *
     * @param worker VKAnonymousWorker
     * @param uids перечисленные через запятую ID пользователей или их короткие имена (screen_name). Максимум 1000 пользователей.
     * @param nameCase падеж для склонения имени и фамилии пользователя.
     * @return Возвращает информацию о пользователях
     * @throws IOException
     * @throws VKException
     */
    static Iterator<User> get(VKAnonymousWorker worker, Collection uids, NameCase nameCase = NameCase.nom) throws IOException, VKException {
        use(DOMCategory) {
            worker.executeQuery(new VKRequest('users.get', [
                    uids: uids.join(','),
                    fields: ['uid', 'first_name', 'last_name', 'screen_name'].join(','),
                    name_case: nameCase.name()
            ])).user.collect {
                new User(
                        it.uid.text().toInteger(),
                        it.first_name.text(),
                        it.last_name.text(),
                        it.screen_name.text()
                )
            }.iterator()
        }
    }

    /**
     * Возвращает расширенную информацию о пользователе.
     *
     * @param worker VKAnonymousWorker
     * @param uids ID пользователя или их короткое имя (screen_name).
     * @param nameCase падеж для склонения имени и фамилии пользователя.
     * @return Возвращает информацию о пользователях
     * @throws IOException
     * @throws VKException
     */
    static User get(VKAnonymousWorker worker, uid, NameCase nameCase = NameCase.nom) throws IOException, VKException {
        Iterator<User> it = get(worker, [uid], nameCase)
        if (it.hasNext())
            return it.next()
        null
    }
}
