package com.thewoolleyweb.grh.config

data class GrhConfig(
  val branchToRevise: String,
  val incrementCommits: List<IncrementCommit>
)
