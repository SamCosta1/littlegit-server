package com.littlegit.server.serializatoin

import com.littlegit.server.application.exception.NoSuchEnumValueException
import com.littlegit.server.model.auth.TokenType
import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import org.sql2o.converters.Converter

class TokenTypeAdapter: Converter<TokenType> {

    @FromJson override fun convert(raw: Any): TokenType? {
        try {

            val rawInt = if (raw is Int)
                raw
            else if (raw is Double)
                raw.toInt()
            else raw.toString().toBigDecimal().toInt()
            
            return TokenType.fromInt(rawInt)!!

        } catch (e: Exception) {
            throw NoSuchEnumValueException(raw)
        }
    }

    @ToJson override fun toDatabaseParam(role: TokenType): Any = role.code

}