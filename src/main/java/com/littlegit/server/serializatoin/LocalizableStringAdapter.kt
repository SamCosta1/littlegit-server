package com.littlegit.server.serializatoin

import com.littlegit.server.model.i18n.LocalizableString
import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson

class LocalizableStringAdapter {

    @ToJson
    fun toJson(localizableString: LocalizableString?): String? {
        return localizableString?.key ?: ""
    }
}