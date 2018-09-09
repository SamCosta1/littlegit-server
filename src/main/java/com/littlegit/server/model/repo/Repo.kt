package com.littlegit.server.model.repo

import com.littlegit.server.model.Validatable
import com.littlegit.server.model.ValidatableResult
import com.littlegit.server.model.i18n.LocalizableString
import java.time.OffsetDateTime
import java.util.regex.Pattern

typealias RepoId = Int
typealias CloneUrlPath = String
data class Repo(val id: RepoId,
                val repoName: String,
                val createdDate: OffsetDateTime,
                val creatorId: Int,
                val description:   String,
                val serverId: Int,
                val cloneUrlPath: CloneUrlPath,
                val filePath: String) {

    fun toRepoSummary(): RepoSummary = RepoSummary(id, repoName, createdDate, description, cloneUrlPath)
}

data class RepoSummary(val id: RepoId, val repoName: String, val createdDate: OffsetDateTime, val description: String, val cloneUrlPath: CloneUrlPath)

data class CreateRepoModel(val repoName: String, val description: String = ""): Validatable {

    override fun validate(): ValidatableResult {

        val invalidMessages = mutableListOf<LocalizableString>()

        if (repoName.isBlank()) invalidMessages.add(LocalizableString.RepoNameBlank)
        if (repoName.length >= 20) invalidMessages.add(LocalizableString.RepoNameTooLong)
        if (!repoName.matches(Pattern.compile("\\w+[\\w-]+").toRegex())) invalidMessages.add(LocalizableString.RepoNameInvalid)
        if (description.length >= 200) invalidMessages.add(LocalizableString.DescriptionTooLong)

        return ValidatableResult(invalidMessages.isEmpty(), invalidMessages)
    }
}