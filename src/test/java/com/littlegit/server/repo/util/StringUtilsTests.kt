package com.littlegit.server.repo.util

import com.littlegit.server.util.StringUtils
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class StringUtilsTests {

    @Test
    fun testGenerateWithoutExtraCharacters() {
        val intendedLength = 10
        val result = StringUtils.generate(intendedLength)

        assertEquals(intendedLength, result.length)
    }

    @Test
    fun testGenerateWithExtraCharacters() {
        val intendedLength = 10
        val chars = arrayOf('@', '6', 'g')
        val result = StringUtils.generate(intendedLength, chars[0], chars[1], chars[2])

        assertEquals(intendedLength, result.length)

        chars.forEach { assertTrue(result.contains(it)) }
    }

    @Test
    fun testGenerateWithOnlyCharacters() {
        val intendedLength = 3
        val chars = arrayOf('@', '6', 'g')
        val result = StringUtils.generate(intendedLength, chars[0], chars[1], chars[2])

        assertEquals(intendedLength, result.length)

        chars.forEach { assertTrue(result.contains(it)) }
    }
}