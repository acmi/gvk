package com.vk.worker

import groovy.transform.Immutable

/**
 * @author acmi
 */
@Immutable
class VKRequest {
    String method
    Map params
}
