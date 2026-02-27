package cn.solarmoon.sparkcore.api.js

import arrow.core.raise.either
import org.graalvm.polyglot.Value

fun Value.safeAsDouble() = either {
    try {
        asDouble()
    } catch (e: Exception) {
        raise(e)
    }
}