package com.littlegit.server.util

import java.text.MessageFormat

object StringUtils {
    fun generate(length: Int, vararg  includingChars: Char): String {
        val builder = StringBuilder()

        for (i in 0 until length - includingChars.size) {
            builder.append("a")
        }

        includingChars.forEach { builder.append(it) }

        return builder.toString()
    }

    fun bytesToString(hash: ByteArray): String {
        val hexString = StringBuffer()

        for (i in hash.indices) {
            if (0xff and hash[i].toInt() < 0x10) {
                hexString.append("0" + Integer.toHexString(0xFF and hash[i].toInt()))
            } else {
                hexString.append(Integer.toHexString(0xFF and hash[i].toInt()))
            }
        }

        return hexString.toString()
    }
}

fun String.inject(vararg params: Any) = MessageFormat.format(this, *params)!!
