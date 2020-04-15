package info.akang.covid

import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.URL

interface Retriever<T> {
    fun retrieve(source: T): String
}

class HttpRetriever: Retriever<URL> {
    override fun retrieve(source: URL): String {
        val sb = StringBuilder()

        with(source.openConnection() as HttpURLConnection) {
            inputStream.bufferedReader().use {
                it.lines().forEach { line ->
                    sb.append(line).append("\n")
                }
            }
        }

        return sb.toString()
    }
}

class FileRetriever: Retriever<String> {
    override fun retrieve(source: String): String {
        return FileRetriever::class.java.getResource(source).readText()
    }
}
