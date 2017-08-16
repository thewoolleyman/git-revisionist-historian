package com.thewoolleyweb.grh.processor

import com.thewoolleyweb.grh.Args
import com.thewoolleyweb.grh.config.load
import com.thewoolleyweb.grh.git.readLog
import com.thewoolleyweb.grh.plan.createPlan
import com.thewoolleyweb.grh.plan.renderPlan
import java.io.File
import java.io.InputStream

fun process(args: Args): Unit {
  val grhConfig = load(StringBuilder(readConfigFile(args.configFile)))
  val log = readLog(grhConfig.branchToRevise)
  val plan = createPlan(grhConfig, log)
  println(renderPlan(plan))
}

private fun readConfigFile(configFile: String): String {
  val inputStream: InputStream = File(configFile).inputStream()
  return inputStream.bufferedReader().use { it.readText() }
}
