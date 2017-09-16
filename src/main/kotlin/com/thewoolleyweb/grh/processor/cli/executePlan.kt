package com.thewoolleyweb.grh.processor.cli

import com.thewoolleyweb.grh.Output
import com.thewoolleyweb.grh.args.Args
import com.thewoolleyweb.grh.plan.Plan
import com.thewoolleyweb.grh.processhelper.Run

fun executePlan(plan: Plan, args: Args, run: Run, output: Output) {
  val commandLines = renderPlan(plan, args.skipPush)
  commandLines.forEach { commandLine ->
    output("$ $commandLine\n")
    if (!args.dryRun) {
      val commandOutput = run(commandLine).joinToString("\n")
      output(commandOutput)
    }
  }
  if (args.skipPush) {
    output("(skipping push)\n")
  }
}

