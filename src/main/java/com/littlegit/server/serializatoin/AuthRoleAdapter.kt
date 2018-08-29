package com.littlegit.server.serializatoin

import com.littlegit.server.application.exception.NoSuchEnumValueException
import com.littlegit.server.model.GitServerRegion
import com.littlegit.server.model.repo.RepoAccessLevel
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

    internal fun anyToString(raw: Any) = when (raw) {
        is String -> raw
        else -> raw.toString()
    }

    @JvmStatic fun addAllTo(adapters: MutableMap<Class<out Any>, Converter<out Any>>) {
        adapters[AuthRole::class.java] = AuthRoleAdapter()
        adapters[RepoAccessLevel::class.java] = RepoAccessLevelAdapter()
        adapters[GitServerRegion::class.java] = GitServerRegionAdapter()
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

class GitServerRegionAdapter: Converter<GitServerRegion> {

    @FromJson override fun convert(raw: Any): GitServerRegion? {
        try {

            val rawString = EnumAdapters.anyToString(raw)
            return GitServerRegion.fromRaw(rawString)!!

        } catch (e: Exception) {
            throw NoSuchEnumValueException(raw)
        }
    }

    @ToJson override fun toDatabaseParam(region: GitServerRegion): Any = region.code

}