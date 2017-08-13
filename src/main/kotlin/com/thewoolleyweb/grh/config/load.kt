package com.thewoolleyweb.grh.config

import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import com.beust.klaxon.string

fun load(rawValue: StringBuilder): GrhConfig {
    val jsonObject = Parser().parse(rawValue) as JsonObject
    return GrhConfig(
        remote = jsonObject.string("remote") ?: "origin"
    )
}