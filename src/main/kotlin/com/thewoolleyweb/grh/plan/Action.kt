package com.thewoolleyweb.grh.plan

import com.thewoolleyweb.grh.git.Command

data class Action(
  val command: Command,
  val sha: String,
  val refname: String
)
