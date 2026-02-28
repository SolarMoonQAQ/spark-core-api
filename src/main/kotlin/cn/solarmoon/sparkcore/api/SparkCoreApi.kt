package cn.solarmoon.sparkcore.api

import org.slf4j.LoggerFactory

object SparkCoreApi {

    val LOGGER = LoggerFactory.getLogger("星火核心")

    fun logger(name: String) = LoggerFactory.getLogger("星火核心/$name")

}