package com.thewoolleyweb.grh.processor

import com.thewoolleyweb.grh.Args
import com.thewoolleyweb.grh.cmd.run
import com.thewoolleyweb.grh.config.load
import com.thewoolleyweb.grh.git.readLog
import com.thewoolleyweb.grh.plan.createPlan
import com.thewoolleyweb.grh.plan.renderPlan
import java.io.File
import java.nio.file.Paths



fun process(args: Args): Unit {
  println("Running with config file ${args.configFile}")
  val grhConfig = load(StringBuilder(readConfigFile(args.configFile)))
  val log = readLog(grhConfig.branchToRevise)
  val plan = createPlan(grhConfig, log)
  val commandLines = renderPlan(plan, args.skipPush)
  doRun(commandLines, args.dryRun)
}

private fun doRun(commandLines: List<String>, dryRun: Boolean) =
  if (dryRun) {
    doDryRun(commandLines)
  } else {
    doLiveRun(commandLines)
  }

private fun doDryRun(commandLines: List<String>) {
  println("Invoking dry run, these command would have been invoked:")
  commandLines.forEach { commandLine ->
    logCommandLines(commandLine)
  }
  println("Finished dry run.")
}

private fun doLiveRun(commandLines: List<String>) {
  println("Invoking commands:")
  commandLines.forEach { commandLine ->
    runWithLogging(commandLine)
  }
  println("Finished invoking commands.")
}

private fun logCommandLines(commandLines: String) = println("$ $commandLines")

private fun runWithLogging(commandLines: String) {
  logCommandLines(commandLines)
  print(run(commandLines).joinToString("\n"))
}

private fun readConfigFile(configFile: String): String {
  val currentPath = Paths.get("").toAbsolutePath().toString()
  println("Reading config file '$configFile, current path is $currentPath")
  return File(configFile).inputStream().bufferedReader().use { it.readText() }
}
