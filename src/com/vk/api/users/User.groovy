package com.vk.api.users

import com.vk.api.Info
import groovy.transform.Immutable

/**
 * @author acmi
 */
@Immutable
class User implements Info{
    int uid
    String firstName
    String lastName
    String screenName

    @Override
    int getId() {
        uid
    }

    @Override
    String getName(){
        "$firstName $lastName"
    }
}
