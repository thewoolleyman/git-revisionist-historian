package com.thewoolleyweb.grh.processor

import com.thewoolleyweb.grh.args.Args
import com.thewoolleyweb.grh.config.loadConfig
import com.thewoolleyweb.grh.processor.api.processWithApi
import com.thewoolleyweb.grh.processor.cli.processWithCli


fun process(args: Args) {
  val configFile = args.configFile
  println("Running with config file $configFile")
  val grhConfig = loadConfig(configFile)
  val processor = if (args.processor == "cli") {
    ::processWithCli
  } else {
    ::processWithApi
  }
  if (args.dryRun) {
    println("Starting dry run, these commands would have been performed:")
    processor(grhConfig, args)
    println("Finished dry run.")
  } else {
    println("Starting run:")
    processor(grhConfig, args)
    println("Finished run.")
  }
}
