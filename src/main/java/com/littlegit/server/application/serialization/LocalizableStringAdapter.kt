package com.littlegit.server.application.serialization

import com.littlegit.server.model.i18n.LocalizableString
import com.squareup.moshi.ToJson

class LocalizableStringAdapter {

    @ToJson
    fun toJson(localizableString: LocalizableString?): String? {
        return localizableString?.key ?: ""
    }
}