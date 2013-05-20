package com.vk.api

import groovy.transform.Immutable

/**
 * @author acmi
 */
@Immutable
class Identifier {
    int ownerId
    int mediaId
    Type type

    String toString(boolean showType){
        "${showType ? type.name() : ''}${ownerId}_${mediaId}"
    }

    @Override
    String toString() {
        toString(true)
    }

    static enum Type{
        post,
        photo,
        video,
        audio,
        doc
    }
}
