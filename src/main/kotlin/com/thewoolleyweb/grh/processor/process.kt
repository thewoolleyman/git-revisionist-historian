package com.thewoolleyweb.grh.processor

import com.thewoolleyweb.grh.Args
import com.thewoolleyweb.grh.cmd.run
import com.thewoolleyweb.grh.config.load
import com.thewoolleyweb.grh.git.readLog
import com.thewoolleyweb.grh.plan.createPlan
import com.thewoolleyweb.grh.plan.renderPlan
import java.io.File
import java.io.InputStream

fun process(args: Args): Unit {
  println("Running with config file ${args.configFile}")
  val grhConfig = load(StringBuilder(readConfigFile(args.configFile)))
  val log = readLog(grhConfig.branchToRevise)
  val plan = createPlan(grhConfig, log)
  val invocations = renderPlan(plan)
  doLiveRun(invocations)
}

private fun doLiveRun(invocations: List<String>) {
  println("Invoking commands:")
  invocations.forEach { invocation ->
    runWithLogging(invocation)
  }
  runWithLogging("git push --tags")
  println("Finished invoking commands.")
}

private fun runWithLogging(invocation: String) {
  println("$ $invocation")
  print(run(invocation).joinToString("\n"))
}

private fun readConfigFile(configFile: String): String {
  val inputStream: InputStream = File(configFile).inputStream()
  return inputStream.bufferedReader().use { it.readText() }
}
