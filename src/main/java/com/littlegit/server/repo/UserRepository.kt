package com.littlegit.server.repo

import com.littlegit.server.db.Cache
import com.littlegit.server.db.DatabaseConnector
import com.littlegit.server.model.*
import com.littlegit.server.util.HashingUtils
import java.text.MessageFormat
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor (private val dbCon: DatabaseConnector,
                                          private val cache: Cache) {

    companion object {
        private const val USER_CACHE_KEY = "User(Id:{0})"
    }

    fun getFullUsers(id: Int): FullUser? {

        return cache.retrieve(MessageFormat.format(USER_CACHE_KEY, id), FullUser::class.java) {
            val sql = """
                SELECT * FROM Users
                WHERE Id = :id
            """
            val params = mapOf("id" to id)

            val users = dbCon.executeSelect(sql, FullUser::class.java, params = params)

            users?.firstOrNull()
        }
    }

    fun createUser(signupModel: SignupModel): UserId? {
        val salt = HashingUtils.generateSalt()
        val passwordHash = HashingUtils.hash(signupModel.password, salt)

        val user = CreateUserModel.fromSignupModel(signupModel, salt, passwordHash)

        return dbCon.executeInsert("""
            INSERT INTO Users (
                firstName,
                surname,
                email,
                passwordHash,
                passwordSalt,
                role,
                languageCode
            )
            VALUES (:firstName, :surname, :email, :passwordHash, :passwordSalt, :role, :languageCode);
        """, model = user)
    }

    fun invalidateCache(userId: UserId) {
        cache.delete(MessageFormat.format(USER_CACHE_KEY, userId))
    }
}