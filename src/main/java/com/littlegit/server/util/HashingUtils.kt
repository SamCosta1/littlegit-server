package com.littlegit.server.util

import java.security.MessageDigest
import java.security.SecureRandom


object HashingUtils {

    private val random = SecureRandom()

    fun hash(str: String, salt: String): String {
        val messageDigest = MessageDigest.getInstance("SHA-256")
        val concatenated = "$str--$salt"
        messageDigest.update(concatenated.toByteArray())

        return StringUtils.bytesToString(messageDigest.digest())
    }

    fun generateSalt(): String {
        val saltBytes = ByteArray(32)
        random.nextBytes(saltBytes)
        return StringUtils.bytesToString(saltBytes)
    }
}