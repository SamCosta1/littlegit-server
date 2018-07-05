package com.littlegit.server.repo.util

import com.littlegit.server.util.HashingUtils
import org.junit.Test
import kotlin.test.assertEquals

class HashingUtilsTests {

    @Test
    fun testHash() {
        val stringToHash = "password"
        val salt = HashingUtils.generateSalt()

        val hash1 = HashingUtils.hash(stringToHash, salt)
        val hash2 = HashingUtils.hash(stringToHash, salt)

        assertEquals(hash1, hash2)
    }

    @Test
    fun testGenerateUniqueSalt() {
        val salts = mutableListOf<String>()

        for (i in 1..200) {
            salts.add(HashingUtils.generateSalt())
        }

        val distinctSalts = salts.distinct()

        assertEquals(salts.size, distinctSalts.size)
    }

}