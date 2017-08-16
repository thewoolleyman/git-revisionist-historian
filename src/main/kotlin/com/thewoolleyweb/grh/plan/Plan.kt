package com.thewoolleyweb.grh.plan

import com.thewoolleyweb.grh.git.Invocation

data class Plan(
  val steps: List<Invocation>
)
