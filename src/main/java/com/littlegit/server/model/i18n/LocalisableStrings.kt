package com.littlegit.server.model.i18n

typealias LS = LocalizableString

// DO NOT change the keys. They'll be hardcoded into clients so hardcoding them will break everything
enum class LocalizableString(val key: String) {
    InvalidEmail("invalid_email"),
    InvalidPassword("invalid_password"),
    FirstNameBlank("first_name_blank"),
    SurnameBlank("surname_blank"),
    LanguageCodeBlank("language_code_blank"),
    SurnameTooLong("surname_to_long"),
    FirstNameTooLong("first_name_too_long"),
    InvalidLanguageCode("invalid_language_code"),
    Response500Body("5xx_error"),
    EmailInUse("email_in_use")
}