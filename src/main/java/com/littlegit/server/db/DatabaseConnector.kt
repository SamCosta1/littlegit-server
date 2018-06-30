package com.littlegit.server.db

import com.littlegit.server.application.settings.SettingsProvider
import org.sql2o.Sql2o
import javax.inject.Inject


class DatabaseConnector @Inject constructor (settingsProvider: SettingsProvider) {
    private val sql2o: Sql2o

    init {
        val dbConfig = settingsProvider.settings.db
        Class.forName ("com.mysql.jdbc.Driver").newInstance()
        sql2o = Sql2o("jdbc:mysql://${dbConfig.host}:3306/${dbConfig.database}", dbConfig.user, dbConfig.password)
    }

    fun <T> executeSelect(sql: String, clazz: Class<T>, model: Any): List<T>? {
        return this.executeSelect(sql, clazz, null, model)
    }

    fun <T> executeSelect(sql: String, clazz: Class<T>, params: Map<String, Any>? = null): List<T>? {
        return this.executeSelect(sql, clazz, params, null)
    }

    private fun <T> executeSelect(sql: String, clazz: Class<T>, params: Map<String, Any>? = null, model: Any? = null): List<T>? {

        val con = sql2o.open()
        val query = con.createQuery(sql)

        params?.forEach{paramName, value ->
            query.addParameter(paramName, value)
        }

        if (model != null) {
            query.bind(model)
        }

        var result: List<T>? = null
        try {
            result =  query.executeAndFetch(clazz)

        } catch (e: Exception) {
            println(e)

        }



        return result
    }

}