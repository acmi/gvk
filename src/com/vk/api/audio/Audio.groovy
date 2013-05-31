package com.vk.api.audio

import groovy.transform.Immutable

/**
 * @author acmi
 */
@Immutable
class Audio {
    int id
    int ownerId
    String artist
    String title
    int duration
    String url
    String performer
    int lyricsId
}
