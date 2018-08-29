package com.littlegit.server.model

import com.littlegit.server.model.i18n.LocalizableString
import java.net.InetAddress

typealias GitServerId = Int

data class GitServer(val id: GitServerId,
                          override val ip: InetAddress,
                          override val region: String,
                          override val capacity: Int)
    : CreateGitServerModel(ip, region, capacity)

open class CreateGitServerModel(open val ip: InetAddress,
                                open val region: String,
                                open val capacity: Int): Validatable {

    override fun validate(): ValidatableResult {
        val invalidMessages = mutableListOf<LocalizableString>()

        if (capacity <= 0) invalidMessages.add(LocalizableString.InvalidCapacity)
        return ValidatableResult(invalidMessages)
    }

}

enum class GitServerRegion(val code: String) {
    UK("uk");

    companion object {
        private val map = GitServerRegion.values().associateBy(GitServerRegion::code)
        fun fromRaw(raw: String) = map[raw]
    }
}
