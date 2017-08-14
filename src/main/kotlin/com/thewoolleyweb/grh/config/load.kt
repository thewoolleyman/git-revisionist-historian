package com.thewoolleyweb.grh.config

import com.beust.klaxon.*

fun load(rawValue: StringBuilder): GrhConfig {
    val configJsonObject = Parser().parse(rawValue) as JsonObject
    val incrementCommitsJsonArray = configJsonObject.array<JsonObject>("incrementCommits") as JsonArray<JsonObject>
    val incrementCommits: List<IncrementCommit>? = incrementCommitsJsonArray?.map {
        IncrementCommit(
                message = it.string("message") ?: throw IllegalArgumentException("incrementCommit message expected")
        )
    }
    return GrhConfig(
        remote = configJsonObject.string("remote") ?: "origin",
        incrementCommits = incrementCommits ?: arrayListOf()
    )
}