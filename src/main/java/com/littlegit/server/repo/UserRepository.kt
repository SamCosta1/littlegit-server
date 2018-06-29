package com.littlegit.server.repo

import com.littlegit.server.db.DatabaseConnector
import com.littlegit.server.model.User
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor (private val dbCon: DatabaseConnector) {

    companion object {
        var a = 1
    }

    fun getUser(): User {
        return User("email@email", "john", "smith ${a++}")
    }

}