package com.vk.api

/**
 * @author acmi
 */
class Message {
    Date date
    String text

    Map findLinks(){
        text.findAll('\\[\\w+\\|.+?\\]').collectEntries {
            def (k,v) = it[1..-2].split('\\|')
            [k, v]
        }
    }
}
