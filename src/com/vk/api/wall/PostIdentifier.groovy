package com.vk.api.wall

import groovy.transform.Immutable

/**
 * @author acmi
 */
@Immutable
class PostIdentifier {
    int ownerId
    int postId

    @Override
    String toString() {
        "${ownerId}_${postId}"
    }
}
