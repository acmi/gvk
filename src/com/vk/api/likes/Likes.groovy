package com.vk.api.likes

import com.vk.api.VKEngine
import com.vk.api.VKException
import groovy.xml.dom.DOMCategory

/**
 * @author acmi
 */
class Likes {
    static Integer add(VKEngine engine, int ownerId, Type type, int itemId) throws IOException, VKException {
        use(DOMCategory) {
            engine.executeQuery('likes.add', [
                    owner_id: ownerId,
                    type: type.name(),
                    item_id: itemId
            ]).response.likes.text().toInteger()
        }
    }

    static Integer delete(VKEngine engine, int ownerId, Type type, int itemId) throws IOException, VKException {
        use(DOMCategory) {
            engine.executeQuery('likes.delete', [
                    owner_id: ownerId,
                    type: type.name(),
                    item_id: itemId
            ]).response.likes.text().toInteger()
        }
    }

    static Iterator<Like> getList(VKEngine engine, Type likeType, int ownerId, int itemId, int offset = 0, Filter filter = Filter.likes, boolean friendsOnly = false) {
        new LikeIterator(engine, likeType, ownerId, itemId, offset, filter, friendsOnly)
    }

    static Boolean isLiked(VKEngine engine, int ownerId, Type type, int itemId) throws IOException, VKException {
        use(DOMCategory) {
            engine.executeQuery('likes.delete', [
                    owner_id: ownerId,
                    type: type.name(),
                    item_id: itemId
            ]).response.likes.text().toInteger()
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
