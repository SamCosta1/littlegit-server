package com.littlegit.server.application

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okio.Okio
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.lang.reflect.Type
import javax.ws.rs.Consumes
import javax.ws.rs.Produces
import javax.ws.rs.WebApplicationException
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.MultivaluedMap
import javax.ws.rs.core.NoContentException
import javax.ws.rs.ext.MessageBodyReader
import javax.ws.rs.ext.MessageBodyWriter
import javax.ws.rs.ext.Provider

@Provider
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class MoshMessageBodyHandler : MessageBodyWriter<Any>, MessageBodyReader<Any> {

    var moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    override fun isReadable(type: Class<*>, genericType: Type, annotations: Array<Annotation>,
                            mediaType: MediaType): Boolean {
        return mediaType.isCompatible(MediaType.APPLICATION_JSON_TYPE)
    }

    @Throws(IOException::class, WebApplicationException::class)
    override fun readFrom(type: Class<Any>, genericType: Type, annotations: Array<Annotation>,
                          mediaType: MediaType, httpHeaders: MultivaluedMap<String, String>, entityStream: InputStream): Any? {
        val adapter = moshi.adapter<Any>(genericType)
        val source = Okio.buffer(Okio.source(entityStream))
        if (!source.request(1)) {
            throw NoContentException("Stream is empty")
        }

        try {
            return adapter.fromJson(source)
        } catch (e: Exception) {
            println(e)
        }

        return null
        // Note: we do not close the InputStream per the interface documentation.
    }


    override fun isWriteable(type: Class<*>, genericType: Type, annotations: Array<Annotation>,
                             mediaType: MediaType): Boolean {
        return mediaType.isCompatible(MediaType.APPLICATION_JSON_TYPE)
    }

    override fun getSize(o: Any, type: Class<*>, genericType: Type, annotations: Array<Annotation>,
                         mediaType: MediaType): Long {
        return -1
    }

    @Throws(IOException::class, WebApplicationException::class)
    override fun writeTo(o: Any, type: Class<*>, genericType: Type, annotations: Array<Annotation>,
                         mediaType: MediaType, httpHeaders: MultivaluedMap<String, Any>, entityStream: OutputStream) {
        val adapter = moshi.adapter<Any>(genericType)
        val sink = Okio.buffer(Okio.sink(entityStream))
        adapter.toJson(sink, o)
        sink.emit()
        // Note: we do not close the OutputStream per the interface documentation.
    }
}
