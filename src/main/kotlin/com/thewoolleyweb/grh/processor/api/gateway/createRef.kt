package com.thewoolleyweb.grh.processor.api.gateway

import com.github.kittinunf.fuel.httpPost
import com.thewoolleyweb.grh.processor.api.Repo

typealias CreateRef = (String, Repo, String, String) -> FuelReturnValue

fun createRef(endpoint: String, repo: Repo, ref: String, sha: String):
  FuelReturnValue {
  val refSuffix = "tags/$ref"
  val baseRefsPath = "/repos/${repo.owner}/${repo.name}/git/refs"
  val postBody = """
    |{
    |  "ref": "refs/$refSuffix",
    |  "sha": "$sha"
    |}
    """.trimMargin().replace("\n", "")

  return "${endpoint}${baseRefsPath}"
    .httpPost(listOf())
    .authenticate("", System.getenv("GITHUB_PERSONAL_ACCESS_TOKEN"))
    .header(mapOf("Content-Type" to "application/json"))
    .body(postBody)
    .responseString()
}

