package com.littlegit.server.serializatoin

import com.littlegit.server.application.exception.NoSuchEnumValueException
import com.littlegit.server.model.user.AuthRole
import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import org.sql2o.converters.Converter

class AuthRoleAdapter: Converter<AuthRole> {

    @FromJson override fun convert(raw: Any): AuthRole? {
        try {

            val rawInt = if (raw is Int)
                raw
            else if (raw is Double)
                raw.toInt()
            else raw.toString().toBigDecimal().toInt()

            return AuthRole.fromInt(rawInt)!!

        } catch (e: Exception) {
            throw NoSuchEnumValueException(raw)
        }
    }

    @ToJson override fun toDatabaseParam(role: AuthRole): Any = role.code

}