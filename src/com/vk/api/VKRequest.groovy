package com.vk.api

import groovy.transform.Immutable
import groovy.transform.ToString

/**
 * @author acmi
 */
@Immutable
@ToString(includePackage = false, includeNames = true)
class VKRequest implements Serializable{
    String method
    Map params
    boolean xml
}
