package org.littlegit.server.db

import org.littlegit.server.application.LittleGitSettings
import org.littlegit.server.repo.UserRepository
import org.sql2o.Sql2o
import javax.inject.Inject

class DatabaseConnector @Inject constructor (private val settings: LittleGitSettings) {
    private val sql2o: Sql2o

    init {
        sql2o = Sql2o("jdbc:mysql://localhost:3306/littlegit", "myUsername", "topSecretPassword")
    }
}