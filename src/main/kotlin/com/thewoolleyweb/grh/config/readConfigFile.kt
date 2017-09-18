package com.thewoolleyweb.grh.config

import com.xenomachina.argparser.SystemExitException
import java.io.File
import java.io.FileNotFoundException
import java.nio.file.Paths

fun readConfigFile(configFile: String): String {
  val currentPath = Paths.get("").toAbsolutePath().toString()
  println("Reading config file '$configFile, current path is $currentPath")
  val bufferedReader = try {
    File(configFile)
      .inputStream()
      .bufferedReader()
  } catch (e: FileNotFoundException) {
    throw SystemExitException(e.toString(), 1)
  }
  return bufferedReader
    .use {
      it.readText()
    }
}
