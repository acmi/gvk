package com.vk.api.groups

import com.vk.api.VKException
import com.vk.api.VKWorker

/**
 * @author acmi
 */
class GroupsCommon {
    private final VKWorker worker

    GroupsCommon(VKWorker worker) {
        this.worker = worker
    }

    protected VKWorker getWorker() { worker }

    /**
     * Возвращает информацию о заданной группе или о нескольких группах.
     *
     * @param gids ID групп, перечисленные через запятую, информацию о которых необходимо получить. В качестве ID могут быть использованы короткие имена групп. Максимум 500 групп.
     * @return Итератор групп
     * @throws IOException
     * @throws com.vk.api.VKException
     */
    Iterator<Group> getById(List gids) throws IOException, VKException{
        Groups.getById(worker, gids)
    }
}
