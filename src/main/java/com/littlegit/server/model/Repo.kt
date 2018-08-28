package com.littlegit.server.model

import com.littlegit.server.model.i18n.LocalizableString
import com.littlegit.server.model.user.AuthRole
import java.time.OffsetDateTime

typealias RepoId = Int
data class Repo(val id: RepoId,
                val repoName: String,
                val createdDate: OffsetDateTime,
                val creatorId: Int,
                val description:   String,
                val serverId: Int,
                val cloneUrlPath: String) {

    fun toRepoSummary(): RepoSummary = RepoSummary(id, repoName, createdDate, description, cloneUrlPath)
}

data class RepoSummary(val id: RepoId, val repoName: String, val createdDate: OffsetDateTime, val description: String, val cloneUrlPath: String)

data class CreateRepoModel(val repoName: String, val description: String): Validatable {

    override fun validate(): ValidatableResult {

        val invalidMessages = mutableListOf<LocalizableString>()

        if (repoName.isBlank()) invalidMessages.add(LocalizableString.RepoNameBlank)
        if (repoName.length >= 20) invalidMessages.add(LocalizableString.RepoNameTooLong)
        if (description.length >= 200) invalidMessages.add(LocalizableString.DescriptionTooLong)

        return ValidatableResult(invalidMessages.isEmpty(), invalidMessages)
    }
}
