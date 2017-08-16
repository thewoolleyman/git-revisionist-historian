package com.thewoolleyweb.grh.git

data class Invocation(
  val command: Command,
  val sha: String,
  val tag: String
)
