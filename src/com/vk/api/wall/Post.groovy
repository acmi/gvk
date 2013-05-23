package com.vk.api.wall

import com.vk.api.Identifier
import com.vk.api.Message
import groovy.transform.Canonical

/**
 * @author acmi
 */
@Canonical
final class Post extends Message{
    final Identifier identifier
    final int from
    final int to

    Post(Date date, String text, int ownerId, int postId, int from, int to){
        super(date, text)

        this.identifier = new Identifier(ownerId, postId, Identifier.Type.post)
        this.from = from
        this.to = to
    }

    @Override
    String toString() {
        "${from}->${to} ${date}: ${text}"
    }
}
