package com.littlegit.server.serializatoin

import com.littlegit.server.application.exception.NoSuchEnumValueException
import com.littlegit.server.model.repoAccess.RepoAccessLevel
import com.littlegit.server.model.user.AuthRole
import com.squareup.moshi.FromJson
import com.squareup.moshi.Moshi
import com.squareup.moshi.ToJson
import org.sql2o.converters.Converter

object EnumAdapters {

    internal fun anyToInt(raw: Any) = when (raw) {
        is Int -> raw
        is Double -> raw.toInt()
        else -> raw.toString().toBigDecimal().toInt()
    }


    @JvmStatic fun addAllTo(adapters: MutableMap<Class<out Any>, Converter<out Any>>) {
        adapters[AuthRole::class.java] = AuthRoleAdapter()
        adapters[RepoAccessLevel::class.java] = RepoAccessLevelAdapter()
    }

    @JvmStatic fun addAllTo(builder: Moshi.Builder) {
        builder.add(AuthRoleAdapter())
        builder.add(RepoAccessLevelAdapter())
    }
}
class AuthRoleAdapter: Converter<AuthRole> {

    @FromJson override fun convert(raw: Any): AuthRole? {
        try {

            val rawInt = EnumAdapters.anyToInt(raw)

            return AuthRole.fromInt(rawInt)!!

        } catch (e: Exception) {
            throw NoSuchEnumValueException(raw)
        }
    }


    @ToJson override fun toDatabaseParam(role: AuthRole): Any = role.code

}

class RepoAccessLevelAdapter: Converter<RepoAccessLevel> {

    @FromJson override fun convert(raw: Any): RepoAccessLevel? {
        try {

            val rawInt = EnumAdapters.anyToInt(raw)
            return RepoAccessLevel.fromInt(rawInt)!!

        } catch (e: Exception) {
            throw NoSuchEnumValueException(raw)
        }
    }

    @ToJson override fun toDatabaseParam(level: RepoAccessLevel): Any = level.code

}