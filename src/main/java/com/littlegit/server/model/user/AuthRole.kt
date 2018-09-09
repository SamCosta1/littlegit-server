package com.littlegit.server.model.user

enum class AuthRole(val code: Int) {
    Admin(1),
    OrganizationAdmin(2),
    BasicUser(3),
    GitServer(4);

    companion object {
        private val map = AuthRole.values().associateBy(AuthRole::code)
        fun fromInt(type: Int) = map[type]
    }

}