package com.vk.api.groups

import com.vk.api.Info
import groovy.transform.Immutable
import groovy.transform.ToString

/**
 * @author acmi
 */
@Immutable
@ToString(includePackage = false, excludes = ['screenName'])
class Group implements Info {
    int gid
    String name
    String screenName

    @Override
    int getId() {
        -gid
    }
}
