package com.vk.api.likes

import groovy.transform.Immutable

/**
 * @author acmi
 */
@Immutable
class Like {
    int user
    boolean copied

    @Override
    String toString() {
        "${copied ? '[COPY]' : '[LIKE]'} $user"
    }
}
