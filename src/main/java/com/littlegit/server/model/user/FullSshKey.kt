package com.littlegit.server.model.user

import com.littlegit.server.model.Validatable
import com.littlegit.server.model.ValidatableResult
import com.littlegit.server.model.i18n.LocalizableString

typealias SshKeyId = Int
data class FullSshKey(val id: Int,
                      override val publicKey: String,
                      override val userId: Int,
                      override val active: Boolean): SshKey

interface SshKey {
    val publicKey: String
    val userId: Int
    val active: Boolean
}

data class CreateSshKeyModel(override val publicKey: String,
                             override val userId: Int,
                             override val active: Boolean = true): Validatable, SshKey {

    override fun validate(): ValidatableResult {
        val invalidMessages = mutableListOf<LocalizableString>()

        if (userId <= 0)         { invalidMessages.add(LocalizableString.InvalidUserId) }
        if (publicKey.isBlank()) { invalidMessages.add(LocalizableString.InvalidPublicKey) }

        return ValidatableResult(invalidMessages)
    }
}