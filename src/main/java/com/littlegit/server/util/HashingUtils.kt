package com.littlegit.server.util

import java.security.MessageDigest
import java.security.SecureRandom


object HashingUtils {

    private val random = SecureRandom()

    fun hash(str: String, salt: String): String {
        val messageDigest = MessageDigest.getInstance("SHA-256")
        val concatenated = "$str--$salt"
        messageDigest.update(concatenated.toByteArray())

        return bytesToString(messageDigest.digest())
    }

    fun generateSalt(): String {
        val saltBytes = ByteArray(32)
        random.nextBytes(saltBytes)
        return bytesToString(saltBytes)
    }

    private fun bytesToString(hash: ByteArray): String {
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