package com.littlegit.server.model

enum class AuthRole(val code: Int) {
    Admin(1),
    OrganizationAdmin(2),
    BasicUser(3)
}