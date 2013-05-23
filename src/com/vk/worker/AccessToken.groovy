package com.vk.worker

import groovy.transform.Immutable

/**
 * @author acmi
 */
@Immutable
class AccessToken implements Serializable {
    int userId
    String token
}
