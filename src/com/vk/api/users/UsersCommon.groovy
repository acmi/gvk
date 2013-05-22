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
     * @param worker VKWorker
     * @param uids перечисленные через запятую ID пользователей или их короткие имена (screen_name). Максимум 1000 пользователей.
     * @param nameCase падеж для склонения имени и фамилии пользователя.
     * @return Возвращает информацию о пользователях
     * @throws IOException
     * @throws com.vk.api.VKException
     */
    Iterator<User> get(List uids, Users.NameCase nameCase = Users.NameCase.nom) throws IOException, VKException {
        Users.get(worker, uids, nameCase)
    }
}
