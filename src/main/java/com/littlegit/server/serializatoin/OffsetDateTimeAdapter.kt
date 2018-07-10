package com.littlegit.server.serializatoin

import com.littlegit.server.model.InvalidModelException
import org.sql2o.converters.Converter
import sun.plugin.dom.exception.InvalidStateException
import java.sql.Timestamp
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*

class OffsetDateTimeAdapter: Converter<OffsetDateTime> {
    private val dateTimeFormatter = DateTimeFormatter.ISO_DATE_TIME

    override fun toDatabaseParam(dateTime: OffsetDateTime?): Any? {
        if (dateTime == null) {
            return null
        }

        dateTime.withNano(0)
        return Timestamp.from(dateTime.toInstant())
    }

    override fun convert(raw: Any): OffsetDateTime {
        if (raw !is Date) {
            throw InvalidStateException(raw.toString())
        }

        return OffsetDateTime.ofInstant(raw.toInstant(), ZoneOffset.UTC).withNano(0)
    }
}