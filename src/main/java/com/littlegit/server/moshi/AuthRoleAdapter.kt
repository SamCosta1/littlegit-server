package com.littlegit.server.moshi

import com.littlegit.server.application.exception.NoSuchAuthRoleException
import com.littlegit.server.model.AuthRole
import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import org.sql2o.converters.Converter

class AuthRoleAdapter: Converter<AuthRole> {

    override fun convert(raw: Any): AuthRole? {
        if (raw is Int) {
            return toAuthRole(raw)
        }

        return null
    }

    override fun toDatabaseParam(role: AuthRole): Any {
        return fromAuthRole(role)
    }

    @FromJson fun toAuthRole(authCode: Int): AuthRole {
        try {
            return AuthRole.fromInt(authCode)!!
        } catch (e: Exception) {
            throw NoSuchAuthRoleException(authCode)
        }
    }

    @ToJson fun fromAuthRole(authRole: AuthRole): Int {
        return authRole.code
    }
}