package com.littlegit.server.application.settings

import com.squareup.moshi.Moshi
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.stream.Collectors
import javax.inject.Inject
import javax.inject.Singleton
import java.io.File
import java.util.*


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

    @Synchronized
    private fun parseSettings(): LittleGitSettings {
        if (_settings != null) {
            return _settings!!
        }

        val mainSettingsJson = this.getSettingsJson()
        val debugSettings = this.getDebugSettings()

        if (mainSettingsJson.isNullOrBlank() && debugSettings.isNullOrBlank()) {
            throw Error("Settings not found")
        }

        val settingsToUse = mainSettingsJson ?: debugSettings
        val parsed = moshi.adapter(LittleGitSettings::class.java).fromJson(settingsToUse!!)
        isDebugMode = parsed?.isDebug ?: false

        return parsed ?: throw Error("Settings not found or broken")
    }

    private fun getDebugSettings(): String? {
        val reader = LittleGitSettings::class.java.getResourceAsStream("/settings.json")
        return BufferedReader(InputStreamReader(reader)).lines().collect(Collectors.joining("\n"))
    }

    private fun getSettingsJson(): String? {
        var scanner: Scanner? = null
        var json: String? = null

        try {
            scanner = Scanner(File("${System.getProperty("user.home")}/littlegit-settings.json"))
            json = scanner.useDelimiter("\\A").next()
        } catch  (e: Exception) {
            // This is valid, means we're using debug settings
        } finally {
            scanner?.close()
        }

        return json
    }
}