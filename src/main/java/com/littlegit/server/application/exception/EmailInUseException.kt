package com.littlegit.server.application.exception

data class EmailInUseException(val email: String) : Throwable()
