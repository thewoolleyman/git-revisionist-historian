package com.thewoolleyweb.grh.plan

import com.thewoolleyweb.grh.git.Command

// Should this live in git package instead???
fun renderPlan(plan: Plan, skipPush: Boolean): List<String> {
  val commandLines: ArrayList<String> = ArrayList()
  plan.steps.forEach { step ->
    when (step.command) {
      Command.TAG -> commandLines.add("git tag --force ${step.tag} ${step.sha}")
      Command.PUSH_TAGS -> if (!skipPush) commandLines.add("git push --force --tags")
    }
  }
  return commandLines
}
