package com.littlegit.server.application.settings


data class DbConfig(
    var host: String,
    var multipleStatements: Boolean,
    var driver: String,
    var password: String,
    var user: String,
    val database: String
)