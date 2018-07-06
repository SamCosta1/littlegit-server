package com.littlegit.server.application.exception

import java.lang.RuntimeException

class UserUnauthorizedException(val msg: String): RuntimeException() {

    override fun toString(): String {
        return msg + "  " + super.toString()
    }
}
