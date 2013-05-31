package com.vk.api.audio

import com.vk.api.Identifier
import groovy.transform.Immutable
import groovy.transform.ToString

/**
 * @author acmi
 */
@Immutable
@ToString(includePackage = false, includes = ['identifier', 'artist', 'title'])
class Audio {
    Identifier identifier
    String artist
    String title
    int duration
    String url
    String performer
    int lyricsId
}
