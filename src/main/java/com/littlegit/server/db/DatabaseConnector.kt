package com.littlegit.server.db

import com.littlegit.server.application.settings.SettingsProvider
import com.littlegit.server.model.auth.TokenType
import com.littlegit.server.model.user.AuthRole
import com.littlegit.server.serializatoin.AuthRoleAdapter
import com.littlegit.server.serializatoin.OffsetDateTimeAdapter
import com.littlegit.server.serializatoin.TokenTypeAdapter
import org.sql2o.Query
import org.sql2o.Sql2o
import org.sql2o.converters.Converter
import org.sql2o.quirks.NoQuirks
import java.math.BigInteger
import java.time.OffsetDateTime
import javax.inject.Inject


class DatabaseConnector @Inject constructor (settingsProvider: SettingsProvider) {
    private val sql2o: Sql2o

    init {
        val dbConfig = settingsProvider.settings.db
        Class.forName ("com.mysql.jdbc.Driver").newInstance()
        sql2o = Sql2o("jdbc:mysql://${dbConfig.host}:3306/${dbConfig.database}",
                        dbConfig.user,
                        dbConfig.password,
                        NoQuirks(getAdapters()))
    }

    fun <T> executeSelect(sql: String, clazz: Class<T>, model: Any): List<T>? {
        return this.executeSelect(sql, clazz, null, model)
    }

    fun <T> executeSelect(sql: String, clazz: Class<T>, params: Map<String, Any>? = null): List<T>? {
        return this.executeSelect(sql, clazz, params, null)
    }

    fun <T> executeScalar(sql: String, clazz: Class<T>, model: Any): List<T>? {
        return this.executeScalar(sql, clazz, null, model)
    }

    fun <T> executeScalar(sql: String, clazz: Class<T>, params: Map<String, Any>? = null): List<T>? {
        return this.executeScalar(sql, clazz, params = params, model = null)
    }

    private fun <T> executeScalar(sql: String, clazz: Class<T>, params: Map<String, Any>?, model: Any? = null): List<T>? {
        val query = this.prepareQuery(sql, params, model)

        val result =  query.executeScalarList(clazz)
        query.close()
        query.connection.close()
        return result
    }

    fun executeDelete(sql: String, params: Map<String, Any>? = null, model: Any? = null) {
        val query = this.prepareQuery(sql, params, model)

        query.executeUpdate()
        query.close()
        query.connection.close()
    }

    fun executeInsert(sql: String, params: Map<String, Any>? = null, model: Any? = null): Int {
        val query = this.prepareQuery(sql, params, model)

        val result =  (query.executeUpdate().key as BigInteger).toInt()
        query.close()
        query.connection.close()

        return result
    }

    private fun <T> executeSelect(sql: String, clazz: Class<T>, params: Map<String, Any>? = null, model: Any? = null): List<T>? {

        val query = this.prepareQuery(sql, params, model)

        val result =  query.executeAndFetch(clazz)
        query.close()
        query.connection.close()

        return result
    }

    private fun prepareQuery(sql: String, params: Map<String, Any>? = null, model: Any? = null): Query {
        val con = sql2o.open()
        val query = con.createQuery(sql)
        params?.forEach{paramName, value ->
            query.addParameter(paramName, value)
        }
        if (model != null) {
            query.bind(model)
        }

        return query
    }

    private fun getAdapters(): Map<Class<out Any>, Converter<out Any>> =  mapOf(
            AuthRole::class.java  to AuthRoleAdapter(),
            TokenType::class.java to TokenTypeAdapter(),
            OffsetDateTime::class.java to OffsetDateTimeAdapter()
    )

}