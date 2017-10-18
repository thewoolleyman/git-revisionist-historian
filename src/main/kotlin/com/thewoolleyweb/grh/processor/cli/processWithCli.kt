package com.thewoolleyweb.grh.processor.cli

import com.thewoolleyweb.grh.args.Args
import com.thewoolleyweb.grh.config.GrhConfig
import com.thewoolleyweb.grh.output
import com.thewoolleyweb.grh.plan.createPlan
import com.thewoolleyweb.grh.processhelper.run

fun processWithCli(grhConfig: GrhConfig, args: Args) {
  val run = ::run
  val output = ::output
  val log = readLog(args, grhConfig.branchToRevise, run, output)
  val plan = createPlan(grhConfig, log)
  executePlan(plan, args, run, output)
}
