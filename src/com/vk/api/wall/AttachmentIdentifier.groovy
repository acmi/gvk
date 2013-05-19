package com.vk.api.wall

import groovy.transform.Immutable

/**
 * @author acmi
 */
@Immutable
class AttachmentIdentifier {
    int ownerId
    int mediaId
    Type type

    @Override
    String toString() {
        "${type.name()}${ownerId}_${mediaId}"
    }

    static enum Type{
        photo,
        video,
        audio,
        doc
    }
}
