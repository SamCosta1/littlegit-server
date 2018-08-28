package com.littlegit.server.repo

import com.littlegit.server.db.Cache
import com.littlegit.server.db.DatabaseConnector
import com.littlegit.server.model.*
import com.littlegit.server.model.user.*
import com.littlegit.server.util.HashingUtils
import com.littlegit.server.util.inject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class UserRepository @Inject constructor (private val dbCon: DatabaseConnector,
                                          private val cache: Cache) {

    companion object {
        const val USER_CACHE_KEY_BY_ID = "User(Id:{0})"
        const val USER_CACHE_BY_EMAIL = "User(Email:{0})"
    }

    fun getFullUser(id: Int): FullUser? {

        return cache.retrieve(USER_CACHE_KEY_BY_ID.inject(id), FullUser::class.java) {
            val sql = """
                SELECT * FROM Users
                WHERE Id = :id
            """
            val params = mapOf("id" to id)

            val users = dbCon.executeSelect(sql, FullUser::class.java, params = params)

            users?.firstOrNull()
        }
    }

    fun getUser(email: String): User? {
        return getFullUser(email)?.toUser()
    }

    fun getFullUser(email: String): FullUser? {

        return cache.retrieve(USER_CACHE_BY_EMAIL.inject(email), FullUser::class.java) {
            val sql = """
                SELECT * FROM Users
                WHERE email = :email
            """
            val params = mapOf("email" to email)

            val users = dbCon.executeSelect(sql, FullUser::class.java, params = params)

            if (users != null && users.size > 1) {
                throw IllegalStateException("Multiple users have the same email")
            }

            users?.firstOrNull()
        }
    }

    fun getUser(id: UserId): User? {
        return getFullUser(id)?.toUser()
    }

    fun createUser(signupModel: SignupModel): UserId? {
        val salt = HashingUtils.generateSalt()
        val passwordHash = HashingUtils.hash(signupModel.password, salt)

        val validationResult = signupModel.validate()
        if (validationResult.isNotValid) {
            throw InvalidModelException(validationResult)
        }

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

    fun invalidateCache(userId: UserId) = cache.delete(USER_CACHE_KEY_BY_ID.inject(userId))
    fun invalidateCache(email: String) = cache.delete(USER_CACHE_BY_EMAIL.inject(email))
}