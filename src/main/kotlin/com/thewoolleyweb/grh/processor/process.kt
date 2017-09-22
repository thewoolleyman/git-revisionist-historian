package com.thewoolleyweb.grh.processor

import com.thewoolleyweb.grh.args.Args
import com.thewoolleyweb.grh.config.loadConfig
import com.thewoolleyweb.grh.processor.api.processWithApi
import com.thewoolleyweb.grh.processor.cli.processWithCli
import org.springframework.beans.factory.annotation.Autowired


fun process(args: Args) {
  val config = args.config
  println("Running with config $config")
  val grhConfig = loadConfig(config)
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
