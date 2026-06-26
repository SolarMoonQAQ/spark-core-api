package cn.solarmoon.sparkcore.api

import java.io.File
import java.io.FileNotFoundException
import java.nio.file.Files
import java.nio.file.StandardCopyOption

object NativeLoader {

    fun loadLibrary() {
        val osName = System.getProperty("os.name").lowercase()
        val libName = when {
            osName.contains("win") -> "spark_core_geometry.dll"
            osName.contains("mac") -> "spark_core_geometry.dylib"
            else -> "spark_core_geometry.so"
        }

        try {
            // 1. 从 resources (classpath) 中读取 DLL 文件流
            val inputStream = NativeLoader::class.java.getResourceAsStream("/$libName")
                ?: throw FileNotFoundException("无法在 resources 中找到动态库: $libName")

            // 2. 在操作系统的临时目录创建一个前缀为 spark_core 的临时文件
            val tempFile = File.createTempFile("spark_core_", "_$libName")

            // JVM 退出时自动删除这个临时文件，不留垃圾
            tempFile.deleteOnExit()

            // 3. 把流拷贝进这个物理文件
            inputStream.use { input ->
                Files.copy(input, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
            }

            // 4. 神级操作：使用操作系统的绝对物理路径加载 DLL
            System.load(tempFile.absolutePath)

            println("[SparkCore] 成功加载高性能 Rust 渲染核心: ${tempFile.absolutePath}")
        } catch (e: Exception) {
            System.err.println("[SparkCore] 致命错误：无法加载 Rust 动态库！")
            e.printStackTrace()
            throw RuntimeException(e)
        }
    }
}