package com.thewoolleyweb.grh.processor.cli

import java.io.File
import java.util.concurrent.TimeUnit

typealias Run = (String) -> List<String>

// TODO: This is used by other packages like acceptance tests, should be moved up
fun run(commandLine: String): List<String> {
  val timeout: Long = 30
  val parts = commandLine.split("\\s".toRegex())
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
    val message = "\n\nError processing command line (exit value $exitValue): '$commandLine'\n\n" +
      "Error output:\n$stderrLines\n"
    throw RuntimeException(message)
  }

  return stdout
}
