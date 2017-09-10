package com.thewoolleyweb.grh.processor.api

import com.beust.klaxon.*
import com.thewoolleyweb.grh.git.Commit
import java.io.StringReader

fun parseRefHistory(result: String): List<Commit> {
  val configJsonObject = Parser().parse(StringReader(result)) as JsonObject
  val history = configJsonObject.
    obj("data")?.
    obj("repository")?.
    obj("ref")?.
    obj("target")?.
    obj("history")

  val commitsJsonArray: JsonArray<JsonObject> = history?.array<JsonObject>("edges")
    ?: throw IllegalArgumentException("No history found for repo in GraphQL result from API: \n" + result)
  val commits: List<Commit>? = loadCommits(commitsJsonArray, result)
  return commits ?: arrayListOf()
}

private fun loadCommits(commitsJsonArray: JsonArray<JsonObject>, result: String)
  : List<Commit> = commitsJsonArray.map {
  val node = it.obj("node")
  Commit(
    sha = node?.string("oid") ?: throw IllegalArgumentException("oid field not found in GraphQL result from API: \n" + result),
    message = node.string("message") ?: throw IllegalArgumentException("message field not found in GraphQL result from API: \n" + result)
  )
}
