package com.vk.api

import groovy.transform.Canonical

/**
 * @author acmi
 */
@Canonical
class Message {
    final Date date
    final String text

    Message(Date date, String text) {
        this.date = date
        this.text = text
    }

    Map findLinks() {
        text.findAll('\\[\\w+\\|.+?\\]').collectEntries {
            def (k, v) = it[1..-2].split('\\|')
            [k, v]
        }
    }
}
