package com.vk.api.wall

import com.vk.api.Identifier
import com.vk.api.Message
import groovy.transform.Immutable

/**
 * @author acmi
 */
@Immutable
class Comment implements Message {
    Identifier identifier
    int user
    Date date
    String text

    @Override
    String toString() {
        "${user}(${date}): ${text}"
    }
}
