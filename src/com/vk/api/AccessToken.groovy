package com.vk.api

import groovy.transform.Immutable

import java.util.concurrent.TimeUnit

/**
 * @author acmi
 */
@Immutable
class AccessToken implements Serializable {
    String token
    int userId
    long expiresIn

    Date getExpireDate() {
        new Date(expiresIn > 0 ? System.currentTimeMillis() + expiresIn : Long.MAX_VALUE)
    }

    static AccessToken create(String token, int userId, long expiresIn, TimeUnit timeUnit) {
        new AccessToken(token, userId, timeUnit.toMillis(expiresIn))
    }
}
