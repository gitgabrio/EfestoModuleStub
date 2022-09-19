//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//
package org.kie.efesto.kafka.example.client.utils

import org.slf4j.LoggerFactory
import java.io.File
import java.net.URISyntaxException
import java.nio.file.Paths
import java.util.*

object FileUtil {
    private val logger = LoggerFactory.getLogger(FileUtil::class.java.name)
    @Throws(URISyntaxException::class)
    fun getFileFromFileName(fileName: String, classLoader: ClassLoader): Optional<File> {
        logger.debug("getFileByFileNameFromClassloader {} {}", fileName, classLoader)
        val url = classLoader.getResource(fileName)
        return if (url != null) {
            logger.debug("url {}", url)
            Optional.of(Paths.get(url.toURI()).toFile())
        } else {
            val file = File(fileName)
            logger.debug("file {}", file)
            logger.debug("file.exists() {}", file.exists())
            if (file.exists()) Optional.of(file) else Optional.empty()
        }
    }
}