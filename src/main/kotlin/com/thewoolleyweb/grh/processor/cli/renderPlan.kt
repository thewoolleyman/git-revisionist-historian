package com.thewoolleyweb.grh.processor.cli

import com.thewoolleyweb.grh.git.Command
import com.thewoolleyweb.grh.plan.Plan

fun renderPlan(plan: Plan, skipPush: Boolean): List<String> {
  val commandLines: ArrayList<String> = ArrayList()
  plan.actions.forEach { action ->
    when (action.command) {
      Command.TAG -> commandLines.add("git tag --force ${action.refname} ${action.sha}")
    }
  }
  if (!skipPush) {
    commandLines.add("git push --force --tags")
  }

  return commandLines
}
