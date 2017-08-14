package com.thewoolleyweb.grh.config

import com.beust.klaxon.*

fun load(rawValue: StringBuilder): GrhConfig {
  val configJsonObject = Parser().parse(rawValue) as JsonObject
  val incrementCommitsJsonArray = configJsonObject.array<JsonObject>("incrementCommits") as JsonArray<JsonObject>
  val incrementCommits: List<IncrementCommit>? = loadIncrementCommits(incrementCommitsJsonArray)
  return GrhConfig(
    remote = configJsonObject.string("remote") ?: "origin",
    branchToRevise = configJsonObject.string("branchToRevise") ?: throw IllegalArgumentException("branchToRevise is required"),
    incrementCommits = incrementCommits ?: arrayListOf()
  )
}

private fun loadIncrementCommits(incrementCommitsJsonArray: JsonArray<JsonObject>): List<IncrementCommit> {
  return incrementCommitsJsonArray.map {
    IncrementCommit(
      message = it.string("message") ?: throw IllegalArgumentException("incrementCommit message is required"),
      tags = it.array<String>("tags")?.map { it } ?: arrayListOf(),
      branches = it.array<String>("branches")?.map { it } ?: arrayListOf()
    )
  }
}
