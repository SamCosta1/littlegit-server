package com.littlegit.server.repo

import com.littlegit.server.db.Cache
import com.littlegit.server.db.DatabaseConnector
import com.littlegit.server.model.InvalidModelException
import com.littlegit.server.model.user.*
import javax.inject.Inject

class SshKeyRepository@Inject constructor (private val dbCon: DatabaseConnector,
                                           private val cache: Cache) {

    fun getSshKeysForUser(user: User, activeOnly: Boolean = true): List<FullSshKey>? {
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

        return dbCon.executeSelect(sql, FullSshKey::class.java, params = mapOf("userId" to user.id))
    }

    fun sshKeyExists(sshKey: SshKey): Boolean? {
        return dbCon.executeScalar("""
            SELECT  COUNT(id) > 0
            FROM    SshKeys
            WHERE   publicKey=:publicKey
            AND     userId=:userId
        """, Boolean::class.java, model = sshKey)?.firstOrNull()
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