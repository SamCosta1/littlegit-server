package com.littlegit.server.repo.repo

import com.littlegit.server.model.InvalidModelException
import com.littlegit.server.model.user.CreateSshKeyModel
import com.littlegit.server.repo.testUtils.CleanupHelper
import com.littlegit.server.repo.testUtils.RepositoryHelper
import com.littlegit.server.repo.testUtils.UserHelper
import com.littlegit.server.repo.testUtils.assertSshKey
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class SshKeyRepoTests {

    @Test(expected = InvalidModelException::class)
    fun testCreateInvalidModel_InvalidUserId_ThrowsException() {
        val createModel = CreateSshKeyModel("Public key", -1)
        RepositoryHelper.sshKeyRepository.createSshKey(createModel)
    }

    @Test(expected = InvalidModelException::class)
    fun testCreateInvalidModel_InvalidKey_ThrowsException() {
        val createModel = CreateSshKeyModel("", 1)
        RepositoryHelper.sshKeyRepository.createSshKey(createModel)
    }

    @Test
    fun testCreateValidPublicKey_IsSuccessful() {
        val user = UserHelper.createTestUser()
        val createModel = CreateSshKeyModel("Gandalf's favourite public key", user.id)

        val cleaner = {
           CleanupHelper.cleanupSshKey(createModel.publicKey)
        }

        cleaner()

        try {
            val id = RepositoryHelper.sshKeyRepository.createSshKey(createModel)
            assertNotNull(id); id!!

            val keys = RepositoryHelper.sshKeyRepository.getSshKeysForUser(user, false)
            assertNotNull(keys); keys!!

            assertEquals(1, keys.size)
            assertSshKey(createModel, id, keys.first())
        } finally {
            cleaner()
        }
    }

    @Test
    fun testGetKeys_ActiveOnly_IsSuccessful() {
        val userSignupModel = UserHelper.createSignupModel()
        val publicKey1 = "Gandalf's favourite public key"
        val publicKey2 = "Gandalf's second favourite public key"
        val publicKey3 = "Gandalf's third favourite public key"

        val cleaner = {
            CleanupHelper.cleanupUser(userSignupModel.email)
            CleanupHelper.cleanupSshKey(publicKey1)
            CleanupHelper.cleanupSshKey(publicKey3)
            CleanupHelper.cleanupSshKey(publicKey2)
        }

        cleaner()


        try {
            val userId = RepositoryHelper.userRepository.createUser(userSignupModel)!!
            val createModel1 = CreateSshKeyModel(publicKey1, userId)
            val createModel2 = CreateSshKeyModel(publicKey2, userId, false)
            val createModel3 = CreateSshKeyModel(publicKey3, userId)

            val id1 = RepositoryHelper.sshKeyRepository.createSshKey(createModel1)
            val id2 = RepositoryHelper.sshKeyRepository.createSshKey(createModel2)
            val id3 = RepositoryHelper.sshKeyRepository.createSshKey(createModel3)
            assertNotNull(id1); id1!!
            assertNotNull(id2); id2!!
            assertNotNull(id3); id3!!

            val keys = RepositoryHelper.sshKeyRepository.getSshKeysForUser(RepositoryHelper.userRepository.getUser(userId)!!, true)
            assertNotNull(keys); keys!!

            assertEquals(2, keys.size)
            assertSshKey(createModel1, id1, keys.first())
            assertSshKey(createModel3, id3, keys.last())
        } finally {
            cleaner()
        }
    }
}