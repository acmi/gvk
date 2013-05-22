package com.vk.api.likes

import com.vk.api.VKException
import com.vk.api.VKRequest
import com.vk.api.VKWorkerUser
import groovy.xml.dom.DOMCategory

/**
 * @author acmi
 */
class LikesFull extends LikesCommon {
    /**
     * Добавляет указанный объект в список Мне нравится текущего пользователя.
     *
     * @param worker VKWorkerUser
     * @param ownerId идентификатор владельца Like-объекта. В случае записей и комментариев на стене ownerId равен идентификатору страницы со стеной, а не автору записи.
     * @param type идентификатор типа Like-объекта.
     * @param itemId идентификатор Like-объекта.
     * @return В случае успеха возвращает текущее количество пользователей, которые добавили данный объект в свой список Мне нравится.
     * @throws IOException
     * @throws VKException
     */
    static Integer add(VKWorkerUser worker, int ownerId = worker.userId, Type type, int itemId) throws IOException, VKException {
        use(DOMCategory) {
            worker.executeQuery(new VKRequest('likes.add', [
                    owner_id: ownerId,
                    type: type.name(),
                    item_id: itemId
            ])).likes.text().toInteger()
        }
    }

    /**
     * Удаляет указанный объект из списка Мне нравится текущего пользователя.
     *
     * @param worker VKWorkerUser
     * @param ownerId идентификатор владельца Like-объекта.
     * @param type идентификатор типа Like-объекта.
     * @param itemId идентификатор Like-объекта.
     * @return В случае успеха возвращает текущее количество пользователей, которые добавили данный объект в свой список Мне нравится.
     * @throws IOException
     * @throws VKException
     */
    static Integer delete(VKWorkerUser worker, int ownerId = worker.userId, Type type, int itemId) throws IOException, VKException {
        use(DOMCategory) {
            worker.executeQuery(new VKRequest('likes.delete', [
                    owner_id: ownerId,
                    type: type.name(),
                    item_id: itemId
            ])).likes.text().toInteger()
        }
    }
}
