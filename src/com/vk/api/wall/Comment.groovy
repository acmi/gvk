package com.vk.api.wall

import com.vk.api.Message
import groovy.transform.Canonical

/**
 * @author acmi
 */
@Canonical
final class Comment extends Message{
    final int id
    final int user

    Comment(int id, int user, Date date, String text){
        super(date, text)

        this.id = id
        this.user = user
    }

    @Override
    String toString() {
        "${user} ${date}: ${text}"
    }
}
