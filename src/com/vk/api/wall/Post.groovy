package com.vk.api.wall

import com.vk.api.Identifier
import com.vk.api.Message
import groovy.transform.Immutable

/**
 * @author acmi
 */
@Immutable
class Post implements Message {
    Identifier identifier
    int from
    int to
    Date date
    String text

    @Override
    String toString() {
        "${from}->${to}(${date}): ${text}"
    }
}
