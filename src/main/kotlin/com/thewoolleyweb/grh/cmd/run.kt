package com.thewoolleyweb.grh.cmd

import java.io.File
import java.util.concurrent.TimeUnit

fun run(cmd: String, timeout: Long = 5): List<String> {
  val parts = cmd.split("\\s".toRegex())
  val proc = ProcessBuilder(*parts.toTypedArray())
    .directory(File("."))
    .redirectOutput(ProcessBuilder.Redirect.PIPE)
    .redirectError(ProcessBuilder.Redirect.PIPE)
    .start()

  proc.waitFor(timeout, TimeUnit.SECONDS)

  // TODO: How to chronologically interleave stdout and stderr, as well as all other
  // goals listed in https://github.com/thewoolleyman/process_helper/blob/master/README.md ???
  // See https://www.pivotaltracker.com/story/show/150247329
  val stdout = proc
    .inputStream
    .bufferedReader()
    .useLines { it.toList() }

  val stderr = proc
    .errorStream
    .bufferedReader()
    .useLines { it.toList() }

  val exitValue = proc.exitValue()
  if (exitValue != 0) {
    val stderrLines = stderr.joinToString("\n")
    val message = "\n\nError processing command (exit value $exitValue): '$cmd'\n\n" +
      "Error output:\n$stderrLines\n"
    throw RuntimeException(message)
  }

  return stdout
}
