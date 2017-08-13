package com.thewoolleyweb.grh.cmd

import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit

fun run(cmd: String, timeout: Long = 5): List<String> {
    try {
        val parts = cmd.split("\\s".toRegex())
        val proc = ProcessBuilder(*parts.toTypedArray())
                .directory(File("."))
                .redirectOutput(ProcessBuilder.Redirect.PIPE)
                .redirectError(ProcessBuilder.Redirect.PIPE)
                .start()

        proc.waitFor(timeout, TimeUnit.SECONDS)
        return proc
                .inputStream
                .bufferedReader()
                .useLines {  it.toList()}
    } catch(e: IOException) {
        // TODO: return custom exception
        e.printStackTrace()
        return arrayListOf("ERROR!")
    }
}