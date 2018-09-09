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
    EmailInUse("email_in_use"),
    UsernameInUse("username_in_use"),
    RepoNameBlank("repo_name_blank"),
    RepoNameTooLong("repo_name_too_long"),
    RepoNameInvalid("repo-name-invalid"),
    DescriptionTooLong("description_too_long"),
    InvalidCapacity("invalid_capacity"),
    UsernameBlank("blank_username"),
    InvalidUsername("invalid_username"),
    ValueAlreadyExists("value_already_exists"),
    InvalidUserId("invalid_user_id"),
    InvalidPublicKey("invalid_public_key")

}