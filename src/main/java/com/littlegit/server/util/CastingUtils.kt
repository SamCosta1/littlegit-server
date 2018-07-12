package com.littlegit.server.util

import com.littlegit.server.application.exception.ProgrammerError
import com.littlegit.server.model.user.User
import java.security.Principal

fun Principal.asUser(): User {
    if (this is User) {
        return this
    } else {
        throw ProgrammerError()
    }
}