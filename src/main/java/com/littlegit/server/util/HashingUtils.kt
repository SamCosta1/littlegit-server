package com.littlegit.server.util

import com.oracle.util.Checksums.update
import java.security.MessageDigest
import java.security.SecureRandom


object HashingUtils {

    private val random = SecureRandom()

    fun hash(str: String, salt: String): String {
        val messageDigest = MessageDigest.getInstance("SHA-256")
        val concatenated = "$str--$salt"
        messageDigest.update(concatenated.toByteArray())

        return String(messageDigest.digest())
    }

    fun generateSalt(): String {
        val saltBytes = ByteArray(32)
        random.nextBytes(saltBytes)
        return String(saltBytes)
    }
}