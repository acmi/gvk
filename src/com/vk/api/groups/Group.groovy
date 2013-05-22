package com.vk.api.groups

import com.vk.api.Info
import groovy.transform.Immutable

/**
 * @author acmi
 */
@Immutable
class Group implements Info{
    int gid
    String name
    String screenName

    @Override
    int getId() {
        -gid
    }

//    boolean closed
//    boolean admin
//    Type type
//    String photo
//    String photoSmall
//    String phostBig
//
//    static enum Type{
//        group
//    }
}
