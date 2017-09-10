package com.thewoolleyweb.grh.processor.cli

import com.thewoolleyweb.grh.args.Args
import com.thewoolleyweb.grh.config.GrhConfig
import com.thewoolleyweb.grh.output
import com.thewoolleyweb.grh.plan.createPlan

fun processWithCli(grhConfig: GrhConfig, args: Args) {
  val log = readLog(grhConfig.remote, grhConfig.branchToRevise)
  val plan = createPlan(grhConfig, log)
  val run = ::run
  executePlan(plan, args, run, ::output)
}
