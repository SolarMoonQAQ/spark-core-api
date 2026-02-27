package cn.solarmoon.sparkcore.api.extensionpack

import arrow.core.raise.either
import arrow.fx.coroutines.Resource
import arrow.fx.coroutines.resource
import arrow.fx.coroutines.resourceScope
import kotlinx.coroutines.coroutineScope
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import java.io.File
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path
import java.util.zip.ZipFile
import kotlin.collections.set

object ExtensionPackLoader {

    const val META_PATH = "meta.json"

    val JSON = Json {
        ignoreUnknownKeys = true
    }

    fun loadFile(path: Path) = resource {

    }

//    fun loadPack(path: Path) = resource {
//        path.use
//    }

}

typealias ExtensionPackReader = (File) -> Resource<ExtensionPack> // 包文件 -> 扩展包

@OptIn(ExperimentalSerializationApi::class)
val zipReader: ExtensionPackReader = { file ->
    resource {
        val zipFile = install(ZipFile(file))

        val meta = zipFile.getInputStream(zipFile.getEntry(ExtensionPackLoader.META_PATH)).use {
            ExtensionPackLoader.JSON.decodeFromStream<ExtensionPackMetaInfo>(it)
        }

        val contents = zipFile.entries().asSequence()
            .filter { !it.isDirectory }
            .associate { entry ->
                entry.name to zipFile.getInputStream(entry).use { it.readBytes() }
            }

        ExtensionPack(contents, meta)
    }
}


//@OptIn(ExperimentalSerializationApi::class)
//val directoryReader: ExtensionPackReader = { file ->
//    resource {
//        val meta = file.resolve(ExtensionPackLoader.META_PATH).use {
//            ExtensionPackLoader.JSON.decodeFromStream<ExtensionPackMetaInfo>(it.inputStream())
//        }
//
//        val contents = file.listFiles()!!.associate {
//            it.name to it.readBytes()
//        }
//
//        ExtensionPack(contents, meta)
//    }
//}