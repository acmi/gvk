package com.vk.api.wall

import com.vk.api.Message

/**
 * @author acmi
 */
class Post extends Message{
    int id
    int from
    int to

    @Override
    String toString() {
        "${from}->${to} ${date}: ${text}"
    }
}
