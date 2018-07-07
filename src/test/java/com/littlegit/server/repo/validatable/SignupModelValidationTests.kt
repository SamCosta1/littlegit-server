package com.littlegit.server.repo.validatable

import com.littlegit.server.repo.testUtils.UserHelper
import com.littlegit.server.util.StringUtils
import junit.framework.Assert.assertTrue
import org.junit.Test
import kotlin.test.assertEquals

// TODO: Check the notes are correct once localisation is in place
class SignupModelValidationTests() {

    @Test
    fun testValidModel_IsSuccessful() {
        val result = UserHelper.createSignupModel("gandalf@thefive..mag.uk",
                "W1zardsRule",
                "Gandalf",
                "The Grey",
                "en-GB").validate()

        assertTrue(result.isValid)
        assertEquals(0,result.invalidMessages.size)
    }

    @Test
    fun testEmailWithoutAtSymbol_Fails() {
        val result = UserHelper.createSignupModel("gandalg.thefive.mag.uk").validate()
        assertTrue(result.isNotValid)
        assertEquals(1, result.invalidMessages.size)
    }

    @Test
    fun testEmailWithoutDot_Fails() {
        val result = UserHelper.createSignupModel("gandal-thefive@mag-uk").validate()
        assertTrue(result.isNotValid)
        assertEquals(1, result.invalidMessages.size)
    }

    @Test
    fun testEmailBlank_Fails() {
        val result = UserHelper.createSignupModel("").validate()
        assertTrue(result.isNotValid)
        assertEquals(1, result.invalidMessages.size)
    }

    @Test
    fun testFirstNameBlank_Fails() {
        val result = UserHelper.createSignupModel(firstName = "").validate()
        assertTrue(result.isNotValid)
        assertEquals(1, result.invalidMessages.size)
    }

    @Test
    fun testLanguageCodeBlank_Fails() {
        val result = UserHelper.createSignupModel(languageCode = "").validate()
        assertTrue(result.isNotValid)
        assertEquals(1, result.invalidMessages.size)
    }

    @Test
    fun testEmailTooLong_Fails() {
        val result = UserHelper.createSignupModel(email = StringUtils.generate(51, '@', '.')).validate()
        assertTrue(result.isNotValid)
        assertEquals(1, result.invalidMessages.size)
    }

    @Test
    fun testFirstNameTooLong_Fails() {
        val result = UserHelper.createSignupModel(firstName = StringUtils.generate(51)).validate()
        assertTrue(result.isNotValid)
        assertEquals(1, result.invalidMessages.size)
    }

    @Test
    fun testSurnameTooLong_Fails() {
        val result = UserHelper.createSignupModel(surname = StringUtils.generate(51)).validate()
        assertTrue(result.isNotValid)
        assertEquals(1, result.invalidMessages.size)
    }

    @Test
    fun testLanguageCodeTooLong_Fails() {
        val result = UserHelper.createSignupModel(languageCode = StringUtils.generate(21)).validate()
        assertTrue(result.isNotValid)
        assertEquals(1, result.invalidMessages.size)
    }

}