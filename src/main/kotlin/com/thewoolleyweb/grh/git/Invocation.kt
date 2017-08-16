package com.thewoolleyweb.grh.git

data class Invocation(
  val command: Command,
  // TODO: can these be made non-optional?  Perhaps a different data class for Commands which don't require a sha/tag?
  val sha: String? = null,
  val tag: String? = null
)
