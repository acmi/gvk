package com.vk.api.users

import com.vk.api.VKException
import com.vk.api.VKWorker

/**
 * @author acmi
 */
class UsersCommon {
    private final VKWorker worker

    UsersCommon(VKWorker worker) {
        this.worker = worker
    }

    protected VKWorker getWorker() { worker }

    /**
     * Возвращает расширенную информацию о пользователях.
     *
     * @param uids перечисленные через запятую ID пользователей или их короткие имена (screen_name). Максимум 1000 пользователей.
     * @param nameCase падеж для склонения имени и фамилии пользователя.
     * @return Возвращает информацию о пользователях
     * @throws IOException
     * @throws com.vk.api.VKException
     */
    Iterator<User> get(List uids, Users.NameCase nameCase = Users.NameCase.nom) throws IOException, VKException {
        Users.get(worker, uids, nameCase)
    }

    /**
     * Возвращает расширенную информацию о пользователе.
     *
     * @param uids ID пользователя или их короткое имя (screen_name).
     * @param nameCase падеж для склонения имени и фамилии пользователя.
     * @return Возвращает информацию о пользователях
     * @throws IOException
     * @throws com.vk.api.VKException
     */
    User get(uid, Users.NameCase nameCase = Users.NameCase.nom) throws IOException, VKException {
        Iterator<User> it = get([uid], nameCase)
        if (it.hasNext())
            return it.next()
        null
    }
}
