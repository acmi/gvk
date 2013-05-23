package com.vk.api.wall

import com.vk.api.Identifier
import com.vk.api.Message
import groovy.transform.Canonical

/**
 * @author acmi
 */
@Canonical
final class Comment extends Message {
    final Identifier identifier
    final int user

    Comment(int ownerId, int id, int user, Date date, String text) {
        super(date, text)

        this.identifier = new Identifier(ownerId, id, Identifier.Type.comment)
        this.user = user
    }

    @Override
    String toString() {
        "${user} ${date}: ${text}"
    }
}
