package com.littlegit.server.db

import com.littlegit.server.application.settings.SettingsProvider
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPoolConfig
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Cache @Inject constructor(private val moshi: Moshi, settingsProvider: SettingsProvider) {

    private val pool: JedisPool

    init {
        val redisSettings = settingsProvider.settings.redis;
        pool = JedisPool(JedisPoolConfig(), redisSettings.host)
    }

    fun <T>getList(key: String, clazz: Class<T>): List<T>? {
        val type = Types.newParameterizedType(List::class.java, clazz)
        return get(key, moshi.adapter(type))
    }

    fun <T>get(key: String, clazz: Class<T>): T? = get(key, moshi.adapter(clazz))

    private fun <T>get(key: String, adapter: JsonAdapter<T>): T? {
        pool.resource.use { jedis->

            val json = jedis[key]

            if (json != null) {
                return adapter.fromJson(json)
            }
        }

        return null
    }

    fun get(key: String): String? {
        pool.resource.use { jedis ->
            return jedis[key]
        }
    }

    fun set(key: String, obj: Any) {
        val json = moshi.adapter(obj.javaClass).toJson(obj)

        pool.resource.use { jedis ->
            jedis[key] = json
        }
    }

    fun <T: Any>setList(key: String, obj: List<T>, clazz: Class<T>) {
        val type = Types.newParameterizedType(List::class.java, clazz)

        val json = moshi.adapter<List<T>>(type).toJson(obj)

        pool.resource.use { jedis ->
            jedis[key] = json
        }
    }

    fun <T: Any>retrieveList(key: String, clazz: Class<T>, secondaryProvider: () -> List<T>?): List<T>? {
        // Try retrieve the object from cache
        val cached = this.getList(key, clazz)

        if (cached != null) {
            return cached
        }

        // If it's not there, invoke the secondary provider (i.e. access from db)
        val obj = secondaryProvider.invoke()
        obj?.let { setList(key, it, clazz) }

        return obj
    }

    fun <T: Any>retrieve(key: String, clazz: Class<T>, secondaryProvider: () -> T?): T? {
        // Try retrieve the object from cache
        val cached = this.get(key, clazz)

        if (cached != null) {
            return cached
        }

        // If it's not there, invoke the secondary provider (i.e. access from db)
        val obj = secondaryProvider.invoke()
        obj?.let { set(key, it) }

        return obj
    }


    fun delete(key: String) {
        pool.resource.use { jedis ->
            jedis.del(key)
        }
    }
}