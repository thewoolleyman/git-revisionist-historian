package com.thewoolleyweb.grh.processor.api.gateway

import com.github.kittinunf.fuel.httpPatch
import com.thewoolleyweb.grh.processor.api.Repo

typealias ReplaceRef = (String, Repo, String, String) -> FuelReturnValue

fun replaceRef(endpoint: String, repo: Repo, ref: String, sha: String):
  FuelReturnValue {
  val refSuffix = "tags/$ref"
  val baseRefsPath = "/repos/${repo.owner}/${repo.name}/git/refs"
  val patchPath = "${baseRefsPath}/$refSuffix"
  val patchBody = """
    |{
    |  "force": true,
    |  "sha": "$sha"
    |}
    """.trimMargin().replace("\n", "")

  return "${endpoint}${patchPath}"
    .httpPatch(listOf())
    .authenticate("", System.getenv("GITHUB_PERSONAL_ACCESS_TOKEN"))
    .header(mapOf("Content-Type" to "application/json"))
    .body(patchBody)
    .responseString()
}

