package cn.solarmoon.sparkcore.api.extensionpack

import arrow.core.getOrElse
import arrow.core.raise.catch
import arrow.core.raise.either
import arrow.core.raise.ensure
import arrow.fx.coroutines.parMap
import arrow.fx.coroutines.resource
import arrow.fx.coroutines.resourceScope
import cn.solarmoon.sparkcore.api.SparkCoreApi
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.exists
import kotlin.io.path.inputStream
import kotlin.io.path.isDirectory
import kotlin.io.path.isRegularFile
import kotlin.io.path.name
import kotlin.io.path.readBytes
import kotlin.sequences.associate
import kotlin.streams.asSequence
import kotlin.use

object ExtensionPackReader {

    const val META_PATH = "meta.json"

    val JSON = Json {
        ignoreUnknownKeys = true
    }

    val LOGGER = SparkCoreApi.logger("拓展包读取")

    /**
     * 从给定文件路径读取拓展包（大多数情况下不区分文件格式，只要内部结构符合拓展包需求）
     */
    @OptIn(ExperimentalSerializationApi::class)
    suspend fun readPack(path: Path) = either {
        resourceScope {
            // 区分文件夹和zip
            val rootPath = if (path.isDirectory()) path else install(FileSystems.newFileSystem(path)).getPath("/")

            // 确认meta
            val metaPath = rootPath.resolve(META_PATH)
            ensure(metaPath.exists()) { ExtensionPackReadError.MissingMeta }
            val meta = catch(
                {
                    metaPath.inputStream().use {
                        JSON.decodeFromStream<ExtensionPackMetaInfo>(it)
                    }
                },
                {
                    raise(ExtensionPackReadError.InvalidMeta(metaPath, it))
                }
            )

            // 读取内容
            val contents = Files.walk(rootPath).use { stream ->
                stream.filter { it.isRegularFile() && it.name != META_PATH }
                    .asSequence()
                    .associate { file ->
                        val key = rootPath.relativize(file).toString().replace('\\', '/')
                        key to file.readBytes()
                    }
            }

            ExtensionPack(meta, contents)
        }
    }

}

sealed interface ExtensionPackReadError {
    object MissingMeta: ExtensionPackReadError
    data class InvalidMeta(val path: Path, val cause: Throwable): ExtensionPackReadError
}

/**
 * 高性能并行扫描目录以读取拓展包组
 */
suspend fun ExtensionPackReader.loadAllPacksParallelFromDirectory(directory: Path) = either {
    ensure(directory.exists() && directory.isDirectory()) {
        LOGGER.error("目录不存在或者不是目录")
    }

    val paths = Files.list(directory).use { it.toList() }

    paths.parMap(concurrency = Runtime.getRuntime().availableProcessors()) { path ->
        readPack(path).onLeft { error ->
            LOGGER.error("无法读取拓展包 ${path.name}，错误：${error}")
        }
    }.mapNotNull { it.getOrNull() }
}

