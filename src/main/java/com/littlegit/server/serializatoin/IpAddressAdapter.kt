package com.littlegit.server.serializatoin

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import org.sql2o.converters.Converter
import sun.plugin.dom.exception.InvalidStateException
import java.net.InetAddress

class IpAddressAdapter: Converter<InetAddress> {

    override fun toDatabaseParam(ip: InetAddress?): Any? {
        if (ip == null) {
            return null
        }

        return ip.toString()
    }

    override fun convert(raw: Any): InetAddress {
        if (raw !is String) {
            throw InvalidStateException(raw.toString())
        }

        return fromJson(raw)
    }

    @ToJson fun toJson(ip: InetAddress?): String? {
        return ip?.toString() ?: ""
    }

    @FromJson fun fromJson(ip: String): InetAddress {
        return InetAddress.getByName(ip)
    }
}