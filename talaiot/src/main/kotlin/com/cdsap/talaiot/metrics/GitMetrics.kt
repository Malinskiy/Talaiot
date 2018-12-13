package com.cdsap.talaiot.metrics

import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.IllegalStateException

class GitMetrics : Metrics {
    override fun get(): Map<String, String> {
        val runtime = Runtime.getRuntime()

        try {
            val bufferBranch = BufferedReader(
                InputStreamReader(runtime.exec("git rev-parse --abbrev-ref HEAD").inputStream)
            )

            val bufferUserName = BufferedReader(
                InputStreamReader(runtime.exec("git config user-name").inputStream)
            )
            val user = bufferUserName.readLine()
            val branch = bufferBranch.readLine()
            return mutableMapOf("gitUser" to user, "branch" to branch)
        } catch (e: IllegalStateException) {
            throw IllegalArgumentException(
                "Error getting information about the Git Repository. In case " +
                        "you are not in a Git Repository you can disable the metrics of Git with " +
                        "talaiot{" +
                        "  gitMetrics=false" +
                        "}"
            )
        }

    }
}