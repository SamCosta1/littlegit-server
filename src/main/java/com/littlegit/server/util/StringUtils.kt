package com.littlegit.server.util

object StringUtils {
    fun generate(length: Int, vararg  includingChars: Char): String {
        val builder = StringBuilder()

        for (i in 0 until length - includingChars.size) {
            builder.append("a")
        }

        includingChars.forEach { builder.append(it) }

        return builder.toString()
    }
}