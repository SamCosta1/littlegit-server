package com.littlegit.server.repo.testUtils

import com.littlegit.server.model.CreateGitServerModel
import com.littlegit.server.model.GitServer
import com.littlegit.server.model.auth.Token
import com.littlegit.server.model.user.CreateSshKeyModel
import com.littlegit.server.model.user.SshKey
import com.littlegit.server.model.user.SshKeyId
import com.littlegit.server.model.user.UserId
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

internal fun assertSshKey(createModel: CreateSshKeyModel, createdId: SshKeyId, actual: SshKey) {
    assertEquals(createModel.active, actual.active)
    assertEquals(createModel.publicKey, actual.publicKey)
    assertEquals(createModel.userId, actual.userId)
    assertEquals(createdId, actual.id)
}