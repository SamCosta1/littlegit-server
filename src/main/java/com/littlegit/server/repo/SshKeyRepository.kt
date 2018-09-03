package com.littlegit.server.repo

import com.littlegit.server.db.Cache
import com.littlegit.server.db.DatabaseConnector
import com.littlegit.server.model.GitServer
import com.littlegit.server.model.InvalidModelException
import com.littlegit.server.model.user.CreateSshKeyModel
import com.littlegit.server.model.user.SshKey
import com.littlegit.server.model.user.SshKeyId
import com.littlegit.server.model.user.User
import javax.inject.Inject

class SshKeyRepository@Inject constructor (private val dbCon: DatabaseConnector,
                                           private val cache: Cache) {

    fun getSshKeysForUser(user: User, activeOnly: Boolean = true): List<SshKey>? {
        var sql = """
            SELECT * FROM SshKeys
            WHERE userId=:userId
        """

        if (activeOnly) {
            sql = """
                $sql
                AND active=true
            """
        }

        return dbCon.executeSelect(sql, SshKey::class.java, params = mapOf("userId" to user.id))
    }

    fun createSshKey(createModel: CreateSshKeyModel): SshKeyId? {

        val validationResult = createModel.validate()
        if (validationResult.isNotValid) {
            throw InvalidModelException(validationResult)
        }

        return dbCon.executeInsert("""
            INSERT INTO SshKeys (
                publicKey,
                userId,
                active
            )
            VALUES (:publicKey, :userId, :active)
        """, model = createModel)
    }
}