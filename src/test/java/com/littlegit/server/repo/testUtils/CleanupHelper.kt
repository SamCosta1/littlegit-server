package com.littlegit.server.repo.testUtils

import java.math.BigInteger

object CleanupHelper {

    fun cleanupUser(email: String) {

        val ids = RepositoryHelper.dbConnector.executeScalar("""
            SELECT id FROM Users WHERE email=:email
        """, Integer::class.java, params = mapOf("email" to email))

        RepositoryHelper.dbConnector.executeDelete("""

            DELETE FROM Users WHERE email=:email

        """, mapOf("email" to email))

        ids?.forEach { RepositoryHelper.userRepository.invalidateCache(it.toInt()) }
    }
}