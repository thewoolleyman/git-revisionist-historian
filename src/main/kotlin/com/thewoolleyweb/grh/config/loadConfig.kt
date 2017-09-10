package com.thewoolleyweb.grh.config

import java.io.File
import java.nio.file.Paths

fun loadConfig(configFile: String) = parseConfig(StringBuilder(readConfigFile(configFile)))

fun readConfigFile(configFile: String): String {
  val currentPath = Paths.get("").toAbsolutePath().toString()
  println("Reading config file '$configFile, current path is $currentPath")
  return File(configFile).inputStream().bufferedReader().use { it.readText() }
}
