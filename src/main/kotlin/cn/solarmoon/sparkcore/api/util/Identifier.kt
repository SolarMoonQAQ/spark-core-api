package cn.solarmoon.sparkcore.api.util

@JvmInline
value class Identifier private constructor(val raw: String) {
    constructor(namespace: String, path: String): this("$namespace:$path")

    val namespace get() = raw.substringBefore(':', "default")
    val path get() = raw.substringAfter(':')
}
