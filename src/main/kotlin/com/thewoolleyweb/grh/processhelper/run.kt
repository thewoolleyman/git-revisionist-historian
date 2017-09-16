package com.thewoolleyweb.grh.processhelper

import java.io.File
import java.util.concurrent.TimeUnit

typealias Run = (String) -> List<String>
typealias RunInDir = (String, String) -> List<String>

// TODO: Can this extra wrapper method be avoided with a second default/optional param?
fun run(commandLine: String): List<String> {
  return runInDir(commandLine, ".")
}

fun runInDir(commandLine: String, directory: String): List<String> {
  val timeout: Long = 30
  val parts = commandLine.split("\\s".toRegex())
  val proc = ProcessBuilder(*parts.toTypedArray())
    .directory(File(directory))
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
