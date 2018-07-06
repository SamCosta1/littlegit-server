package com.littlegit.server.repo.testUtils

import org.mockito.Mockito
import org.mockito.stubbing.OngoingStubbing

fun <T>upon(call: T): OngoingStubbing<T> = Mockito.`when`(call)