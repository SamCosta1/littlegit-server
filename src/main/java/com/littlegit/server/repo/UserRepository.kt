package com.littlegit.server.repo

import com.littlegit.server.db.Cache
import com.littlegit.server.db.DatabaseConnector
import com.littlegit.server.model.User
import java.text.MessageFormat
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor (private val dbCon: DatabaseConnector,
                                          private val cache: Cache) {

    companion object {
        private const val USER_CACHE_KEY = "User(Id:{0})"
    }

    fun getUser(id: Int): User? {

        val databaseAccessor: () -> User? = {
            val sql = """
                SELECT * FROM Users
                WHERE Id = :id
            """
            val params = mapOf("id" to id)

            val users = dbCon.executeSelect(sql, User::class.java, params = params)

            users?.firstOrNull()
        }

       // return cache.retrieve(MessageFormat.format(USER_CACHE_KEY, id), User::class.java, databaseAccessor)
        return databaseAccessor.invoke()
    }
}