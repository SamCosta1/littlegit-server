package com.littlegit.server.model.repoAccess

import com.littlegit.server.model.RepoId
import com.littlegit.server.model.user.UserId

data class RepoAccess(val userId: UserId, val repoId: RepoId, val active: Boolean, val level: RepoAccessLevel)



enum class RepoAccessLevel(val code: Int) {
    Owner(1),
    Contributor(2);

    companion object {
        private val map = RepoAccessLevel.values().associateBy(RepoAccessLevel::code)
        fun fromInt(type: Int) = map[type]
    }
}