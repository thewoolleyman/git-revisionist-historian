package com.thewoolleyweb.grh.config

data class GrhConfig(
  val remote: String,
  val branchToRevise: String?,
  val incrementCommits: List<IncrementCommit>
)
