package com.vk.api.likes

import com.vk.api.VKException
import com.vk.api.VKWorker

/**
 * @author acmi
 */
class LikesCommon {
    private final VKWorker worker

    LikesCommon(VKWorker worker) {
        this.worker = worker
    }

    protected VKWorker getWorker() { worker }

    /**
     * Получает список идентификаторов пользователей, которые добавили заданный объект в свой список Мне нравится.
     *
     * @param likeType тип Like-объекта.
     * @param ownerId дентификатор владельца Like-объекта (id пользователя или id приложения). Если параметр type равен sitepage, то в качестве owner_id необходимо передавать id приложения. Если параметр не задан, то считается, что он равен либо идентификатору текущего пользователя, либо идентификатору текущего приложения (если type равен sitepage).
     * @param itemId идентификатор Like-объекта. Если type равен sitepage, то параметр item_id может содержать значение параметра page_id, используемый при инициализации виджета «Мне нравится».
     * @param offset смещение, относительно начала списка, для выборки определенного подмножества.
     * @param filter указывает, следует ли вернуть всех пользователей, добавивших объект в список "Мне нравится" или только тех, которые рассказали о нем друзьям.
     * @param friendsOnly указывает, необходимо ли возвращать только пользователей, которые являются друзьями текущего пользователя.
     * @return Итератор Like
     */
    Iterator<Like> getList(Likes.Type likeType, int ownerId, int itemId, int offset = 0, Likes.Filter filter = Likes.Filter.likes, boolean friendsOnly = false) {
        Likes.getList(worker, likeType, ownerId, itemId, offset, filter, friendsOnly)
    }

    /**
     * Проверяет находится ли объект в списке Мне нравится заданного пользователя.
     *
     * @param userId идентификатор пользователя у которого необходимо проверить наличие объекта в списке Мне нравится.
     * @param ownerId идентификатор владельца Like-объекта.
     * @param type идентификатор типа Like-объекта.
     * @param itemId идентификатор Like-объекта.
     * @return указанный Like-объект находится в списке Мне нравится пользователя с идентификатором userId
     * @throws IOException
     * @throws com.vk.api.VKException
     */
    Boolean isLiked(int userId, int ownerId, Likes.Type type, int itemId) throws IOException, VKException {
        Likes.isLiked(worker, userId, ownerId, type, itemId)
    }
}
