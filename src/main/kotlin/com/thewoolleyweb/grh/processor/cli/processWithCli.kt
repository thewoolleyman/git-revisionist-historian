package com.thewoolleyweb.grh.processor.cli

import com.thewoolleyweb.grh.args.Args
import com.thewoolleyweb.grh.config.GrhConfig
import com.thewoolleyweb.grh.output
import com.thewoolleyweb.grh.plan.createPlan
import com.thewoolleyweb.grh.processhelper.run

fun processWithCli(grhConfig: GrhConfig, args: Args) {
  val run = ::run
  val log = readLog(grhConfig.remote, grhConfig.branchToRevise, run)
  val plan = createPlan(grhConfig, log)
  executePlan(plan, args, run, ::output)
}
