package com.thewoolleyweb.grh.config

import com.beust.klaxon.*

fun parseConfig(rawValue: StringBuilder): GrhConfig {
  val configJsonObject = Parser().parse(rawValue) as JsonObject
  val incrementCommitsJsonArray: JsonArray<JsonObject> = configJsonObject.array<JsonObject>("incrementCommits")
    ?: throw IllegalArgumentException("branchToRevise is required")
  val incrementCommits: List<IncrementCommit>? = loadIncrementCommits(incrementCommitsJsonArray)
  return GrhConfig(
    branchToRevise = configJsonObject.string("branchToRevise")
      ?: throw IllegalArgumentException("branchToRevise is required"),
    incrementCommits = incrementCommits ?: arrayListOf()
  )
}

private fun loadIncrementCommits(incrementCommitsJsonArray: JsonArray<JsonObject>)
  : List<IncrementCommit> = incrementCommitsJsonArray.map {
  IncrementCommit(
    message = it.string("message") ?: throw IllegalArgumentException("incrementCommit message is required"),
    tags = it.array<String>("tags")?.map { it } ?: arrayListOf()
  )
}
