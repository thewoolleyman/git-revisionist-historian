package com.thewoolleyweb.grh.config

data class IncrementCommit(
  val message: String,
  val tags: List<String>,
  val branches: List<String>
)
