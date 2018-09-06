package com.littlegit.server.model.user

import com.littlegit.server.model.Validatable
import com.littlegit.server.model.ValidatableResult
import com.littlegit.server.model.i18n.LocalizableString

typealias SshKeyId = Int
data class SshKey(val id: Int,
                  val publicKey: String,
                  val userId: Int,
                  val active: Boolean)

data class CreateSshKeyModel(val publicKey: String, val userId: Int, val active: Boolean = true): Validatable {

    override fun validate(): ValidatableResult {
        val invalidMessages = mutableListOf<LocalizableString>()

        if (userId <= 0)         { invalidMessages.add(LocalizableString.InvalidUserId) }
        if (publicKey.isBlank()) { invalidMessages.add(LocalizableString.InvalidPublicKey) }

        return ValidatableResult(invalidMessages)
    }
}