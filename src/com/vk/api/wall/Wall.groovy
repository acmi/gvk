package com.vk.api.wall

import com.vk.api.VKEngine

/**
 * @author acmi
 */
class Wall {
    static WallIterator get(VKEngine engine, int ownerId, int offset = 0, Filter filter = Filter.all) {
        new WallIterator(engine, ownerId, offset, filter)
    }

    static enum Filter {
        owner,
        others,
        all
    }

    static CommentIterator getComments(VKEngine engine, int ownerId, int postId, int offset = 0, Sort sort = Sort.asc) {
        new CommentIterator(engine, ownerId, postId, offset, sort)
    }

    static enum Sort {
        asc,
        desc
    }
}
