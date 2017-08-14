package com.thewoolleyweb.grh.config

data class GrhConfig(
  val remote: String,
  val incrementCommits: List<IncrementCommit>
)
