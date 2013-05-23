package com.vk.worker.impl

import com.vk.worker.VKRequest
import groovy.transform.Canonical
import groovy.transform.PackageScope

/**
 * @author acmi
 */
@Canonical
@PackageScope
class Request {
    VKRequest request
    ObjectHolder response
}
