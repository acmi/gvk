package com.vk.api

import com.vk.api.groups.GroupsCommon
import com.vk.api.likes.LikesCommon
import com.vk.api.status.StatusCommon
import com.vk.api.users.UsersCommon
import com.vk.api.wall.WallCommon
import org.w3c.dom.Element;

/**
 * @author acmi
 */
interface VKWorker {
    Element executeQuery(VKRequest request) throws Exception

    UsersCommon getUsers()
    GroupsCommon getGroups()
    LikesCommon getLikes()
    WallCommon getWall()
    StatusCommon getStatus()
}
