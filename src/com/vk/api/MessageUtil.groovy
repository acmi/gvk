package com.vk.api

/**
 * @author acmi
 */
class MessageUtil {
    static Map findLinks(Message message) {
        message.text.findAll('\\[\\w+\\|.+?\\]').collectEntries {
            def (k, v) = it[1..-2].split('\\|')
            [k, v]
        }
    }
}
