package com.moblino.countynews.common.ext

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun <T> withIoDispatcher(operation: suspend () -> T): T =
    withContext(Dispatchers.IO) { operation() }