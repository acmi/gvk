package com.vk.api.groups

import com.vk.api.Info
import groovy.transform.Immutable

/**
 * @author acmi
 */
@Immutable
class Group implements Info {
    int gid
    String name
    String screenName

    @Override
    int getId() {
        -gid
    }
}
