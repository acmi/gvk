package com.vk.api.likes

import com.vk.api.VKEngine
import com.vk.api.VKException
import groovy.xml.dom.DOMCategory

/**
 * @author acmi
 */
class Likes {
    /**
     * Добавляет указанный объект в список Мне нравится текущего пользователя.
     *
     * @param engine VKEngine
     * @param ownerId идентификатор владельца Like-объекта. В случае записей и комментариев на стене ownerId равен идентификатору страницы со стеной, а не автору записи.
     * @param type идентификатор типа Like-объекта.
     * @param itemId идентификатор Like-объекта.
     * @return В случае успеха возвращает текущее количество пользователей, которые добавили данный объект в свой список Мне нравится.
     * @throws IOException
     * @throws VKException
     */
    static Integer add(VKEngine engine, int ownerId, Type type, int itemId) throws IOException, VKException {
        use(DOMCategory) {
            engine.executeQuery('likes.add', [
                    owner_id: ownerId,
                    type: type.name(),
                    item_id: itemId
            ]).likes.text().toInteger()
        }
    }

    /**
     * Удаляет указанный объект из списка Мне нравится текущего пользователя.
     *
     * @param engine VKEngine
     * @param ownerId идентификатор владельца Like-объекта.
     * @param type идентификатор типа Like-объекта.
     * @param itemId идентификатор Like-объекта.
     * @return В случае успеха возвращает текущее количество пользователей, которые добавили данный объект в свой список Мне нравится.
     * @throws IOException
     * @throws VKException
     */
    static Integer delete(VKEngine engine, int ownerId, Type type, int itemId) throws IOException, VKException {
        use(DOMCategory) {
            engine.executeQuery('likes.delete', [
                    owner_id: ownerId,
                    type: type.name(),
                    item_id: itemId
            ]).likes.text().toInteger()
        }
    }

    /**
     * Получает список идентификаторов пользователей, которые добавили заданный объект в свой список Мне нравится.
     *
     * @param engine VKEngine
     * @param likeType тип Like-объекта.
     * @param ownerId дентификатор владельца Like-объекта (id пользователя или id приложения). Если параметр type равен sitepage, то в качестве owner_id необходимо передавать id приложения. Если параметр не задан, то считается, что он равен либо идентификатору текущего пользователя, либо идентификатору текущего приложения (если type равен sitepage).
     * @param itemId идентификатор Like-объекта. Если type равен sitepage, то параметр item_id может содержать значение параметра page_id, используемый при инициализации виджета «Мне нравится».
     * @param offset смещение, относительно начала списка, для выборки определенного подмножества.
     * @param filter указывает, следует ли вернуть всех пользователей, добавивших объект в список "Мне нравится" или только тех, которые рассказали о нем друзьям.
     * @param friendsOnly указывает, необходимо ли возвращать только пользователей, которые являются друзьями текущего пользователя.
     * @return Итератор Like
     */
    static Iterator<Like> getList(VKEngine engine, Type likeType, int ownerId, int itemId, int offset = 0, Filter filter = Filter.likes, boolean friendsOnly = false) {
        new LikeIterator(engine, likeType, ownerId, itemId, offset, filter, friendsOnly)
    }

    /**
     * Проверяет находится ли объект в списке Мне нравится заданного пользователя.
     * @param engine VKEngine
     * @param userId идентификатор пользователя у которого необходимо проверить наличие объекта в списке Мне нравится.
     * @param ownerId идентификатор владельца Like-объекта.
     * @param type идентификатор типа Like-объекта.
     * @param itemId идентификатор Like-объекта.
     * @return указанный Like-объект находится в списке Мне нравится пользователя с идентификатором userId
     * @throws IOException
     * @throws VKException
     */
    static Boolean isLiked(VKEngine engine, int userId, int ownerId, Type type, int itemId) throws IOException, VKException {
        use(DOMCategory) {
            engine.executeQuery('likes.isLiked', [
                    user_id: userId,
                    owner_id: ownerId,
                    type: type.name(),
                    item_id: itemId
            ]).text() == '1'
        }
    }

    static enum Type {
        post,
        comment,
        photo,
        audio,
        video,
        note,
        sitepage
    }

    static enum Filter {
        likes,
        copies
    }
}
