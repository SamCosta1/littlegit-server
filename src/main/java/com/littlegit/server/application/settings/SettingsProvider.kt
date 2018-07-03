package com.littlegit.server.application.settings

import com.squareup.moshi.Moshi
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.stream.Collectors
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsProvider @Inject constructor(private val moshi: Moshi) {

    companion object {
        // HACK: Annoyingly we can't dependency inject into the exception mapper so has to access this statically
        var isDebugMode: Boolean = false
    }

    private var _settings: LittleGitSettings? = null

    val settings: LittleGitSettings
    get() {

        if (_settings == null) {
            _settings = parseSettings()
        }

        return _settings!!
    }

    private fun parseSettings(): LittleGitSettings {
        val reader = LittleGitSettings::class.java.getResourceAsStream("/settings.json")
        val content = BufferedReader(InputStreamReader(reader)).lines().collect(Collectors.joining("\n"))

        val parsed = moshi.adapter(LittleGitSettings::class.java).fromJson(content)
        isDebugMode = parsed?.isDebug ?: false

        return parsed ?: throw Exception("Settings not found or broken")
    }
}