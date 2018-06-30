package com.littlegit.server.repo

import com.littlegit.server.db.DatabaseConnector
import com.littlegit.server.model.User
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor (private val dbCon: DatabaseConnector) {

    fun getUser(id: Int): User? {

        val sql = """
            SELECT * FROM Users
            WHERE Id = :id
        """
        val params = mapOf("id" to id)

        val users = dbCon.executeSelect(sql, User::class.java, params = params)

        if (users == null || users.isEmpty()) {
            return null
        }

        return users.first()
    }

}