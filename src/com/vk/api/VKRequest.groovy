package com.vk.api

import groovy.transform.Immutable

/**
 * @author acmi
 */
@Immutable
class VKRequest implements Serializable{
    String method
    Map params
    boolean xml

    @Override
    String toString() {
        "$method${xml ? '.xml' : ''}$params"
    }
}
