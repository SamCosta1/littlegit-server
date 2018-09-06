package com.littlegit.server.repo.testUtils

import com.littlegit.server.model.CreateGitServerModel
import com.littlegit.server.model.GitServer
import com.littlegit.server.model.auth.Token
import com.littlegit.server.model.repo.Repo
import com.littlegit.server.model.repo.RepoAccess
import com.littlegit.server.model.repo.RepoAccessLevel
import com.littlegit.server.model.user.*
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal fun assertToken(userId: UserId?, expected: Token, actual: Token?) {
    assertEquals(userId, actual?.userId)
    assertEquals(expected.token, actual?.token)
    assertEquals(expected.tokenType, actual?.tokenType)
    assertTrue(expected.expiry.isEqual(actual?.expiry))
}

internal fun assertGitServer(createModel: CreateGitServerModel, actual: GitServer?) {
    assertEquals(createModel.capacity, actual?.capacity)
    assertEquals(createModel.ip, actual?.ip)
    assertEquals(createModel.region, actual?.region)
}

internal fun assertSshKey(createModel: CreateSshKeyModel, createdId: SshKeyId, actual: FullSshKey) {
    assertEquals(createModel.active, actual.active)
    assertEquals(createModel.publicKey, actual.publicKey)
    assertEquals(createModel.userId, actual.userId)
    assertEquals(createdId, actual.id)
}

internal fun assertRepo(repo: Repo, user: User, repoAccessLevel: RepoAccessLevel, active: Boolean, repoAccess: RepoAccess?) {
    assertEquals(repo.id, repoAccess?.repoId)
    assertEquals(user.id, repoAccess?.userId)
    assertEquals(repoAccessLevel, repoAccess?.level)
    assertEquals(active, repoAccess?.active)
}