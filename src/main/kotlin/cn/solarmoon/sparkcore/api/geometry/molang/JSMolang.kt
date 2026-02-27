package cn.solarmoon.sparkcore.api.geometry.molang

import org.graalvm.polyglot.Context
import org.graalvm.polyglot.HostAccess

private val context = ThreadLocal.withInitial {
    Context.newBuilder("js")
        .allowHostAccess(HostAccess.EXPLICIT)
        .build()
}

fun getMolangJSContext() = context.get()!!