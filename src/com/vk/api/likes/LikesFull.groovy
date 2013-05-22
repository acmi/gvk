package com.vk.api.likes

import com.vk.api.VKException
import com.vk.api.VKWorkerUser

/**
 * @author acmi
 */
class LikesFull extends LikesCommon {
    LikesFull(VKWorkerUser worker) {
        super(worker)
    }

    @Override
    protected VKWorkerUser getWorker() {
        super.getWorker()
    }

    /**
     * Добавляет указанный объект в список Мне нравится текущего пользователя.
     *
     * @param ownerId идентификатор владельца Like-объекта. В случае записей и комментариев на стене ownerId равен идентификатору страницы со стеной, а не автору записи.
     * @param type идентификатор типа Like-объекта.
     * @param itemId идентификатор Like-объекта.
     * @return В случае успеха возвращает текущее количество пользователей, которые добавили данный объект в свой список Мне нравится.
     * @throws IOException
     * @throws com.vk.api.VKException
     */
    Integer add(int ownerId, Likes.Type type, int itemId) throws IOException, VKException {
        Likes.add(worker, ownerId, type, itemId)
    }

    /**
     * Удаляет указанный объект из списка Мне нравится текущего пользователя.
     *
     * @param ownerId идентификатор владельца Like-объекта.
     * @param type идентификатор типа Like-объекта.
     * @param itemId идентификатор Like-объекта.
     * @return В случае успеха возвращает текущее количество пользователей, которые добавили данный объект в свой список Мне нравится.
     * @throws IOException
     * @throws com.vk.api.VKException
     */
    Integer delete(int ownerId, Likes.Type type, int itemId) throws IOException, VKException {
        Likes.delete(worker, ownerId, type, itemId)
    }
}
