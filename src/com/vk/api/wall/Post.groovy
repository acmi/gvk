package com.vk.api.wall

import com.vk.api.Message
import groovy.transform.Canonical

/**
 * @author acmi
 */
@Canonical
final class Post extends Message{
    final int id
    final int from
    final int to

    Post(Date date, String text, int id, int from, int to){
        super(date, text)

        this.id = id
        this.from = from
        this.to = to
    }

    @Override
    String toString() {
        "${from}->${to} ${date}: ${text}"
    }
}
