package com.thewoolleyweb.grh.plan

// Should this live in git package instead???
fun renderPlan(plan: Plan): List<String> {
  return plan.steps.map { step ->
    "git tag --force ${step.tag} ${step.sha}"
  }
}
