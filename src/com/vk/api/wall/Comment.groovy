package com.vk.api.wall

import com.vk.api.Message

/**
 * @author acmi
 */
class Comment extends Message{
    int id
    int user

    @Override
    String toString() {
        "${user} ${date}: ${text}"
    }
}
