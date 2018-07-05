package com.littlegit.server.db

import com.littlegit.server.application.settings.SettingsProvider
import com.squareup.moshi.Moshi
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

    fun <T>get(key: String, clazz: Class<T>): T? {
        pool.resource.use { jedis->

            val json = jedis[key]

            if (json != null) {
                return moshi.adapter(clazz).fromJson(json)
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