package com.littlegit.server.model

import java.time.OffsetDateTime

data class Repo(val id: Int,
                val repoName: String,
                val createdDate: OffsetDateTime,
                val creatorId: Int,
                val description:   String,
                val serverId: Int,
                val cloneUrlPath: String)
