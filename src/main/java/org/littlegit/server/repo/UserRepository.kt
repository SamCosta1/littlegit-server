package org.littlegit.server.repo

import org.littlegit.server.db.DatabaseConnector
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor (private val dbCon: DatabaseConnector){

}