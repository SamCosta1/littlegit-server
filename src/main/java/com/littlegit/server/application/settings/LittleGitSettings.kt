package com.littlegit.server.application.settings

import com.littlegit.server.application.MoshMessageBodyHandler
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.*
import java.util.stream.Collectors
import javax.ws.rs.core.MediaType

class LittleGitSettings(val db: DbConfig) {

    companion object {
        const val VARIANT_VAR_NAME = "LITTLEGIT_RUNTIME_VARIANT"
        @JvmStatic fun parseSettings(): LittleGitSettings {
            val reader = LittleGitSettings::class.java.getResourceAsStream("/settings.json")
            val content = BufferedReader(InputStreamReader(reader)).lines().collect(Collectors.joining("\n"))

            val parsed = MoshMessageBodyHandler().moshi.adapter(LittleGitSettings::class.java).fromJson(content)

            return parsed ?: throw Exception("Settings not found or broken")

        }
    }

}