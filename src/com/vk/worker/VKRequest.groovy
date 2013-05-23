package com.vk.worker

import groovy.transform.Immutable

/**
 * @author acmi
 */
@Immutable
class VKRequest {
    final String method
    final Map params
}
